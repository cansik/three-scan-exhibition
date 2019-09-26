package ch.bildspur.threescan.controller

import processing.core.PGraphics
import processing.core.PShape

class DemoMode {
    private lateinit var cloud : PShape

    fun setup() {

    }

    fun renderDemo(g : PGraphics) {
        g.shape(cloud)
    }
}