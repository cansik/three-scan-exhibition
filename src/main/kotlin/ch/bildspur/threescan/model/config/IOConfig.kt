package ch.bildspur.threescan.model.config

import ch.bildspur.threescan.model.DataModel
import com.google.gson.annotations.Expose

class IOConfig {

    @Expose
    var devicePort = DataModel("/dev/tty.SLAB_USBtoUART")

    @Expose
    var baudRate = DataModel(115200)
}