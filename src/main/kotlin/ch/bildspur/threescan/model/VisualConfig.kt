package ch.bildspur.threescan.model

import com.google.gson.annotations.Expose

class VisualConfig {
    @Expose
    var fullScreen = DataModel(false)

    @Expose
    var screenIndex = DataModel(0)

    @Expose
    var width = DataModel(1024)

    @Expose
    var height = DataModel(768)

    @Expose
    var frameRate = DataModel(60)

    @Expose
    var pixelDensity = DataModel(2)
}