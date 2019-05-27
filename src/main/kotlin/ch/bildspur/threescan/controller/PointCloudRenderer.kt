package ch.bildspur.threescan.controller

import ch.bildspur.threescan.Application
import ch.bildspur.threescan.model.pointcloud.PointCloud
import processing.core.PGraphics
import processing.opengl.PShader

class PointCloudRenderer(val app : Application) {
    lateinit var pointShader : PShader

    var colorMix = 0.0f
    var pointColor = app.color(255)
    var pointScale = 2.0f

    fun setup() {
        pointShader = app.loadShader("shader/pointColor.glsl", "shader/pointVertex.glsl")
    }

    fun render(g: PGraphics, cloud : PointCloud) {
        // enable shader
        g.shader(pointShader)
        g.push()
        g.translate(cloud.position.x, cloud.position.y, cloud.position.z)
        g.scale(cloud.scale)

        // set shader uniforms
        pointShader.set("pointScale", pointScale)
        pointShader.set("pointColor",
            app.red(pointColor) / 255f,
            app.green(pointColor) / 255f,
            app.blue(pointColor) / 255f,
            app.alpha(pointColor) / 255f)
        pointShader.set("colorMix", colorMix)

        // draw cloud
        g.shape(cloud.vertexBuffer)

        // disable shader
        g.pop();
        g.resetShader();
    }
}