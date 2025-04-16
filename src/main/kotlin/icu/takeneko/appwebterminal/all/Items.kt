package icu.takeneko.appwebterminal.all

import appeng.api.parts.PartModels
import appeng.items.parts.PartItem
import appeng.items.parts.PartModelsHelper
import com.tterrag.registrate.providers.RegistrateRecipeProvider
import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.block.part.WebTerminalPart
import icu.takeneko.appwebterminal.registrate
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.ShapelessRecipeBuilder

val MEWebTerminalPartItem = registrate
    .item<PartItem<WebTerminalPart>>("cable_web_terminal") {
        PartItem(
            it,
            WebTerminalPart::class.java
        ) { item -> WebTerminalPart(item) }
    }.lang("ME Web Terminal")
    .model { _, _ -> }
    .recipe { dataGenContext, registrateRecipeProvider ->
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, dataGenContext.get())
            .requires(MEWebTerminal)
            .unlockedBy("has_item", RegistrateRecipeProvider.has(MEWebTerminal))
            .save(registrateRecipeProvider, AppWebTerminal.location("cable_web_terminal_from_block"))
    }.register()


fun registerItems() {
    PartModels.registerModels(PartModelsHelper.createModels(WebTerminalPart::class.java))
}