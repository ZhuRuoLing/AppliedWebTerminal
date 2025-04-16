package icu.takeneko.appwebterminal.client.rendering

import appeng.api.stacks.AEKey
import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexSorting
import icu.takeneko.appwebterminal.all.KeyImageProviderRegistry
import net.minecraft.client.Minecraft
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.slf4j.LoggerFactory
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.div

class AEKeyRenderer(
    private val sizeX: Int,
    private val sizeY: Int
) {
    private val frameBuffer = FrameBuffer(
        sizeX,
        sizeY
    )
    private val nativeImage = NativeImage(
        sizeX,
        sizeY,
        false
    )

    private val projectionMatrix = Matrix4f().setOrtho(
        0f,
        sizeX.toFloat(),
        sizeY.toFloat(),
        0f,
        -1000f,
        1000f
    )

    private val logger = LoggerFactory.getLogger("AEKeyRenderer")

    private var lastNotify: Long = 0

    fun renderAll(basePath: Path, progressListener: RenderProgressListener) {
        rendering = true
        KeyImageProviderRegistry.values.forEach { prov ->
            val entries = prov.getAllEntries().toList()
            progressListener.notifyTotalCount(entries.size)
            entries.forEachIndexed { index, it ->
                val id = it.id
                if (System.currentTimeMillis() - lastNotify > 200) {
                    progressListener.notifyProgress(index, it)
                    lastNotify = System.currentTimeMillis()
                }
                @Suppress("UNCHECKED_CAST")
                renderSingle(
                    it,
                    prov as AEKeyImageProvider<AEKey>,
                    basePath
                        / it.type.id.toString().replace(":", "_")
                        / "$id.png".replace(":", "_")
                )
            }
        }
        progressListener.notifyCompleted()
        rendering = false
    }

    fun <T : AEKey> renderSingle(key: T, provider: AEKeyImageProvider<T>, path: Path) {
        val bufferSource = Minecraft.getInstance().renderBuffers().bufferSource()
        val poseStack = PoseStack()
        poseStack.pushPose()

        frameBuffer.clear()
        val oldRotMat = Matrix3f(RenderSystem.getInverseViewRotationMatrix())
        val oldProjMat = Matrix4f(RenderSystem.getProjectionMatrix())
        val oldVertexSorting = RenderSystem.getVertexSorting()
        RenderSystem.setInverseViewRotationMatrix(Matrix3f())
        RenderSystem.getModelViewStack().apply {
            pushPose()
            setIdentity()
            RenderSystem.applyModelViewMatrix()
        }
        Lighting.setupForEntityInInventory()
        RenderSystem.setProjectionMatrix(projectionMatrix, VertexSorting.ORTHOGRAPHIC_Z)
        RenderSystem.enableCull()
        RenderSystem.setShaderColor(0.99f,0.99f,0.99f,1f)
        RenderSystem.enableDepthTest()
        frameBuffer.bindWrite(true)
        try {
            provider.renderImage(
                key,
                poseStack,
                bufferSource,
                sizeX,
                sizeY
            )
        } catch (e : Exception) {
            logger.error("Error while rendering ${key.type.id}/${key.id}", e)
        }

        frameBuffer.bindWrite(true)
        bufferSource.endBatch()
        RenderSystem.getModelViewStack().popPose()
        RenderSystem.applyModelViewMatrix()
        RenderSystem.setProjectionMatrix(oldProjMat, oldVertexSorting)
        RenderSystem.setInverseViewRotationMatrix(oldRotMat)
        Minecraft.getInstance().mainRenderTarget.bindWrite(true)
        frameBuffer.bindRead()
        nativeImage.downloadTexture(0, true)
        nativeImage.flipY()
        nativeImage.applyToAllPixels {
            if (it == -16777216) {
                return@applyToAllPixels 0
            }
            it
        }
        path.apply {
            createParentDirectories()
            deleteIfExists()
            createFile()
            nativeImage.writeToFile(this)
        }
    }

    fun dispose() {
        frameBuffer.dispose()
        nativeImage.close()
    }

    companion object {
        var rendering = false
            private set
    }
}
