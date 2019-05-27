package ch.bildspur.threescan.model.config

import com.google.gson.annotations.Expose

class AppConfig {
    @Expose var visual = VisualConfig()

    @Expose var io = IOConfig()
}