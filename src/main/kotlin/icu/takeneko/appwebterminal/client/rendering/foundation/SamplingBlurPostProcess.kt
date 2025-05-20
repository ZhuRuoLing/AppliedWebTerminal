package icu.takeneko.appwebterminal.client.rendering.foundation

import org.lwjgl.opengl.GL30
import org.slf4j.LoggerFactory

class SamplingBlurPostProcess(
    xSize: Int,
    ySize: Int,
    private val iterations: Int
) : PostProcess(xSize, ySize) {

    private val logger = LoggerFactory.getLogger("SamplingBlurPostProcess")
    private var buffers = createFramebuffers()

    val output: FrameBuffer
        get() = buffers[0]

    private fun createFramebuffers() = buildList {
        for (i in 0 until iterations) {
            val x = xSize shr i
            val y = ySize shr i
            if (x < 5 || y < 5) {
                logger.warn("Iterations too big for current size!")
                break
            }
            add(FrameBuffer(x, y, false))
        }
    }

    override fun resize(xSize: Int, ySize: Int) {
        buffers.forEach { it.dispose() }
        this.xSize = xSize
        this.ySize = ySize
        buffers = createFramebuffers()
    }

    private fun blitNamed(
        from: Int,
        to: Int,
        x1: Int,
        y1: Int,
        x2: Int,
        y2: Int
    ) {
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, from)
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, to)
        GL30.glBlitFramebuffer(
            0, 0,
            x1, y1,
            0, 0,
            x2, y2,
            GL30.GL_COLOR_BUFFER_BIT,
            GL30.GL_NEAREST
        )
    }

    override fun apply(inputFramebuffer: Int) {
        blitNamed(
            inputFramebuffer,
            buffers[0].framebufferId,
            xSize, ySize,
            xSize, ySize
        )
        for (i in 1 until buffers.size) {
            val from = buffers[i - 1]
            val to = buffers[i]
            blitNamed(
                from.framebufferId,
                to.framebufferId,
                from.xSize, from.ySize,
                to.xSize, to.xSize
            )
        }
        for (i in (buffers.size - 1) downTo 1) {
            val from = buffers[i]
            val to = buffers[i - 1]
            blitNamed(
                from.framebufferId,
                to.framebufferId,
                from.xSize, from.ySize,
                to.xSize, to.xSize
            )
        }
    }
}