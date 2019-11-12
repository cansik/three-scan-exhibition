package ch.bildspur.threescan.model.pointcloud

import org.jengineering.sjmply.PLY
import org.jengineering.sjmply.PLYElementList
import org.jengineering.sjmply.PLYFormat
import org.jengineering.sjmply.PLYType.FLOAT32
import org.jengineering.sjmply.PLYType.UINT8
import processing.core.PApplet
import processing.core.PConstants.POINTS
import processing.core.PShape
import processing.core.PVector
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

    fun load(fileName: String) {
        val path = Paths.get(fileName)
        var ply = PLY()

        try {
            ply = PLY.load(path)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        val vertex = ply.elements("vertex")

        // coordinates
        val x = vertex.property(FLOAT32, "x")
        val y = vertex.property(FLOAT32, "y")
        val z = vertex.property(FLOAT32, "z")

        // colors
        var r = ByteArray(0)
        var g = ByteArray(0)
        var b = ByteArray(0)
        var colorLoaded = false

        try {
            r = vertex.property(UINT8, "red")
            g = vertex.property(UINT8, "green")
            b = vertex.property(UINT8, "blue")

            colorLoaded = true
        } catch (ex: Exception) {
            println("no color information!")
        }

        vertexBuffer = app.createShape()
        vertexBuffer.beginShape(POINTS)

        for (i in x.indices) {
            vertexBuffer.strokeWeight(1.0f)

            if (colorLoaded) {
                val rv = r[i].toInt() and 0xFF
                val gv = g[i].toInt() and 0xFF
                val bv = b[i].toInt() and 0xFF
                vertexBuffer.stroke(rv.toFloat(), gv.toFloat(), bv.toFloat())
            } else {
                vertexBuffer.stroke(255)
            }
            vertexBuffer.vertex(x[i], -y[i], z[i])
        }

        vertexBuffer.endShape()
    }
}