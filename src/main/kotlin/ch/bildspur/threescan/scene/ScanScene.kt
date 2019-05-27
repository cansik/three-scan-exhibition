package ch.bildspur.threescan.scene

import ch.bildspur.threescan.Application
import ch.bildspur.threescan.controller.PointCloudSync
import ch.bildspur.threescan.model.pointcloud.PointCloud
import processing.core.PConstants
import processing.core.PGraphics

class ScanScene(app : Application) : BaseScene("Scan Scene", app) {
    var pointCloud = PointCloud(app)

    var cloudSync = PointCloudSync(app.scanner, pointCloud)

    override fun setup() {
        app.scanner.onScanEnd += {
            println("end")
        }

        cloudSync.setup()
    }

    override fun start() {
        cloudSync.reset()
        pointCloud.create()

        // start scanning
        if(!app.scanner.scanning)
            app.scanner.startScan()
    }

    override fun logic() {
        cloudSync.update()
    }

    override fun draw(g : PGraphics) {
        g.background(22f)

        // render pointcloud
        app.pointCloudRenderer.render(g, pointCloud)

        app.cam.hud {
            app.style.h2()
            g.fill(255f)
            g.textAlign(PConstants.LEFT, PConstants.BOTTOM)
            g.text("scanning... Points: [${pointCloud.size}]", 20f, 30f)
        }
    }

    override fun stop() {

    }

    override fun dispose() {

    }
}