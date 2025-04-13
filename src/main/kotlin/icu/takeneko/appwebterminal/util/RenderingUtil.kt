package icu.takeneko.appwebterminal.util

import com.mojang.blaze3d.vertex.PoseStack

inline fun <reified T> PoseStack.use(block: PoseStack.() -> T):T {
    this.pushPose()
    val ret = this.block()
    this.popPose()
    return ret
}