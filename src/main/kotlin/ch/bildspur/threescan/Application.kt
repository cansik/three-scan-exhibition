package ch.bildspur.threescan

import ch.bildspur.threescan.controller.PeasyController
import ch.bildspur.threescan.controller.PointCloudRenderer
import ch.bildspur.threescan.io.serial.ThreeScanClient
import ch.bildspur.threescan.model.config.AppConfig
import ch.bildspur.threescan.scene.SceneManager
import ch.bildspur.threescan.style.AppStyle
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

        // setup style
        // change clipping
        //perspective((PConstants.PI / 3.0).toFloat(), width.toFloat() / height, 0.1f, 100000f)

        // setup io
        scanner.open()

        // setup renderer
        pointCloudRenderer.setup()

        // setup app
        style.setup(this.g)
        cam.setup()
        sceneManager.setup()
    }

    override fun draw() {
        background(0)
        sceneManager.update(this.g)
    }

    override fun stop() {
        scanner.close()
    }

    fun run() {
        runSketch()
    }
}