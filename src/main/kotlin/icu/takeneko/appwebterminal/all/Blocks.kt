package icu.takeneko.appwebterminal.all

import appeng.core.definitions.AEBlocks
import appeng.core.definitions.AEItems
import com.tterrag.registrate.providers.RegistrateRecipeProvider
import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.block.WebTerminalBlock
import icu.takeneko.appwebterminal.registrate
import icu.takeneko.appwebterminal.util.get
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile

val meWebTerminal = registrate.block<WebTerminalBlock>("web_terminal", ::WebTerminalBlock)
    .blockstate { dataGenContext, registrateBlockstateProvider ->
        registrateBlockstateProvider.directionalBlock(dataGenContext.get()) {
            UncheckedModelFile(
                AppWebTerminal.location(
                    if (it[WebTerminalBlock.ONLINE])
                        "block/web_terminal"
                    else
                        "block/web_terminal_offline"
                )
            )
        }
    }
    .lang("ME Web Terminal")
    .item()
    .model { dataGenContext, registrateItemModelProvider ->
        registrateItemModelProvider.getBuilder(dataGenContext.name)
            .parent(UncheckedModelFile(AppWebTerminal.location("block/web_terminal")))
    }
    .recipe { ctx, prov ->
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
            .pattern("ABA")
            .pattern("CDC")
            .pattern("EFE")
            .define('A', AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED)
            .define('B', AEBlocks.WIRELESS_ACCESS_POINT)
            .define('C', AEItems.ENGINEERING_PROCESSOR)
            .define('D', AEItems.CRAFTING_CARD)
            .define('E', AEBlocks.QUARTZ_VIBRANT_GLASS)
            .define('F', AEBlocks.CRAFTING_MONITOR)
            .unlockedBy("has_item", RegistrateRecipeProvider.has(AEBlocks.WIRELESS_ACCESS_POINT))
            .save(prov)
    }
    .build()
    .register()


fun registerBlocks() {
    //intentionally empty
}