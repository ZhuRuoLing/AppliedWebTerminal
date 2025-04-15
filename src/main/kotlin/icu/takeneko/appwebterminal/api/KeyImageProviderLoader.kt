package icu.takeneko.appwebterminal.api

import icu.takeneko.appwebterminal.client.rendering.AEKeyImageProvider
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.fml.loading.LoadingModList
import org.slf4j.LoggerFactory
import java.lang.annotation.ElementType

object KeyImageProviderLoader {
    val providers = mutableMapOf<ResourceLocation, () -> AEKeyImageProvider<*>>()
    private val ANNOTATION_NAME = "L" + ImageProvider::class.java.name.replace(".", "/") + ";"
    private val logger = LoggerFactory.getLogger("KeyImageProviderLoader")

    internal fun compileContents() {
        for (modFile in LoadingModList.get().modFiles) {
            val scanData = modFile.file.getScanResult()
            for (annotation in scanData.annotations) {
                if (annotation.annotationType().descriptor == ANNOTATION_NAME
                    && annotation.targetType() === ElementType.TYPE
                ) {
                    val id = annotation.annotationData()["value"] as String
                    val modid = annotation.annotationData()["modid"] as String
                    logger.info("Considering AEKeyImageProvider {} for {}", annotation.memberName(), modid)
                    if (LoadingModList.get().mods.any { it.modId == modid }) {
                        this.providers[ResourceLocation(id)] = {
                            Class.forName(annotation.memberName).run {
                                this.getConstructor().apply { trySetAccessible() }
                            }.newInstance() as AEKeyImageProvider<*>
                        }
                    }
                }
            }
        }
    }
}