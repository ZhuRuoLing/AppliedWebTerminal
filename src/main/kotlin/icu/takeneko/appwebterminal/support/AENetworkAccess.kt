package icu.takeneko.appwebterminal.support

import appeng.api.networking.IGrid
import net.minecraft.core.BlockPos
import java.util.UUID

interface AENetworkAccess {
    var displayName:String

    fun getId(): UUID

    fun worldPosition(): BlockPos

    fun markDirty()

    fun getGrid(): IGrid?
}