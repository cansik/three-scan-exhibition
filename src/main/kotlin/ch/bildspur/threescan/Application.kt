package ch.bildspur.threescan

import ch.bildspur.threescan.controller.PeasyController
import ch.bildspur.threescan.controller.PointCloudRenderer
import ch.bildspur.threescan.controller.timer.Timer
import ch.bildspur.threescan.controller.timer.TimerTask
import ch.bildspur.threescan.io.serial.ThreeScanClient
import ch.bildspur.threescan.model.config.AppConfig
import ch.bildspur.threescan.scene.SceneManager
import ch.bildspur.threescan.style.AppStyle
import ch.bildspur.threescan.thread.ProcessingInvoker
import ch.bildspur.threescan.thread.ProcessingTask
import processing.core.PApplet
import processing.core.PConstants
import kotlin.math.roundToInt

class Application(val config: AppConfig) : PApplet() {
    companion object {
        @JvmStatic
        val NAME = "Three Scan Exhibition Viewer"

        @JvmStatic
        val VERSION = "0.1.0"

        @JvmStatic
        val URI_NAME = "tscnviewer"

        @JvmStatic
        fun map(value: Double, start1: Double, stop1: Double, start2: Double, stop2: Double): Double {
            return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1))
        }

        @JvmStatic
        fun map(value: Int, start1: Int, stop1: Int, start2: Int, stop2: Int): Int {
            return map(
                value.toDouble(),
                start1.toDouble(),
                stop1.toDouble(),
                start2.toDouble(),
                stop2.toDouble()
            ).roundToInt()
        }
    }

    val scanner = ThreeScanClient(this)

    val pointCloudRenderer = PointCloudRenderer(this)

    val sceneManager = SceneManager(this)

    val style = AppStyle(this)

    val cam = PeasyController(this)

    var setupFinished = false

    val invoker = ProcessingInvoker()

    private val timer = Timer()

    var lastCursorMoveTime = 0
    var cursorHideTime = 1000 * 5L

    override fun settings() {
        super.settings()

        // setup main window
        if (config.visual.fullScreen.value)
            fullScreen(PConstants.P3D, config.visual.screenIndex.value)
        else
            size(config.visual.width.value, config.visual.height.value, PConstants.P3D)

        // setup screen density
        pixelDensity(config.visual.pixelDensity.value)
    }

    override fun setup() {
        super.setup()
        surface.setTitle("$NAME - $VERSION")
        frameRate(config.visual.frameRate.value.toFloat())

        // change clipping
        perspective((PConstants.PI / 3.0).toFloat(), width.toFloat() / height, 0.1f, 100000f)
    }

    override fun draw() {
        if(!setupFinished)
            setupControllers()

        background(0)
        sceneManager.update(this.g)
        timer.update()
        invoker.invokeTasks()

        ifDebug {
            showAxisMarker()
        }
    }

    private fun setupControllers() {
        // setup io
        scanner.open()

        // setup renderer
        pointCloudRenderer.setup()

        // setup app
        style.setup(this.g)
        cam.setup()
        sceneManager.setup()

        timer.setup()

        // timer for cursor hiding
        timer.addTask(TimerTask(cursorHideTime, {
            val current = millis()
            if (current - lastCursorMoveTime > cursorHideTime)
                noCursor()
        }, "CursorHide"))

        setupFinished = true
    }

    private fun showAxisMarker() {
        val axisLength = 200
        strokeWeight(3f)

        // x
        stroke(236f, 32f, 73f)
        line(0f, 0f, 0f, axisLength.toFloat(), 0f, 0f)
        text("X-axis", axisLength.toFloat(), 0f, 0f)

        // y
        stroke(47f, 149f, 153f)
        line(0f, 0f, 0f, 0f, axisLength.toFloat(), 0f)
        text("Y-axis", 0f, axisLength.toFloat(), 0f)

        // z
        stroke(247f, 219f, 79f)
        line(0f, 0f, 0f, 0f, 0f, axisLength.toFloat())
        text("Z-axis", 0f, 0f, axisLength.toFloat())
    }

    override fun stop() {
        scanner.close()
    }

    fun run() {
        runSketch()
    }

    override fun mouseMoved() {
        super.mouseMoved()
        cursor()
        lastCursorMoveTime = millis()
    }

    override fun keyPressed() {
        if (key == ' ') {
            sceneManager.scanScene.syncTimeoutTask.lastMillis = 0
        }
    }
}

fun Application.invokeOnProcessing(block : () -> Unit) {
    this.invoker.addTask(ProcessingTask(block))
}

fun Application.ifDebug(block : () -> Unit) {
    if(!this.config.debuggingMode.value) return
    block()
}