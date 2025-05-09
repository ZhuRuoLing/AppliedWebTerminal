package icu.takeneko.appwebterminal.all

import appeng.api.stacks.AEKey
import icu.takeneko.appwebterminal.client.rendering.AEKeyRenderer
import icu.takeneko.appwebterminal.client.rendering.JProgressWindow
import icu.takeneko.appwebterminal.client.rendering.RenderProgressListener
import icu.takeneko.appwebterminal.util.CommandContext
import icu.takeneko.appwebterminal.util.LiteralCommand
import icu.takeneko.appwebterminal.util.S
import icu.takeneko.appwebterminal.util.execute
import icu.takeneko.appwebterminal.util.integerArgument
import icu.takeneko.appwebterminal.util.literal
import icu.takeneko.appwebterminal.util.sendError
import icu.takeneko.appwebterminal.util.sendFeedback
import net.minecraft.client.Minecraft
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraftforge.client.event.RegisterClientCommandsEvent
import org.slf4j.LoggerFactory
import java.awt.HeadlessException
import kotlin.io.path.Path

val AppWebTerminalCommand = LiteralCommand("appwebterminal") {
    literal("resources") {
        literal("upload") {
            execute {
                val src: CommandSourceStack by this
                if (Minecraft.getInstance().isLocalServer) {
                    sendError(Component.translatable("appwebterminal.message.join_server_required"))
                    return@execute 1
                }

                0
            }
        }
        literal("render") {
            integerArgument("limit") {
                integerArgument("size") {
                    execute {
                        val limit: Int by this
                        val size: Int by this
                        executeRender(limit, size)
                    }
                }
                execute {
                    val limit: Int by this
                    executeRender(limit, 256)
                }
            }
            execute {
                executeRender(1, 256)
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

private class ProgressListenerImpl(private val src: CommandSourceStack) : RenderProgressListener {
    private var total: Int = 0
    private val logger = LoggerFactory.getLogger("RenderProgress")
    val progressWindow: JProgressWindow? = try {
        JProgressWindow("Rendering Resources")
    } catch (_: HeadlessException) {
        null
    }

    override fun notifyTotalCount(size: Int) {
        logger.info("Total: $size")
        src.sendSystemMessage(Component.literal("Total: $size"))
        total = size
        progressWindow?.notifyTotalCount(size)
    }

    override fun notifyProgress(current: Int, what: AEKey) {
        logger.info("Progress: $current/$total, Current: ${what.type}/${what.id}")
        src.sendSystemMessage(Component.literal("Progress: $current/$total, Current: ${what.type}/${what.id}"))
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

fun CommandContext<S>.executeRender(limit: Int, size: Int): Int {
    if (AEKeyRenderer.instance != null) {
        sendError(Component.translatable("appwebterminal.message.rendering"))
        return 1
    }
    System.setProperty("java.awt.headless", "false")
    val progressListener = ProgressListenerImpl(this.delegate.source)
    progressListener.progressWindow?.show()
    val renderer = AEKeyRenderer(size, size).apply {
        submitRenderTasks(Path("./aeKeyResources"), progressListener)
    }
    renderer.taskPullLimit = limit
    renderer.renderTasks += {
        sendFeedback(Component.translatable("appwebterminal.message.render_complete"))
        renderer.dispose()
        AEKeyRenderer.instance = null
    }
    AEKeyRenderer.instance = renderer
    progressListener.progressWindow?.dismiss()
    sendFeedback(Component.translatable("appwebterminal.message.started"))
    return 1
}