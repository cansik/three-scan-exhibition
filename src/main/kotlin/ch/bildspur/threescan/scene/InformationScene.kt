package ch.bildspur.threescan.scene

import ch.bildspur.threescan.Application
import ch.bildspur.threescan.controller.timer.Timer
import ch.bildspur.threescan.controller.timer.TimerTask
import processing.core.PGraphics

class InformationScene(app : Application) : BaseScene("Information Scene", app) {

    private val timer = Timer()

    override fun setup() {
        timer.setup()
    }

    override fun start() {
        timer.addTask(TimerTask(app.config.informationWaitTime.value, {
            sceneChangeProposed = true
            nextScene = app.sceneManager.scanScene
        }), initTime = true)
    }

    override fun logic() {
        timer.update()
    }

    override fun draw(g : PGraphics) {
        g.background(22f)

    }

    override fun stop() {

    }

    override fun dispose() {

    }
}