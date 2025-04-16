package icu.takeneko.appwebterminal.all

import appeng.core.definitions.AEBlocks
import appeng.core.definitions.AEItems
import com.tterrag.registrate.providers.RegistrateRecipeProvider
import com.tterrag.registrate.util.entry.BlockEntry
import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.block.WebTerminalBlock
import icu.takeneko.appwebterminal.registrate
import icu.takeneko.appwebterminal.util.get
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Blocks
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile

val MEWebTerminal: BlockEntry<WebTerminalBlock> = registrate.block<WebTerminalBlock>("web_terminal", ::WebTerminalBlock)
    .initialProperties { Blocks.IRON_BLOCK }
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
    .properties {
        it.lightLevel { bs ->
            if (bs[WebTerminalBlock.ONLINE]) 5 else 0
        }
    }
    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
    .tag(BlockTags.NEEDS_STONE_TOOL)
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
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ctx.get())
            .requires(MEWebTerminalPartItem)
            .unlockedBy("has_item", RegistrateRecipeProvider.has(MEWebTerminalPartItem))
            .save(prov, AppWebTerminal.location("cable_web_terminal_from_part"))
    }
    .build()
    .register()


fun registerBlocks() {
    //intentionally empty
}