package ch.bildspur.threescan

import ch.bildspur.threescan.configuration.ConfigurationController
import ch.bildspur.threescan.model.config.AppConfig
import java.lang.Exception

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Main().startApplication(args)
        }
    }

    fun startApplication(args: Array<String>) {
        val configuration = ConfigurationController()
        val appConfig = try {
            configuration.loadAppConfig()
        } catch (ex : Exception) {
            println("Could not load configuration: ${ex.message}")
            AppConfig()
        }

        val viewer = Application(appConfig)
        viewer.run()
    }
}