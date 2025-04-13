package icu.takeneko.appwebterminal.resource

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.apache.commons.codec.binary.Hex
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.notExists
import kotlin.io.path.writeText

@OptIn(ExperimentalSerializationApi::class)
private val Json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    prettyPrintIndent = "  "
}

object CacheProvider {
    private val cacheRoot = Path("./.cache")
    private val cacheFileMetaRoot = cacheRoot / "meta"
    private val cacheDownloadRoot = cacheRoot / "download"
    private val logger = LoggerFactory.getLogger("Cache")
    private val caches = mutableMapOf<String, FileMetadata>()
    private val httpClient = HttpClient.newHttpClient()

    @OptIn(ExperimentalSerializationApi::class)
    fun init() {
        logger.info("Building file cache.")
        listOf(cacheRoot, cacheDownloadRoot, cacheFileMetaRoot).forEach {
            if (it.notExists()) {
                it.createDirectories()
            }
        }
        cacheDownloadRoot.listDirectoryEntries().forEach {
            val fileNameWithExt = it.name
            if ((cacheFileMetaRoot / "$fileNameWithExt.json").notExists()) {
                logger.warn("Cannot find associated file metadata of $fileNameWithExt at $cacheFileMetaRoot.")
                it.deleteIfExists()
            }
        }
        cacheFileMetaRoot.listDirectoryEntries().forEach {
            val fileNameWithExt = it.name
            if ((cacheDownloadRoot / fileNameWithExt.removeSuffix(".json")).notExists()) {
                logger.warn("Cannot find associated file of $fileNameWithExt at $cacheDownloadRoot.")
                it.deleteIfExists()
                return@forEach
            }
            try {
                it.inputStream().use { ins ->
                    icu.takeneko.appwebterminal.resource.Json.decodeFromStream<FileMetadata>(ins).apply {
                        caches[fileName] = this
                    }
                }
            } catch (e: Exception) {
                logger.warn("Cannot load file metadata from $it, caused by $e")
                it.deleteIfExists()
            }
        }

    }

    fun downloadFile(meta: FileMetadata): Path {
        val expectFilePath = cacheDownloadRoot / meta.fileName
        if (meta.fileName in caches && expectFilePath.exists()) {
            val cachedMetadata = caches[meta.fileName]!!
            if (meta != cachedMetadata) {
                logger.warn("Provided metadata does not match with cached metadata, cached file will re-download.")
                downloadFile0(meta)
                return expectFilePath
            }
            if (!validateFile(expectFilePath, meta.fileHash)) {
                logger.warn("Cached file sha1 does not match with metadata, cached file will re-download.")
                downloadFile0(meta)
                return expectFilePath
            }
            return expectFilePath
        } else {
            downloadFile0(meta)
            return expectFilePath
        }
    }

    private fun downloadFile0(meta: FileMetadata) {
        val outputFilePath = cacheDownloadRoot / meta.fileName
        val metadataFilePath = cacheFileMetaRoot / "${meta.fileName}.json"
        metadataFilePath.apply {
            deleteIfExists()
            createParentDirectories()
            createFile()
            writeText(icu.takeneko.appwebterminal.resource.Json.encodeToString(meta))
        }
        outputFilePath.apply {
            deleteIfExists()
            createParentDirectories()
            httpClient.send(
                HttpRequest.newBuilder(URL(meta.downloadUrl).toURI()).GET().build(),
                BodyHandlers.ofFile(this)
            )
        }
        if (!validateFile(outputFilePath, meta.fileHash)) {
            throw RuntimeException(
                "Downloaded file $outputFilePath sha1 not match. (expect: ${
                    meta.fileHash
                }, actual: ${
                    outputFilePath.toFile().sha1()
                }"
            )
        }
    }

    private fun validateFile(filePath: Path, expectSha: String): Boolean {
        return filePath.toFile().sha1() == expectSha
    }

    fun requireFile(fileName: String): Path {
        return cacheDownloadRoot / fileName
    }
}

fun File.sha1(): String {
    val messageDigest = MessageDigest.getInstance("SHA-1")
    val result = messageDigest.digest(this.readBytes())
    return Hex.encodeHex(result).concatToString()
}

@kotlinx.serialization.Serializable
data class FileMetadata(val fileName: String, val downloadUrl: String, val fileSize: Long, val fileHash: String)