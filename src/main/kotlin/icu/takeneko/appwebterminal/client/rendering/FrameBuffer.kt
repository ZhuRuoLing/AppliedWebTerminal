package icu.takeneko.appwebterminal.client.rendering

import org.lwjgl.opengl.GL30.GL_TEXTURE_2D
import org.lwjgl.opengl.GL30.glBindTexture
import org.lwjgl.opengl.GL30.glDeleteFramebuffers
import org.lwjgl.opengl.GL30.glGenFramebuffers
import org.lwjgl.opengl.GL30.glGenTextures

class FrameBuffer(
    val xSize: Int,
    val ySize: Int
) {
    val glId: Int
    val colorTextureId: Int
    val depthTextureId: Int

    init {
        glId = glGenFramebuffers()
        colorTextureId = glGenTextures()
        depthTextureId = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, colorTextureId)
    }

    fun bind(setViewport: Boolean) {

    }

    fun dispose() {
        glDeleteFramebuffers(glId)
    }
}