package ch.bildspur.threescan.model.pointcloud

import processing.core.PApplet
import processing.core.PConstants.POINTS
import processing.core.PShape
import processing.core.PVector
import jdk.nashorn.internal.runtime.regexp.joni.encoding.CharacterType.ASCII
import java.awt.Color.blue
import java.awt.Color.green
import java.awt.Color.red
import org.jengineering.sjmply.PLYType.UINT8
import org.jengineering.sjmply.PLYType.FLOAT32
import org.jengineering.sjmply.PLYElementList
import org.jengineering.sjmply.PLY
import org.jengineering.sjmply.PLYFormat
import java.nio.file.Paths
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


class PointCloud(val app : PApplet, private val bufferSize : Int = 1024 * 30) {
    var scale : Float = 1.0f
    var translation = PVector()
    var rotation = PVector()

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
        vertexBuffer.setStroke(index, app.color(vertex.signalStrength, 0, 0, 0))
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

    fun save(fileName: String, cloudIndex : Int = 0) {
        val path = Paths.get(fileName)
        val ply = PLY()

        val dateTime = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now())

        var cmdIndex = 3
        ply.comments[cmdIndex++] = "Cloud Index: $cloudIndex"
        ply.comments[cmdIndex] = "Time: $dateTime"

        val vertex = PLYElementList(size)

        // coordinates
        val x = vertex.addProperty(FLOAT32, "x")
        val y = vertex.addProperty(FLOAT32, "y")
        val z = vertex.addProperty(FLOAT32, "z")

        // colors
        val r = vertex.addProperty(UINT8, "red")
        val g = vertex.addProperty(UINT8, "green")
        val b = vertex.addProperty(UINT8, "blue")

        for (i in 0 until size) {
            val v = vertexBuffer.getVertex(i)
            val c = vertexBuffer.getFill(i)

            // coordinates
            x[i] = v.x
            y[i] = v.y
            z[i] = v.z

            // colors
            r[i] = app.red(c).toByte()
            g[i] = app.green(c).toByte()
            b[i] = app.blue(c).toByte()
        }

        ply.elements["vertex"] = vertex
        ply.format = PLYFormat.ASCII

        try {
            ply.save(path)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}