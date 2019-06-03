package ch.bildspur.threescan.controller.timer

/**
 * Created by cansik on 12.07.17.
 */
class Timer {
    var taskList = mutableListOf<TimerTask>()

    fun setup() {

    }

    fun update() {
        taskList.forEach {
            val time = millis()
            if (time - it.lastMillis > it.interval) {
                it.lastMillis = time
                it.block(it)
            }
        }
        taskList = taskList.filter { !it.finished }.toMutableList()
    }

    fun addTask(task: TimerTask, initTime : Boolean = false) {
        if(initTime)
            task.lastMillis = millis()
        taskList.add(task)
    }

    private fun millis(): Long {
        return System.currentTimeMillis()
    }
}