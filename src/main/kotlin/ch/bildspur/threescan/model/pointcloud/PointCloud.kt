package ch.bildspur.threescan.model.pointcloud

import processing.core.PApplet
import processing.core.PConstants.POINTS
import processing.core.PShape
import processing.core.PVector

class PointCloud(val app : PApplet, val bufferSize : Int = 1024 * 30) {
    var scale : Float = 1.0f
    var position = PVector()

    var size = 0
        private set

    private enum class PointCloudAttributes(val shaderName : String) {
        INTENSITY("intensity")
    }

    var vertexBuffer = PShape()
        private set

    fun create() {
        size = 0
        initShape()
    }

    fun addVertex(vertex: Vertex) {
        val index = size
        vertexBuffer.setAttrib(PointCloudAttributes.INTENSITY.shaderName, index, vertex.signalStrength / 255f)
        vertexBuffer.setStroke(index, app.color(vertex.signalStrength, 0, 255, 255))
        vertexBuffer.setVertex(index, vertex.position)
        size++
    }

    private fun initShape() {
        vertexBuffer = app.createShape()
        vertexBuffer.beginShape(POINTS)

        // add additional attributes
        vertexBuffer.attrib(PointCloudAttributes.INTENSITY.shaderName, 0.5f)

        // create vbo
        for (i in 0 until bufferSize) {
            vertexBuffer.stroke(app.color(0, 0))
            vertexBuffer.vertex(0f, 0f, 0f)
        }

        vertexBuffer.endShape()
    }
}