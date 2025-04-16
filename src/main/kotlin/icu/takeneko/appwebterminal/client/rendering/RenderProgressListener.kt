package icu.takeneko.appwebterminal.client.rendering

import appeng.api.stacks.AEKey

interface RenderProgressListener {
    fun notifyTotalCount(size: Int)

    fun notifyProgress(current: Int, what: AEKey)

    fun notifyCompleted()
}