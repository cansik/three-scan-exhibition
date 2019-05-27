package ch.bildspur.threescan.scene

import ch.bildspur.threescan.Application
import processing.core.PGraphics

class SceneManager(app : Application) {

    // scenes
    val scanScene = ScanScene(app)
    val previewScene = PreviewScene(app)

    val scenes = listOf(scanScene, previewScene)
    var currentScene : BaseScene = scanScene

    fun setup() {
        scenes.forEach { it.setup() }
        currentScene.start()
    }

    fun update(g : PGraphics) {
        currentScene.logic()
        currentScene.draw(g)

        // check if scene wants to change
        if(currentScene.sceneChangeProposed)
        {
            currentScene.sceneChangeProposed = false
            changeScene(currentScene.nextScene)
        }
    }

    fun changeScene(scene : BaseScene) {
        currentScene.stop()
        currentScene = scene
        currentScene.start()
    }
}