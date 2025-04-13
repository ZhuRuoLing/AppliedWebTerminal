package icu.takeneko.appwebterminal.support

import appeng.api.networking.IGrid
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import java.util.UUID

interface AENetworkAccess {
    fun getId(): UUID

    fun worldPosition(): BlockPos

    fun markDirty()

    fun getGrid(): IGrid?

    fun auth(password: String): Boolean

    fun update(displayName: String, password: String):Boolean

    fun validateNonce(nonce: String): Boolean

    fun getNonce(): String

    fun getTerminalName(): String

    fun level(): Level?
}