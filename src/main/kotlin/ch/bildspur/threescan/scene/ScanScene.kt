package ch.bildspur.threescan.scene

import ch.bildspur.threescan.Application
import ch.bildspur.threescan.configuration.ConfigurationController
import ch.bildspur.threescan.controller.PointCloudSync
import ch.bildspur.threescan.controller.timer.Timer
import ch.bildspur.threescan.controller.timer.TimerTask
import ch.bildspur.threescan.ifDebug
import ch.bildspur.threescan.model.pointcloud.PointCloud
import ch.bildspur.threescan.text.TextPlotter
import ch.bildspur.threescan.util.format
import ch.bildspur.threescan.util.rotate
import ch.bildspur.threescan.util.translate
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PGraphics
import processing.core.PVector
import java.nio.file.Files
import java.nio.file.Paths

class ScanScene(app : Application) : BaseScene("Scan Scene", app) {
    private val savePath = Paths.get(System.getProperty("user.home"), "pointclouds")

    var pointCloud = PointCloud(app, 1024 * 60)

    var cloudSync = PointCloudSync(app.scanner, pointCloud,
        syncEveryPoint = true, syncLimited = true, syncPointLimit = 10)

    private lateinit var plotter : TextPlotter

    private val timer = Timer()

    // timeout task
    val syncTimeoutTask = TimerTask(45 * 1000, {
        if(!app.scanner.scanning)
            return@TimerTask

        // no sync in time
        println("sync timeout triggered, restarting scanner...")
        app.scanner.stopScan()
        Thread.sleep(500)
        app.scanner.close()
        Thread.sleep(5000)
        println("opening scanner...")
        app.scanner.open()
        Thread.sleep(1000)

        timer.taskList.forEach { tt ->
            tt.lastMillis = 0
        }
        timer.resetTask(it)

        println("switching to new scene")
        sceneChangeProposed = true
        nextScene = app.sceneManager.scanScene
    })

    override fun setup() {
        app.scanner.onScanEnd += {
            scanEnded()
        }

        app.scanner.onScanSync += {
            timer.resetTask(syncTimeoutTask)
        }

        // setup plotter
        val textSize = app.style.screenTextSize(30f)
        plotter = TextPlotter(app,
            text = app.config.informationText.value,
            fontSize = textSize,
            allCaps = false,
            lineSpace = textSize * 1.3f,
            maxWidth = 700f,
            position = PVector(30f, 100f)
        )

        cloudSync.setup()
        timer.setup()
        plotter.setup()

        // set pointcloud translation
        pointCloud.translation = PVector(0f, 80f, 0f)
        pointCloud.rotation = PVector(PApplet.radians(90f), 0f, 0f)

        // add sync task
        timer.addTask(TimerTask(100, {
            cloudSync.update()
        }))

        // add plotter task
        timer.addTask(TimerTask(100, {
            plotter.update()
        }))

        // add sync task
        timer.addTask(syncTimeoutTask, initTime = true)
    }

    override fun start() {
        cloudSync.reset()
        pointCloud.create()

        // start scanning
        if(app.scanner.running && !app.scanner.scanning)
            app.scanner.startScan()

        // setup information disappear
        plotter.show()

        timer.addTask(TimerTask(app.config.informationWaitTime.value, {
            plotter.hide()
            it.finished = true
        }), initTime = true)

        timer.resetTask(syncTimeoutTask)
    }

    override fun logic() {
        timer.update()
    }

    override fun draw(g : PGraphics) {
        g.background(12f)

        // auto rotate camera
        app.cam.cam.rotateY(app.config.cameraYRotationSpeed.value)


        if(app.config.demoMode.value.not()) {
            // highlight points
            if (app.scanner.scanning)
                highLightNewPoints(g)

            // render pointcloud
            app.pointCloudRenderer.render(g, pointCloud)
        } else {
            app.demoMode.renderDemo(g)
        }

        // render plot
        app.cam.hud {
            plotter.render(g)

            // render point count
            g.push()
            g.textFont(plotter.font)
            g.translate(g.width - 30f, g.height - 25f)
            g.textSize(plotter.fontSize * 0.5f)
            g.textAlign(PConstants.RIGHT, PConstants.CENTER)
            g.text("${pointCloud.size} pts - ${app.config.cloudCount.value} pcl", 0f, 0f)
            g.pop()
        }

        // draw hud
        app.ifDebug {
            app.cam.hud {
                app.style.text()
                g.fill(255f)
                g.textAlign(PConstants.LEFT, PConstants.BOTTOM)
                g.text(
                    "FPS: ${app.frameRate.format(2)} Displayed Points: [${pointCloud.size}] Actual Points: [${app.scanner.getVertexBuffer().size}]",
                    20f,
                    30f
                )
            }
        }
    }

    private fun highLightNewPoints(g: PGraphics) {
        // line test
        g.push()
        // set coordinates to current pointcloud
        g.translate(pointCloud.translation)
        g.rotate(pointCloud.rotation)
        g.scale(pointCloud.scale)

        for(i in 0 until 35) {
            val indexPointA = pointCloud.size - (1 + i)
            val indexPointB = pointCloud.size - (2 + i)

            if(indexPointB < 0)
                break

            val a = pointCloud.vertexBuffer.getVertex(indexPointA)
            val b = pointCloud.vertexBuffer.getVertex(indexPointB)

            g.strokeWeight(2.0f)
            g.noFill()
            g.stroke(255f, 0f, 0f)
            g.line(a.x, a.y, a.z, b.x, b.y, b.z)
        }
        g.pop()
    }

    private fun scanEnded() {
        // not in main thread!
        println("scan ended")

        // add wait task
        timer.addTask(TimerTask(app.config.afterScanWaitTime.value, {
            // switch scene
            println("switching to scan scene")
            sceneChangeProposed = true
            nextScene = app.sceneManager.scanScene
            it.finished = true
        }), true)

        // store pointcloud
        if(!app.config.savePointClouds.value)
            return

        if (!Files.exists(savePath)) {
            Files.createDirectories(savePath)
        }
        pointCloud.save(Paths.get(savePath.toString(), "pcl-${app.config.cloudCount.value}.ply").toString())

        // store cloud count
        val configuration = ConfigurationController()
        app.config.cloudCount.value++
        configuration.saveAppConfig(app.config)
    }


    override fun stop() {

    }

    override fun dispose() {

    }
}