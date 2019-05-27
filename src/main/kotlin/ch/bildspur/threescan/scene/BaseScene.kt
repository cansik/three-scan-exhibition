package ch.bildspur.threescan.scene

import ch.bildspur.threescan.Application
import ch.bildspur.threescan.model.config.AppConfig
import processing.core.PGraphics

abstract class BaseScene(val name: String, val app : Application) {

    var nextScene = this
    var sceneChangeProposed = false

    fun changeTo(scene : BaseScene) {
        nextScene = scene
        sceneChangeProposed = true
    }

    val config : AppConfig
        get() = app.config

    val sceneManager : SceneManager
        get() = app.sceneManager

    abstract fun setup()
    abstract fun start()
    abstract fun logic()
    abstract fun draw(g : PGraphics)
    abstract fun stop()
    abstract fun dispose()
}