package ch.bildspur.threescan.io.serial

import ch.bildspur.threescan.Application
import com.sun.org.apache.xpath.internal.operations.Bool
import processing.serial.Serial

class SerialClient(val app : Application) {
    var port: Serial? = null

    fun isAttached(): Boolean {
        return null != port
    }

    fun attach() : Boolean {
        return try {
            port = Serial(app, app.config.io.devicePort.value, app.config.io.baudRate.value)
            true
        } catch (ex: Exception) {
            println("Serial Error: " + ex.message)
            false
        }
    }

    fun writeCommand(data: String) {
        port!!.write(data + "\n")
    }

    fun readData(): String? {
        return if (port!!.available() > 0) port!!.readStringUntil('\n'.toInt()) else null
    }

    fun detach() {
        port!!.stop()
        port!!.clear()
        port = null
    }
}