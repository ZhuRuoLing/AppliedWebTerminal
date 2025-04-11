package icu.takeneko.appwebterminal.util

import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.*

fun extractZipFile(inputStream: InputStream, target: Path) {
    target.resolve(System.identityHashCode(inputStream).toString() + ".zip").apply {
        deleteIfExists()
        createFile()
        writer().use {

        }
    }
}