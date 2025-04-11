package icu.takeneko.appwebterminal.support

import appeng.api.networking.IGrid
import net.minecraft.core.BlockPos
import java.util.UUID

interface AENetworkAccess {
    fun getId(): UUID

    fun worldPosition(): BlockPos

    fun markDirty()

    fun getGrid(): IGrid?

    fun auth(password: String): Boolean

    fun update(displayName: String, password: String)

    fun validateNonce(nonce: String): Boolean

    fun getNonce(): String
}