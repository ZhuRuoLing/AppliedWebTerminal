package icu.takeneko.appwebterminal.client.rendering.foundation

import net.minecraft.client.Minecraft
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL14
import org.lwjgl.opengl.GL30
import java.nio.IntBuffer

@Suppress("JoinDeclarationAndAssignment")
class FrameBuffer(
    xSize: Int,
    ySize: Int,
    private val hasDepth: Boolean = true
) {
    var ySize = ySize
        private set
    var xSize = xSize
        private set
    val framebufferId: Int
    var colorTextureId: Int = 0
    private var depthTextureId: Int = 0
    private var clearColorR = 0f
    private var clearColorG = 0f
    private var clearColorB = 0f
    private var clearColorA = 0f

    init {
        framebufferId = GL30.glGenFramebuffers()
        createTexture()
        val status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER)
        if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw IllegalStateException("Incomplete framebuffer, status: $status")
        }
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
    }

    fun createTexture() {
        colorTextureId = GL11.glGenTextures()
        if (hasDepth) {
            depthTextureId = GL11.glGenTextures()
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTextureId)
        GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D,
            0,
            GL11.GL_RGBA,
            xSize,
            ySize,
            0,
            GL11.GL_RGBA,
            GL11.GL_UNSIGNED_BYTE,
            null as IntBuffer?
        )
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)
        if (hasDepth) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTextureId)
            GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_DEPTH_COMPONENT,
                xSize,
                ySize,
                0,
                GL11.GL_DEPTH_COMPONENT,
                GL11.GL_FLOAT,
                null as IntBuffer?
            )
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL11.GL_ZERO)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferId)
        GL30.glFramebufferTexture2D(
            GL30.GL_FRAMEBUFFER,
            GL30.GL_COLOR_ATTACHMENT0,
            GL11.GL_TEXTURE_2D,
            colorTextureId,
            0
        )
        if (hasDepth) {
            GL30.glFramebufferTexture2D(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_DEPTH_ATTACHMENT,
                GL11.GL_TEXTURE_2D,
                depthTextureId,
                0
            )
        }
    }

    fun resize(x: Int, y: Int) {
        GL11.glDeleteTextures(colorTextureId)
        if (hasDepth) {
            GL11.glDeleteTextures(depthTextureId)
        }
        this.xSize = x
        this.ySize = y
        createTexture()
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
    }

    fun clear() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferId)
        GL11.glClearColor(clearColorR, clearColorG, clearColorB, clearColorA)
        if (hasDepth) {
            GL11.glClearDepth(1.0)
        }
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or if (hasDepth) GL11.GL_DEPTH_BUFFER_BIT else 0)
        if (Minecraft.ON_OSX) {
            GL11.glGetError()
        }
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
    }

    fun copyColorsFrom(src: Int) {
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, src)
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, framebufferId)
        GL30.glBlitFramebuffer(
            0, 0,
            xSize, ySize,
            0, 0,
            xSize, ySize,
            GL11.GL_COLOR_BUFFER_BIT,
            GL11.GL_LINEAR
        )
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0)
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0)
    }

    fun setClearColor(r: Float, g: Float, b: Float, a: Float) {
        this.clearColorR = r
        this.clearColorG = g
        this.clearColorB = b
        this.clearColorA = a
    }

    fun bindRead() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.colorTextureId)
    }

    fun bindWrite(setViewport: Boolean = true) {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferId)
        if (setViewport) {
            GL11.glViewport(0, 0, xSize, ySize)
        }
    }

    fun dispose() {
        GL30.glDeleteFramebuffers(framebufferId)
    }

    fun unbindWrite() {
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0)
    }
}