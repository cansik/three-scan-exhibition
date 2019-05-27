package ch.bildspur.threescan.style

import ch.bildspur.threescan.Application
import processing.core.PFont
import processing.core.PGraphics

class AppStyle(private val app : Application) {

    private lateinit var g : PGraphics

    private lateinit var font : PFont

    fun setup(g : PGraphics) {
        this.g = g

        // setup font
        font = app.createFont("Helvetica", 30f)
        g.textFont(font)
    }

    fun h1() {
        g.textSize(30f)
    }

    fun h2() {
        g.textSize(22f)
    }

    fun text() {
        g.textSize(16f)
    }
}