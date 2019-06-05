package ch.bildspur.threescan.text

import ch.bildspur.threescan.Application
import ch.bildspur.threescan.animation.Animation
import ch.bildspur.threescan.util.limit
import ch.bildspur.threescan.util.translate
import processing.core.PFont
import processing.core.PGraphics
import processing.core.PVector


class TextPlotter(val app : Application,
                  var text : String = "",
                  var position : PVector = PVector(),
                  var maxWidth: Float = 500f,
                  var fontSize : Float = app.style.screenTextSize(14f),
                  var lineSpace : Float = fontSize * 1.2f,
                  var allCaps : Boolean = false,
                  var textColor : Int = app.color(255),
                  var backgroundColor : Int = app.color(0),
                  var backgroundOverLap : Float = 0.1f,
                  var showAnimationDuration : Long = 1000 * 15L,
                  var hideAnimationDuration : Long = 1000 * 10L) {

    private lateinit var font : PFont
    var visible = true

    private var lines = emptyList<String>()

    private var showAnimation = Animation(app, showAnimationDuration)
    private var hideAnimation = Animation(app, hideAnimationDuration)

    fun setup() {
        font = app.createFont("SourceCodePro-Light.otf", fontSize)
        addNewText(text)

        hideAnimation.finished += {
            visible = false
        }
    }

    fun addNewText(value : String) {
        lines = wordWrap(value, maxWidth)
    }

    fun render(g : PGraphics) {
        showAnimation.update()
        hideAnimation.update()

        if(!visible)
            return

        g.push()
        g.textFont(font)
        g.translate(position)
        g.textSize(fontSize)
        g.fill(textColor)

        // showing mode
        if(showAnimation.running) {
            introAnimation(g)
        }

        // hiding mode
        if(hideAnimation.running) {
            outroAnimation(g)
        }

        // normal mode
        if(!showAnimation.running && !hideAnimation.running) {
            lines.forEachIndexed { i, line ->
                drawLine(g, preProcess(line), 0f, i * lineSpace)
            }
        }
        g.pop()
    }

    private fun drawLine(g: PGraphics, text : String, x : Float, y : Float) {
        val tfill = g.fillColor
        val overlap = backgroundOverLap * fontSize
        g.noStroke()
        g.fill(backgroundColor, g.alpha(tfill))
        g.rect(x, y - (overlap + fontSize), g.textWidth(text), fontSize + (2 * overlap))

        g.fill(tfill)
        g.text(text, x, y)
    }

    private fun introAnimation(g : PGraphics) {
        val normLine = 1.0 / lines.size

        lines.forEachIndexed { i, line ->
            g.fill(textColor, Application.map(showAnimation.value.toDouble(),
                normLine * i, normLine * i + normLine, 0.0, 255.0)
                .toFloat().limit(0.0f, 255.0f))
            drawLine(g, preProcess(line), 0f, i * lineSpace)
        }
    }

    private fun outroAnimation(g : PGraphics) {
        g.fill(textColor, Application.map(hideAnimation.value.toDouble(),
            0.0, 1.0, 255.0, 0.0).toFloat())

        lines.forEachIndexed { i, line ->
            drawLine(g, preProcess(line), 0f, i * lineSpace)
        }
    }

    fun update() {

    }

    fun hide() {
        hideAnimation.start()
    }

    fun show() {
        visible = true
        showAnimation.start()
    }

    private fun preProcess(text : String) : String {
        if(allCaps)
            return text.toUpperCase()

        return text
    }

    private fun wordWrap(text: String, maxWidth: Float): List<String> {
        val lines = mutableListOf<String>()
        val words = text.split(" ")

        if(words.isEmpty())
            return lines

        var currentLine = words.first()
        var wordIndex = 1

        while(wordIndex < words.size) {
            val currentWord = words[wordIndex]
            val propLine = "$currentLine $currentWord"

            currentLine = if(app.textWidth(propLine) > maxWidth) {
                // create new line
                lines.add(currentLine)
                currentWord
            } else {
                propLine
            }

            wordIndex++
        }

        lines.add(currentLine)
        return lines
    }
}