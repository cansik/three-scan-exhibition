package ch.bildspur.threescan.thread

data class ProcessingTask(val block : () -> Unit)