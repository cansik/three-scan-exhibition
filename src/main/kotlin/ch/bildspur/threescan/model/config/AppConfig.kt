package ch.bildspur.threescan.model.config

import ch.bildspur.threescan.model.DataModel
import com.google.gson.annotations.Expose

class AppConfig {
    @Expose var debuggingMode = DataModel(true)

    @Expose var visual = VisualConfig()

    @Expose var io = IOConfig()

    @Expose var cloudCount = DataModel(0)

    @Expose var pointSize = DataModel(2f)

    @Expose var demoMode = DataModel(true)

    @Expose var demoCloud = DataModel("data/Platzspitz_700k.ply")

    @Expose var savePointClouds = DataModel(true)

    @Expose var afterScanWaitTime = DataModel(1000L * 90)

    @Expose var informationWaitTime = DataModel(1000L * 60)

    @Expose var camerYRotationSpeed = DataModel(0.001)

    @Expose var informationText = DataModel("""
        To generate an inclusive debate about ethical rules for machines and their influences on everyone's life, the understanding of how these devices work is crucial. Deep Vision examines and illustrates the interface between machine perception and human visual understanding through experiments in the fields of spatial computing, data processing, and information embodiment.
    """.trimIndent())
}