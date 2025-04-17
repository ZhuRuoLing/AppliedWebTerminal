package icu.takeneko.appwebterminal.client.gui

import appeng.client.gui.style.StyleManager
import appeng.client.gui.widgets.AETextField
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.all.networkingChannel
import icu.takeneko.appwebterminal.compat.modernui.ModernUISupport
import icu.takeneko.appwebterminal.networking.UpdateWebTerminalNamePacket
import icu.takeneko.appwebterminal.util.use
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import net.minecraftforge.network.PacketDistributor
import java.util.*
import kotlin.math.PI
import kotlin.math.cos

@Suppress("PrivatePropertyName")
class WebTerminalScreen(
    private var name: String,
    private var uuid: UUID,
    private var password: String,
    private var isOnline: Boolean,
) : Screen(Component.translatable("appwebterminal.screen.title")) {
    private val AnimationProgressMillis = 250L
    private val texture = AppWebTerminal.location("textures/gui/blank.png")
    private lateinit var nameField: AETextField
    private lateinit var finishButton: Button
    private lateinit var passwordField: AETextField

    private var animationState = AnimationState.START
    private var animationProgress = 0L
    private var animationStartMillis = -1L
    private var animationEndMillis = -1L
    private var shouldDismissScreen = false
    private var shouldContinueRendering = true
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
        addRenderableWidget(nameField)
        addRenderableWidget(passwordField)
        addRenderableWidget(finishButton)
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == 256) {
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
        if (ModernUISupport.modernUIPresent) {
            ModernUISupport.roundRect(
                guiGraphics,
                x.toFloat(),
                y.toFloat(),
                x + width.toFloat(),
                y + height.toFloat(),
                5f
            )
            return
        }
        guiGraphics.blit(texture, x, y, 0, 0, 195, 136)
    }

    fun setupAnimationState(pose: PoseStack) {
        if (animationState == AnimationState.NORMAL) return
        if (animationStartMillis == -1L) {
            animationStartMillis = System.currentTimeMillis()
            animationEndMillis = System.currentTimeMillis() + AnimationProgressMillis
        }
        if (animationProgress > animationEndMillis) {
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

        val scale = Mth.lerp(1 - progressNonlinear, animationState.startScale, animationState.endScale)
        val alpha = Mth.lerp(1 - progressNonlinear, animationState.startAlpha, animationState.endAlpha)
        val xMove = (1 - scale) * width / 2
        val yMove = (1 - scale) * height / 2
        pose.translate(xMove, yMove, 0f)
        pose.scale(scale, scale, scale)
        //println(progressNonlinear.toString() + " " + alpha)
        finishButton.setAlpha(alpha)
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        RenderSystem.enableBlend()
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

        guiGraphics.drawString(font, this.title, x + 8, y + 6, 0x404040, false)
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
            if (isOnline) 0x00AA00 else 0xFF5555,
            false
        )
        guiGraphics.drawString(
            font,
            Component.translatable("appwebterminal.hint.name"),
            x + 8,
            y + 42,
            0x404040,
            false
        )
        guiGraphics.drawString(
            font,
            Component.translatable("appwebterminal.hint.password"),
            x + 8,
            y + 72,
            0x404040,
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
                0xAAAAAA,
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