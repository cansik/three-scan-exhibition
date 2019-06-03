package ch.bildspur.threescan.text

import ch.bildspur.threescan.util.translate
import processing.core.PApplet
import processing.core.PFont
import processing.core.PGraphics
import processing.core.PVector

class TextPlotter(val app : PApplet,
                  var text : String = "",
                  var position : PVector = PVector(),
                  var fontSize : Float = 14f,
                  var textColor : Int = app.color(255),
                  var backgroundColor : Int = app.color(0, 0)) {

    private lateinit var font : PFont
    var visible = true

    fun setup() {
        font = app.createFont("SourceCodePro-Bold.otf", fontSize)
    }

    fun render(g : PGraphics) {
        if(!visible)
            return

        g.push()
        g.textFont(font)
        g.translate(position)
        g.textSize(fontSize)
        g.fill(textColor)
        g.text(text, 0f, 0f)
        g.pop()
    }

    fun update() {

    }

    fun hide() {
        visible = false
    }

    fun show() {
        visible = true
    }
}