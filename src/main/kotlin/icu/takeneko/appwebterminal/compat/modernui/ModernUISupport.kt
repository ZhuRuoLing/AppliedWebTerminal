package icu.takeneko.appwebterminal.compat.modernui

import icyllis.modernui.mc.ExtendedGuiGraphics
import net.minecraft.client.gui.GuiGraphics
import net.minecraftforge.fml.ModList

object ModernUISupport {
    val modernUIPresent by lazy {
        ModList.get().isLoaded("modernui")
    }

    private fun strokeRoundRect0(
        guiGraphics: GuiGraphics,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        radius: Float
    ) {
        val ext = ExtendedGuiGraphics(guiGraphics)
        ext.strokeWidth = 2f
        ext.setColor(0xFFC6C6C6.toInt())
        ext.fillRoundRect(left, top, right, bottom, radius)
        ext.setGradient(ExtendedGuiGraphics.Orientation.TL_BR, 0xFFFFFFFF.toInt(), 0xFFDDDDDD.toInt())
        ext.strokeRoundRect(left, top, right, bottom, radius)
    }

    fun roundRect(guiGraphics: GuiGraphics, left: Float, top: Float, right: Float, bottom: Float, radius: Float) {
        if (modernUIPresent) {
            strokeRoundRect0(
                guiGraphics, left, top, right, bottom, radius
            )
        }
    }
}