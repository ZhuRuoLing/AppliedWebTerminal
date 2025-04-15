package icu.takeneko.appwebterminal.client.rendering

import net.minecraft.resources.ResourceLocation
import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JProgressBar

class JProgressWindow(
    val title: String
) : RenderProgressListener {
    private val window: JFrame
    private val progressText: JLabel
    private val progressBar: JProgressBar
    private var totalCount:Int = 0

    init {
        window = JFrame(title)
        window.setSize(450, 200)
        val pane = JPanel()
        progressText = JLabel()
        progressBar = JProgressBar()
        progressText.text = "Progress: ?"
        pane.layout = BoxLayout(pane, BoxLayout.Y_AXIS)
        pane.add(progressText)
        pane.add(progressBar)
        window.add(pane)
        window.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        progressBar.isIndeterminate = true
        progressBar.isStringPainted = true
    }

    override fun notifyTotalCount(size: Int) {
        progressBar.isIndeterminate = false
        totalCount = size
        progressText.text = "Progress: $totalCount/?"
        progressBar.minimum = 0
        progressBar.maximum = totalCount
        progressBar.value = 0
    }

    override fun notifyProgress(current: Int, name: ResourceLocation) {
        progressText.text = "Progress: $totalCount/$current Current: $name"
        progressBar.value = current
    }

    fun show() {
        window.isVisible = true
    }

    fun dismiss() {
        window.isVisible = false
        window.dispose()
    }
}