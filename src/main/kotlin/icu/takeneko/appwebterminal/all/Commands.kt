package icu.takeneko.appwebterminal.all

import appeng.api.stacks.AEKey
import icu.takeneko.appwebterminal.client.rendering.AEKeyRenderer
import icu.takeneko.appwebterminal.client.rendering.JProgressWindow
import icu.takeneko.appwebterminal.client.rendering.RenderProgressListener
import icu.takeneko.appwebterminal.util.LiteralCommand
import icu.takeneko.appwebterminal.util.execute
import icu.takeneko.appwebterminal.util.literal
import icu.takeneko.appwebterminal.util.sendError
import icu.takeneko.appwebterminal.util.sendFeedback
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraftforge.client.event.RegisterClientCommandsEvent
import org.slf4j.LoggerFactory
import java.awt.HeadlessException
import kotlin.io.path.Path

val AppWebTerminalCommand = LiteralCommand("appwebterminal") {
    literal("resources") {
        literal("upload") {
            execute {
                val src = this.source
                if (Minecraft.getInstance().isLocalServer) {
                    sendError(Component.translatable("appwebterminal.message.join_server_required"))
                    return@execute 1
                }

                0
            }
        }
        literal("renderLocal") {
            execute {
                System.setProperty("java.awt.headless", "false")
                val progressListener = ProgressListenerImpl()
                progressListener.progressWindow?.show()
                AEKeyRenderer(256, 256).apply {
                    renderAll(Path("./aeKeyResources"), progressListener)
                    dispose()
                }
                progressListener.progressWindow?.dismiss()
                1
            }
        }
        literal("listRegistered") {
            execute {
                KeyImageProviderRegistry.keys.forEach {
                    sendFeedback(Component.literal(it.toString()))
                }
                return@execute 1
            }
        }
    }
}

private class ProgressListenerImpl : RenderProgressListener {
    private var total: Int = 0
    private val logger = LoggerFactory.getLogger("RenderProgress")
    val progressWindow: JProgressWindow? = try {
        JProgressWindow("Rendering Resources")
    } catch (_: HeadlessException) {
        null
    }

    override fun notifyTotalCount(size: Int) {
        logger.info("Total: $size")
        total = size
        progressWindow?.notifyTotalCount(size)
    }

    override fun notifyProgress(current: Int, what: AEKey) {
        logger.info("Progress: $current/$total, Current: ${what.type}/${what.id}")
        progressWindow?.notifyProgress(current, what)
    }

    override fun notifyCompleted() {
        logger.info("Render Completed")
        progressWindow?.notifyCompleted()
    }
}

fun registerClientCommand(event: RegisterClientCommandsEvent) {
    event.dispatcher.register(AppWebTerminalCommand.node)
}