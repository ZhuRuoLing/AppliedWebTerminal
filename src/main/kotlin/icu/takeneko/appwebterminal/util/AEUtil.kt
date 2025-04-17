package icu.takeneko.appwebterminal.util

import appeng.api.stacks.AEKey
import java.util.Objects

fun AEKey.myHash(): Int {
    return Objects.hash(this.id, this.type.id)
}