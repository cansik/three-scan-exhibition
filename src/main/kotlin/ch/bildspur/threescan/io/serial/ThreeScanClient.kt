package ch.bildspur.threescan.io.serial

import ch.bildspur.threescan.Application
import ch.bildspur.threescan.event.Event
import ch.bildspur.threescan.model.pointcloud.Vertex
import kotlin.concurrent.thread
import processing.core.PVector
import java.lang.Exception
import java.lang.Float.parseFloat
import java.lang.Integer.parseInt


class ThreeScanClient(val app : Application) {
    private val mcu = SerialClient(app)

    private var communicationThread = Thread()

    @Volatile var running = false
        private set

    @Volatile var scanning = false
        private set

    // events
    val onScanSync = Event<Int>()
    val onScanData = Event<Int>()
    val onScanEnd = Event<Int>()

    var displayScannerDebugMessages = true

    fun open() {
        mcu.attach()
        running = true

        communicationThread = thread(isDaemon = true) {
            while (running) {
                Thread.sleep(1)
                readSerialData()
            }
        }
    }

    fun close() {
        mcu.detach()

        running = false
        communicationThread.join(5000)
    }

    fun startScan() {
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
                onScanSync(0)
            }

            "DAT" -> {
                // data point
                val x = parseFloat(data[0].trim())
                val y = parseFloat(data[1].trim())
                val z = parseFloat(data[2].trim())
                val signalStrength = parseInt(data[3].trim())

                //vbo.add(Vertex(PVector(x, y, z), signalStrength))

                onScanData(0)
            }

            "END" -> {
                scanning = false
                onScanEnd(0)
            }
        }
    }
}