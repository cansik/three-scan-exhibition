package ch.bildspur.threescan.animation

import ch.bildspur.threescan.event.Event
import processing.core.PApplet


class Animation(private val app : PApplet, var duration : Long) {

    val finished = Event<Animation>()

    var running = false
        private set

    private var startTimeStamp = 0

    var value : Float = 0.0f
        private set

    fun start() {
        startTimeStamp = app.millis()
        running = true
    }

    fun stop() {
        running = false
    }

    fun update() {
        if(!running) return

        val delta = app.millis() - startTimeStamp
        value = PApplet.map(delta.toFloat(), 0.0f, duration.toFloat(), 0.0f, 1.0f)

        // check stop
        if(value >= 1.0f) {
            running = false
            value = 1.0f
            finished(this)
        }
    }
}