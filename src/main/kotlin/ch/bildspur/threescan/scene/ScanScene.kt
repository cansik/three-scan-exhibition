package ch.bildspur.threescan.scene

import ch.bildspur.threescan.Application
import ch.bildspur.threescan.configuration.ConfigurationController
import ch.bildspur.threescan.controller.PointCloudSync
import ch.bildspur.threescan.controller.timer.Timer
import ch.bildspur.threescan.controller.timer.TimerTask
import ch.bildspur.threescan.model.pointcloud.PointCloud
import ch.bildspur.threescan.util.format
import processing.core.PConstants
import processing.core.PGraphics
import java.nio.file.Files
import java.nio.file.Paths

class ScanScene(app : Application) : BaseScene("Scan Scene", app) {
    private val savePath = Paths.get(System.getProperty("user.home"), "pointclouds")

    var pointCloud = PointCloud(app, 1024 * 15)

    var cloudSync = PointCloudSync(app.scanner, pointCloud,
        syncEveryPoint = true, syncLimited = true, syncPointLimit = 10)

    private val scanTimer = Timer()

    override fun setup() {
        app.scanner.onScanEnd += {
            scanEnded()
        }

        cloudSync.setup()
        scanTimer.setup()

        // add sync task
        scanTimer.addTask(TimerTask(100, {
            cloudSync.update()
        }))
    }

    override fun start() {
        cloudSync.reset()
        pointCloud.create()

        // start scanning
        if(!app.scanner.scanning)
            app.scanner.startScan()
    }

    override fun logic() {
        scanTimer.update()
    }

    override fun draw(g : PGraphics) {
        g.background(22f)

        // highlight points
        if(app.scanner.scanning)
            highLightNewPoints(g)

        app.cam.cam.rotateY(0.001)

        // render pointcloud
        app.pointCloudRenderer.render(g, pointCloud)

        // draw hud
        app.cam.hud {
            app.style.text()
            g.fill(255f)
            g.textAlign(PConstants.LEFT, PConstants.BOTTOM)
            g.text("FPS: ${app.frameRate.format(2)} Displayed Points: [${pointCloud.size}] Actual Points: [${app.scanner.getVertexBuffer().size}]", 20f, 30f)
        }
    }

    private fun showLinesFromScanner(g: PGraphics) {
        // line test
        for(i in 0 until 35) {
            val index = pointCloud.size - (1 + i)

            if(index < 0)
                break

            val v = pointCloud.vertexBuffer.getVertex(index)

            g.strokeWeight(0.5f)
            g.noFill()
            g.stroke(255)
            g.line(0f, 0f, 0f, v.x, v.y, v.z)
        }
    }

    private fun highLightNewPoints(g: PGraphics) {
        // line test
        for(i in 0 until 35) {
            val indexPointA = pointCloud.size - (1 + i)
            val indexPointB = pointCloud.size - (2 + i)

            if(indexPointB < 0)
                break

            val a = pointCloud.vertexBuffer.getVertex(indexPointA)
            val b = pointCloud.vertexBuffer.getVertex(indexPointB)

            g.strokeWeight(0.5f)
            g.noFill()
            g.stroke(255)
            g.line(a.x, a.y, a.z, b.x, b.y, b.z)
        }
    }

    private fun scanEnded() {
        println("scan ended")

        // add wait task
        scanTimer.addTask(TimerTask(app.config.afterScanWaitTime, {
            // switch scene
            println("switching to information scene")
            sceneChangeProposed = true
            nextScene = app.sceneManager.informationScene
            it.finished = true
        }))

        // store pointcloud
        if(!app.config.savePointClouds)
            return

        if (!Files.exists(savePath)) {
            Files.createDirectories(savePath)
        }
        pointCloud.save(Paths.get(savePath.toString(), "pcl-${app.config.cloudCount}.ply").toString())

        // store cloud count
        val configuration = ConfigurationController()
        app.config.cloudCount++
        configuration.saveAppConfig(app.config)
    }


    override fun stop() {

    }

    override fun dispose() {

    }
}