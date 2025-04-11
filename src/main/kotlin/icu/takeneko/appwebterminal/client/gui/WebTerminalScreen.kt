package icu.takeneko.appwebterminal.client.gui

import appeng.client.gui.style.StyleManager
import appeng.client.gui.widgets.AETextField
import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.all.networkingChannel
import icu.takeneko.appwebterminal.networking.UpdateWebTerminalNamePacket
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraftforge.network.PacketDistributor
import java.util.*

class WebTerminalScreen(
    private var name: String,
    private var uuid: UUID,
    private var password: String
) : Screen(Component.translatable("appwebterminal.screen.title")) {

    private val texture = AppWebTerminal.location("textures/gui/blank.png")
    private lateinit var nameField: AETextField
    private lateinit var finishButton: Button
    private lateinit var passwordField:AETextField
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
            Minecraft.getInstance().setScreen(null)
        }.bounds(x + 130, y + 110, 60, 20).build()
        addRenderableWidget(nameField)
        addRenderableWidget(passwordField)
        addRenderableWidget(finishButton)
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val x: Int = (this.width - 195) / 2
        val y: Int = (this.height - 136) / 2
        guiGraphics.drawString(Minecraft.getInstance().font, this.title, x + 8, y + 6, 4210752)
        guiGraphics.blit(texture, x, y, 0, 0, 195, 136)
        guiGraphics.drawString(
            Minecraft.getInstance().font,
            Component.translatable("appwebterminal.hint.name"),
            x + 8,
            y + 42,
            4210752,
            false
        )
        guiGraphics.drawString(
            Minecraft.getInstance().font,
            Component.translatable("appwebterminal.hint.password"),
            x + 8,
            y + 72,
            4210752,
            false
        )
        super.render(guiGraphics, mouseX, mouseY, partialTick)
    }

    override fun removed() {
        networkingChannel.send(PacketDistributor.SERVER.noArg(), UpdateWebTerminalNamePacket(name, uuid, password))
    }
}