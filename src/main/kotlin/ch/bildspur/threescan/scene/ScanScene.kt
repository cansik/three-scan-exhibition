package ch.bildspur.threescan.scene

import ch.bildspur.threescan.Application
import processing.core.PConstants
import processing.core.PGraphics

class ScanScene(app : Application) : BaseScene("Scan Scene", app) {
    override fun setup() {

    }

    override fun start() {

    }

    override fun logic() {

    }

    override fun draw(g : PGraphics) {
        g.background(22f)

        app.cam.hud {
            app.style.h2()
            g.fill(255f)
            g.textAlign(PConstants.CENTER, PConstants.CENTER)
            g.text("scanning...", g.width / 2.0f, g.height / 2.0f)
        }
    }

    override fun stop() {

    }

    override fun dispose() {

    }
}