package ch.bildspur.threescan.controller

import ch.bildspur.threescan.Application
import ch.bildspur.threescan.model.pointcloud.PointCloud
import processing.core.PGraphics
import processing.core.PShape

class DemoMode(val app : Application) {
    private val cloud = PointCloud(app, 1)

    fun setup() {
        println("loading demo cloud...")
        cloud.load(app.config.demoCloud.value)
        println("loading done!")
    }

    fun renderDemo(g : PGraphics) {
        g.push()
        // todo: create applications for this
        g.scale(20f)
        g.translate(0f, 10f, 0f)
        g.shape(cloud.vertexBuffer)
        g.pop()
    }
}