package icu.takeneko.appwebterminal.resource

import com.mojang.logging.LogUtils
import icu.takeneko.appwebterminal.config.MinecraftAssetsApi
import io.ktor.util.*
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import java.util.regex.Pattern

class LanguageFileDownloader(val assetsSource: MinecraftAssetsApi) : Thread("LanguageFileDownloader") {
    private val logger = LogUtils.getLogger()
    private val minecraftVersion = MinecraftVersion(assetsSource)
    override fun run() {
        runBlocking {
            CacheProvider.init()
            logger.info("Updating minecraft versions.")
            minecraftVersion.update().whenComplete { _, u ->
                if (u != null) {
                    logger.error("Failed to update Minecraft versions.", u)
                }
            }.await()
            logger.info("Resolving version asset indexes.")
            val versionAssetIndex = minecraftVersion.resolveVersionAssetIndex("1.20.1")!!.whenComplete { t, u ->
                if (u != null) {
                    logger.error("Failed to resolve version assetIndex.", u)
                }
            }.await()
            versionAssetIndex.objects.entries.forEach { (name, obj) ->
                if (languageFilePattern.matcher(name).matches()) {
                    val encodedName = name.encodeBase64()
                    fileNameMapping[name] = encodedName
                    logger.info("Downloading $name to $encodedName")
                    for (i in 0 until 3) {
                        try {
                            CacheProvider.downloadFile(
                                FileMetadata(
                                    encodedName,
                                    assetsSource.urlAssetsReplacer.apply(obj.downloadUrl),
                                    obj.size,
                                    obj.hash
                                )
                            )
                            break
                        } catch (e: Exception) {
                            logger.error("Failed to download $name to $encodedName", e)
                            continue
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val languageFilePattern = Pattern.compile("minecraft/lang/(.+).json")
        val fileNameMapping = mutableMapOf<String, String>()
    }
}