package icu.takeneko.appwebterminal.client.rendering

import net.minecraft.client.Minecraft
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT
import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL11.GL_NEAREST
import org.lwjgl.opengl.GL11.GL_RGBA
import org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER
import org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER
import org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S
import org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T
import org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE
import org.lwjgl.opengl.GL11.GL_ZERO
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.opengl.GL11.glClearColor
import org.lwjgl.opengl.GL11.glClearDepth
import org.lwjgl.opengl.GL11.glGetError
import org.lwjgl.opengl.GL11.glTexParameteri
import org.lwjgl.opengl.GL11.glViewport
import org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE
import org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0
import org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT
import org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT
import org.lwjgl.opengl.GL30.GL_FRAMEBUFFER
import org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE
import org.lwjgl.opengl.GL30.GL_TEXTURE_2D
import org.lwjgl.opengl.GL30.GL_TEXTURE_COMPARE_MODE
import org.lwjgl.opengl.GL30.glBindFramebuffer
import org.lwjgl.opengl.GL30.glBindTexture
import org.lwjgl.opengl.GL30.glCheckFramebufferStatus
import org.lwjgl.opengl.GL30.glDeleteFramebuffers
import org.lwjgl.opengl.GL30.glFramebufferTexture2D
import org.lwjgl.opengl.GL30.glGenFramebuffers
import org.lwjgl.opengl.GL30.glGenTextures
import java.nio.IntBuffer

@Suppress("JoinDeclarationAndAssignment")
class FrameBuffer(
    private val xSize: Int,
    private val ySize: Int
) {
    private val framebufferId: Int
    private val colorTextureId: Int
    private val depthTextureId: Int
    private var clearColorR = 0f
    private var clearColorG = 0f
    private var clearColorB = 0f
    private var clearColorA = 0f

    init {
        framebufferId = glGenFramebuffers()
        colorTextureId = glGenTextures()
        depthTextureId = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, colorTextureId)
        GL11.glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGBA,
            xSize,
            ySize,
            0,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            null as IntBuffer?
        )
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glBindTexture(GL_TEXTURE_2D, depthTextureId)
        GL11.glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_DEPTH_COMPONENT,
            xSize,
            ySize,
            0,
            GL_DEPTH_COMPONENT,
            GL_FLOAT,
            null as IntBuffer?
        )
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_ZERO)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glBindTexture(GL_TEXTURE_2D, 0)
        glBindFramebuffer(GL_FRAMEBUFFER, framebufferId)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTextureId, 0)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTextureId, 0)
        val status = glCheckFramebufferStatus(GL_FRAMEBUFFER)
        if (status != GL_FRAMEBUFFER_COMPLETE) {
            throw IllegalStateException("Incomplete framebuffer, status: $status")
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    fun clear() {
        glBindFramebuffer(GL_FRAMEBUFFER, framebufferId)
        glClearColor(clearColorR, clearColorG, clearColorB, clearColorA)
        glClearDepth(1.0)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        if (Minecraft.ON_OSX) {
            glGetError()
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    fun setClearColor(r: Float, g: Float, b: Float, a: Float) {
        this.clearColorR = r
        this.clearColorG = g
        this.clearColorB = b
        this.clearColorA = a
    }

    fun bindRead() {
        glBindTexture(GL_TEXTURE_2D, this.colorTextureId)
    }

    fun bindWrite(setViewport: Boolean = true) {
        glBindFramebuffer(GL_FRAMEBUFFER, framebufferId)
        if (setViewport) {
            glViewport(0, 0, xSize, ySize)
        }
    }

    fun dispose() {
        glDeleteFramebuffers(framebufferId)
    }
}