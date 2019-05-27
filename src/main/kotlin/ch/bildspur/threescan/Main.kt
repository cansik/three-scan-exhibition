package ch.bildspur.threescan

import ch.bildspur.threescan.configuration.ConfigurationController

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Main().startApplication(args)
        }
    }

    fun startApplication(args: Array<String>) {
        val configuration = ConfigurationController()
        val appConfig = configuration.loadAppConfig()

        val viewer = Application(appConfig)
        viewer.run()
    }
}