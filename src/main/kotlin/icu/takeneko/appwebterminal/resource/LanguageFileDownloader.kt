package icu.takeneko.appwebterminal.resource

import com.mojang.logging.LogUtils
import io.ktor.util.*
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import java.util.regex.Pattern

class LanguageFileDownloader : Thread("LanguageFileDownloader") {
    private val logger = LogUtils.getLogger()
    override fun run() {
        runBlocking {
            CacheProvider.init()
            logger.info("Updating minecraft versions.")
            MinecraftVersion.update().whenComplete { _, u ->
                if (u != null) {
                    logger.error("Failed to update Minecraft versions.", u)
                }
            }.await()
            logger.info("Resolving version asset indexes.")
            val versionAssetIndex = MinecraftVersion.resolveVersionAssetIndex("1.20.1")!!.whenComplete { t, u ->
                if (u != null) {
                    logger.error("Failed to resolve version assetIndex.", u)
                }
            }.await()
            versionAssetIndex.objects.entries.forEach { (name, obj) ->
                if (languageFilePattern.matcher(name).matches()) {
                    val encodedName = name.encodeBase64()
                    fileNameMapping[name] = encodedName
                    logger.info("Downloading $name to $encodedName")
                    CacheProvider.downloadFile(FileMetadata(encodedName, obj.downloadUrl, obj.size, obj.hash))
                }
            }
        }
    }

    companion object {
        private val languageFilePattern = Pattern.compile("minecraft/lang/(.+).json")
        val fileNameMapping = mutableMapOf<String, String>()
    }
}