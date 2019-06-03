package ch.bildspur.threescan.model.config

import ch.bildspur.threescan.Application
import com.google.gson.annotations.Expose

class AppConfig {
    @Expose var visual = VisualConfig()

    @Expose var io = IOConfig()

    @Expose var cloudCount : Int = 0

    @Expose var savePointClouds : Boolean = true

    @Expose var afterScanWaitTime : Long = 1000 * 30
}