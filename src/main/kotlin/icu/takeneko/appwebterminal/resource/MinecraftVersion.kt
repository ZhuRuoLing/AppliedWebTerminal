package icu.takeneko.appwebterminal.resource

import icu.takeneko.appwebterminal.config.MinecraftAssetsApi
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.util.concurrent.CompletableFuture

class MinecraftVersion(val assetsSource: MinecraftAssetsApi) {
    private val mojangApiUrl = assetsSource.endpoint
    private val Json = Json {
        ignoreUnknownKeys = true
    }
    private val versionManifestUrl = "$mojangApiUrl/mc/game/version_manifest_v2.json"
    lateinit var versionManifest: VersionManifest
    private val httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build()
    val versions = mutableMapOf<String, VersionData>()

    fun update(): CompletableFuture<VersionManifest> {
        println(versionManifestUrl)
        val request = HttpRequest.newBuilder().GET().uri(Url(versionManifestUrl).toURI()).build()
        return httpClient.sendAsync(request, BodyHandlers.ofString()).thenApply {
            val resp = it.body()
            println(it.headers())
            println(resp)
            versionManifest = Json.decodeFromString<VersionManifest>(resp)
            for (version in versionManifest.versions) {
                versions[version.id] = version
            }
            return@thenApply versionManifest
        }
    }

    fun resolveVersionMetadata(versionName: String): CompletableFuture<VersionMetadata>? {
        val version = versions[versionName] ?: return null
        val request = HttpRequest.newBuilder().GET().uri(Url(version.url).toURI()).build()
        return httpClient.sendAsync(request, BodyHandlers.ofString()).thenApply {
            val resp = it.body()
            return@thenApply Json.decodeFromString<VersionMetadata>(resp)
        }
    }

    fun resolveVersionAssetIndex(version: String): CompletableFuture<AssetIndex>? {
        val versionMetadataFuture = resolveVersionMetadata(version) ?: return null
        return versionMetadataFuture.thenApply {
            HttpRequest.newBuilder().GET().uri(Url(assetsSource.urlIndexReplacer.apply(it.assetIndex.url)).toURI()).build()
        }.thenCompose {
            httpClient.sendAsync(it, BodyHandlers.ofString())
        }.thenApply {
            return@thenApply Json.decodeFromString(it.body())
        }
    }
}

@kotlinx.serialization.Serializable
data class LatestData(val release: String, val snapshot: String)

enum class VersionType {
    @kotlinx.serialization.SerialName("snapshot")
    SNAPSHOT,

    @kotlinx.serialization.SerialName("release")
    RELEASE,

    @kotlinx.serialization.SerialName("old_alpha")
    OLD_ALPHA,

    @kotlinx.serialization.SerialName("old_beta")
    OLD_BETA,
}

@kotlinx.serialization.Serializable
data class AssetIndex(
    val objects: Map<String, AssetObject>
)

@kotlinx.serialization.Serializable
data class AssetObject(
    val hash: String,
    val size: Long
){
    val downloadUrl:String
        get() = "https://resources.download.minecraft.net/${hash.substring(0..1)}/$hash"
}

@kotlinx.serialization.Serializable
data class VersionData(
    val id: String,
    val type: VersionType,
    val url: String,
    val releaseTime: String,
    val time: String
)

@kotlinx.serialization.Serializable
data class VersionMetadata(
    val assetIndex: AssetIndexMetadata
)

@kotlinx.serialization.Serializable
data class AssetIndexMetadata(
    val sha1: String,
    val size: Int,
    val url: String,
    val id: Int
)

@kotlinx.serialization.Serializable
data class VersionManifest(val latest: LatestData, val versions: MutableList<VersionData>)