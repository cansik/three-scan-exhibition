package ch.bildspur.threescan.scene

import ch.bildspur.threescan.Application
import ch.bildspur.threescan.controller.PointCloudSync
import ch.bildspur.threescan.controller.timer.Timer
import ch.bildspur.threescan.controller.timer.TimerTask
import ch.bildspur.threescan.model.pointcloud.PointCloud
import ch.bildspur.threescan.util.format
import com.sun.corba.se.impl.orbutil.graph.Graph
import processing.core.PConstants
import processing.core.PGraphics
import kotlin.concurrent.thread

class ScanScene(app : Application) : BaseScene("Scan Scene", app) {
    var pointCloud = PointCloud(app, 1024 * 15)

    var cloudSync = PointCloudSync(app.scanner, pointCloud,
        syncEveryPoint = true, syncLimited = true, syncPointLimit = 10)

    val syncTimer = Timer()

    override fun setup() {
        app.scanner.onScanEnd += {
            println("end")
        }

        cloudSync.setup()
        syncTimer.setup()

        // add sync task
        syncTimer.addTask(TimerTask(100, {
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
        syncTimer.update()
    }

    override fun draw(g : PGraphics) {
        g.background(22f)

        highLightNewPoints(g)

        // render pointcloud
        app.pointCloudRenderer.render(g, pointCloud)

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


    override fun stop() {

    }

    override fun dispose() {

    }
}