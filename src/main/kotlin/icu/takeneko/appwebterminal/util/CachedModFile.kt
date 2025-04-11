package icu.takeneko.appwebterminal.util

import net.minecraftforge.forgespi.locating.IModFile
import kotlin.io.path.readBytes

class CachedModFile(
    private val modFile: IModFile
) {
    private val cacheMap = mutableMapOf<String, ByteArray>()

    fun getResource(path: String): ByteArray? {
        val resourcePath = modFile.findResource(path) ?: return null
        return if (path in cacheMap) {
            cacheMap[path]
        } else {
            resourcePath.readBytes().also { cacheMap[path] = it }
        }
    }
}