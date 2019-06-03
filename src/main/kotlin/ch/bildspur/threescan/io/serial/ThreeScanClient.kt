package ch.bildspur.threescan.io.serial

import ch.bildspur.threescan.Application
import ch.bildspur.threescan.event.Event
import ch.bildspur.threescan.model.pointcloud.Vertex
import processing.core.PVector
import java.lang.Float.parseFloat
import java.lang.Integer.parseInt
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread


class ThreeScanClient(val app : Application) {
    private val mcu = SerialClient(app)

    private var communicationThread = Thread()

    @Volatile var running = false
        private set

    @Volatile var scanning = false
        private set

    // todo: maybe split up into public and private vertex buffer
    private val vertexBuffer = CopyOnWriteArrayList<Vertex>()

    // events
    val onScanSync = Event<Int>()
    val onScanData = Event<Int>()
    val onScanEnd = Event<Int>()

    var displayScannerDebugMessages = true

    fun getVertexBuffer() : List<Vertex> {
        return vertexBuffer
    }

    fun open() : Boolean {
        if(!mcu.attach())
            return false

        running = true

        communicationThread = thread(isDaemon = true) {
            while (running) {
                Thread.sleep(1)
                readSerialData()
            }
        }

        return true
    }

    fun close() {
        mcu.detach()

        running = false
        communicationThread.join(5000)
    }

    fun startScan() {
        vertexBuffer.clear()

        mcu.writeCommand("CMD:SET:ST:1")
        Thread.sleep(100)
        mcu.writeCommand("CMD:START")

        scanning = true
    }

    fun stopScan() {
        mcu.writeCommand("CMD:STOP")
        scanning = false
    }

    private fun readSerialData() {
        if (!mcu.isAttached())
            return

        val raw = (mcu.readData() ?: return).trim()

        if(raw.isBlank())
            return

        if(raw.startsWith("TST"))
        {
            // relevant data
            val data = raw.split(":")
            //val header = data[0].trim()
            val cmd = data[1].trim()

            try {
                runCommand(cmd, data.drop(2))
            } catch (ex : Exception) {
                println("Parse Exception: ${ex.message}")
            }
        }
        else {
            if(displayScannerDebugMessages)
                println("MCU: $raw")
        }
    }

    private fun runCommand(cmd : String, data : List<String>) {
        when(cmd) {
            "SYN" -> {
                onScanSync(vertexBuffer.size - 1)
            }

            "DAT" -> {
                // data point
                val x = parseFloat(data[0].trim())
                val y = parseFloat(data[1].trim())
                val z = parseFloat(data[2].trim())
                val signalStrength = parseInt(data[3].trim())

                vertexBuffer.add(Vertex(PVector(x, y, z), signalStrength))
                onScanData(vertexBuffer.size - 1)
            }

            "END" -> {
                scanning = false
                onScanEnd(vertexBuffer.size - 1)
            }
        }
    }
}