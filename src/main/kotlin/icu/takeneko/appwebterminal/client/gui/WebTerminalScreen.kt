package icu.takeneko.appwebterminal.client.gui

import appeng.client.gui.style.StyleManager
import appeng.client.gui.widgets.AETextField
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.blaze3d.vertex.VertexSorting
import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.all.networkingChannel
import icu.takeneko.appwebterminal.client.all.BlurPostProcessInstance
import icu.takeneko.appwebterminal.client.all.TexturedRoundRect
import icu.takeneko.appwebterminal.networking.UpdateWebTerminalNamePacket
import icu.takeneko.appwebterminal.util.use
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import net.minecraftforge.network.PacketDistributor
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW
import java.util.UUID
import kotlin.math.PI
import kotlin.math.cos

@Suppress("PrivatePropertyName")
class WebTerminalScreen(
    private var name: String,
    private var uuid: UUID,
    private var password: String,
    private var isOnline: Boolean,
) : Screen(Component.translatable("appwebterminal.screen.title")) {
    private val AnimationProgressMillis = 175L
    private lateinit var nameField: AETextField
    private lateinit var finishButton: Button
    private lateinit var passwordField: AETextField

    private var animationState = AnimationState.START
    private var animationProgress = 0L
    private var animationStartMillis = -1L
    private var animationEndMillis = -1L
    private var shouldDismissScreen = false
    private var shouldContinueRendering = true
    private var scale = 1f
    private var alpha = 1f
    override fun init() {
        super.init()
        val style = StyleManager.loadStyleDoc("/screens/terminals/crafting_terminal.json")
        val x: Int = (this.width - 195) / 2
        val y: Int = (this.height - 136) / 2
        nameField = AETextField(style, Minecraft.getInstance().font, x + 60, y + 40, 128, 18)
            .apply {
                setBordered(false)
                value = name
                setResponder {
                    name = it
                }
            }
        passwordField = AETextField(style, Minecraft.getInstance().font, x + 60, y + 70, 128, 18)
            .apply {
                setBordered(false)
                value = password
                setResponder {
                    password = it
                }
            }

        finishButton = Button.builder(Component.translatable("appwebterminal.button.done")) {
            setupDismissAnimationState()
        }.bounds(x + 130, y + 110, 60, 20).build()
        if (animationState != AnimationState.NORMAL) {
            finishButton.setAlpha(animationState.endAlpha)
        }
        addRenderableWidget(nameField)
        addRenderableWidget(passwordField)
        addRenderableWidget(finishButton)
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == 256 || (this.focused !is AETextField && keyCode == GLFW.GLFW_KEY_E)) {
            if (!shouldDismissScreen) {
                setupDismissAnimationState()
            }
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    fun setupDismissAnimationState() {
        shouldDismissScreen = true
        animationState = AnimationState.DISMISS
        animationStartMillis = -1L
    }

    override fun shouldCloseOnEsc(): Boolean {
        return false
    }

    fun background(guiGraphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int) {
        RenderSystem.getModelViewStack().pushPose()
        RenderSystem.getModelViewStack().setIdentity()
        RenderSystem.applyModelViewMatrix()
        val poseStack = guiGraphics.pose()
        poseStack.pushPose()

        val guiScale = Minecraft.getInstance().window.guiScale.toFloat()
        val windowWidth = Minecraft.getInstance().window.width
        val windowHeight = Minecraft.getInstance().window.height
        val oldProjMat = RenderSystem.getProjectionMatrix()
        RenderSystem.setProjectionMatrix(
            Matrix4f().setOrtho(
                0f, windowWidth.toFloat(), windowHeight.toFloat(), 0f, -1000f, 1000f
            ),
            VertexSorting.ORTHOGRAPHIC_Z
        )

        val shader = TexturedRoundRect
        poseStack.setIdentity()
        val xMove = (1 - this.scale) * windowWidth / 2
        val yMove = (1 - this.scale) * windowHeight / 2
        poseStack.translate(xMove, yMove, 0f)
        poseStack.scale(this.scale, this.scale, this.scale)
        val x0 = x * guiScale
        val y0 = y * guiScale
        val x1 = (x + width) * guiScale
        val y1 = (y + height) * guiScale
        val u0 = x0 / windowWidth
        val v0 = y0 / windowHeight
        val u1 = x1 / windowWidth
        val v1 = y1 / windowHeight
        val pose = poseStack.last().pose()
        val x0t = (x0 + xMove) * scale
        val y0t = (y0 + yMove) * scale
        val x1t = (x1 + xMove) * scale
        val y1t = (y1 + yMove) * scale
        val centerX: Float = (x0 + x1) * 0.5f
        val centerY: Float = (y0 + y1) * 0.5f
        val extentX: Float = (x1t - x0t) * 0.5f
        val extentY: Float = (y1t - y0t) * 0.5f
        val color = 0xffE0E0E0.toInt()
        shader.setSampler("Sampler0", BlurPostProcessInstance!!.output.colorTextureId)
        RenderSystem.setShaderTexture(0, BlurPostProcessInstance!!.output.colorTextureId)
        shader.safeGetUniform("u_Rect").set(centerX, centerY, extentX, extentY)
        shader.safeGetUniform("u_Radii").set(20f, -1f)
        val bufferBuilder = Tesselator.getInstance().builder
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX)
        bufferBuilder.vertex(pose, x0, y0, 50f)
            .color(color)
            .uv(u0, v0)
            .endVertex()
        bufferBuilder.vertex(pose, x0, y1, 50f)
            .color(color)
            .uv(u0, v1)
            .endVertex()
        bufferBuilder.vertex(pose, x1, y1, 50f)
            .color(color)
            .uv(u1, v1)
            .endVertex()
        bufferBuilder.vertex(pose, x1, y0, 50f)
            .color(color)
            .uv(u1, v0)
            .endVertex()
        RenderSystem.setShader { shader }
        BufferUploader.drawWithShader(bufferBuilder.end())
        RenderSystem.getModelViewStack().popPose()
        RenderSystem.applyModelViewMatrix()
        RenderSystem.setProjectionMatrix(oldProjMat, VertexSorting.ORTHOGRAPHIC_Z)
        poseStack.popPose()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val handled = this.children().any {
            it.mouseClicked(mouseX, mouseY, button).also { b ->
                if (b) {
                    this.focused = it
                }
            }
        }
        if (!handled) {
            this.focused = null
        }
        return true
    }

    fun setupAnimationState(pose: PoseStack) {
        if (animationState == AnimationState.NORMAL) return
        if (animationStartMillis == -1L) {
            animationStartMillis = System.currentTimeMillis()
            animationEndMillis = System.currentTimeMillis() + AnimationProgressMillis
        }
        if (animationProgress >= animationEndMillis) {
            finishButton.setAlpha(animationState.endAlpha)
            if (shouldDismissScreen) {
                Minecraft.getInstance().setScreen(null)
                shouldContinueRendering = false
                return
            }
            animationState = AnimationState.NORMAL
            return
        }
        animationProgress = System.currentTimeMillis()
        val progress = (animationEndMillis - animationProgress) / AnimationProgressMillis.toFloat()
        val progressNonlinear = (cos((progress + 1) * PI) / 2.0 + 0.5).toFloat()

        scale = Mth.lerp(1 - progressNonlinear, animationState.startScale, animationState.endScale)
        alpha = Mth.lerp(1 - progressNonlinear, animationState.startAlpha, animationState.endAlpha)
        println(alpha)
        val xMove = (1 - scale) * width / 2
        val yMove = (1 - scale) * height / 2
        pose.translate(xMove, yMove, 0f)
        pose.scale(scale, scale, scale)
        finishButton.setAlpha(alpha)
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        RenderSystem.enableBlend()
        RenderSystem.disableDepthTest()
        shouldContinueRendering = true
        val pose = guiGraphics.pose()
        pose.pushPose()
        setupAnimationState(pose)
        if (!shouldContinueRendering) {
            pose.popPose()
            return
        }
        val x: Int = (this.width - 195) / 2
        val y: Int = (this.height - 136) / 2
        val font = Minecraft.getInstance().font
        background(guiGraphics, x, y, 195, 136)

        guiGraphics.drawString(font, this.title, x + 8, y + 6, 0xffffff, false)
        val statusText = if (isOnline) {
            Component.translatable("appwebterminal.gui.me_network_online")
        } else {
            Component.translatable("appwebterminal.gui.me_network_offline")
        }
        val statusTextWidth = font.width(statusText)
        guiGraphics.drawString(
            font,
            statusText,
            x + 195 - 8 - statusTextWidth,
            y + 6,
            if (isOnline) 0x00CC00 else 0xFF7777,
            false
        )
        guiGraphics.drawString(
            font,
            Component.translatable("appwebterminal.hint.name"),
            x + 8,
            y + 42,
            0xffffff,
            false
        )
        guiGraphics.drawString(
            font,
            Component.translatable("appwebterminal.hint.password"),
            x + 8,
            y + 72,
            0xffffff,
            false
        )
        guiGraphics.pose().use {
            translate((x + 4).toDouble(), (y + 127).toDouble(), 0.0)
            scale(0.5f, 0.5f, 0.5f)
            guiGraphics.drawString(
                font,
                Component.literal(uuid.toString()),
                0,
                0,
                0xeeeeee,
                false
            )
        }
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        pose.popPose()
    }

    override fun removed() {
        networkingChannel.send(PacketDistributor.SERVER.noArg(), UpdateWebTerminalNamePacket(name, uuid, password))
    }

    enum class AnimationState {
        START {
            override val startScale = 1.25f
            override val endScale = 1f
            override val startAlpha = 0f
            override val endAlpha = 1f
        },
        NORMAL {
            override val startScale = 1f
            override val endScale = 1f
            override val startAlpha = 1f
            override val endAlpha = 1f
        },
        DISMISS {
            override val startScale = 1f
            override val endScale = 1.25f
            override val startAlpha = 1f
            override val endAlpha = 0f
        };

        abstract val startScale: Float
        abstract val endScale: Float
        abstract val startAlpha: Float
        abstract val endAlpha: Float
    }
}