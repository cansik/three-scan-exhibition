package ch.bildspur.threescan.style

import ch.bildspur.threescan.Application
import processing.core.PFont
import processing.core.PGraphics

class AppStyle(private val app : Application,
               val defaultFontSize : Float = 30f,
               val defaultWidth : Float = 1024f) {

    private lateinit var g : PGraphics

    private lateinit var font : PFont

    var fontSizeMultiplier = 1.0f

    fun setup(g : PGraphics) {
        this.g = g

        // setup font
        font = app.createFont("Helvetica", defaultFontSize)
        g.textFont(font)

        fontSizeMultiplier = app.width / defaultWidth
    }

    fun screenTextSize(size : Float) : Float {
        return size * fontSizeMultiplier
    }

    fun h1() {
        g.textSize(screenTextSize(30f))
    }

    fun h2() {
        g.textSize(screenTextSize(22f))
    }

    fun text() {
        g.textSize(screenTextSize(16f))
    }
}