package ch.bildspur.threescan.model.config

import ch.bildspur.threescan.model.DataModel
import com.google.gson.annotations.Expose

class AppConfig {
    @Expose var visual = VisualConfig()

    @Expose var io = IOConfig()

    @Expose var cloudCount : Int = 0

    @Expose var savePointClouds = DataModel(true)

    @Expose var afterScanWaitTime = DataModel(1000L * 60)

    @Expose var informationWaitTime = DataModel(1000L * 30)

    @Expose var camerYRotationSpeed = DataModel(0.001)
}