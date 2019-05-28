package ch.bildspur.threescan.controller

import ch.bildspur.threescan.io.serial.ThreeScanClient
import ch.bildspur.threescan.model.pointcloud.PointCloud

class PointCloudSync(var scanner : ThreeScanClient,
                     var pointCloud : PointCloud,
                     var syncEveryPoint : Boolean = false,
                     var syncLimited : Boolean = false) {

    @Volatile private var currentIndex = 0
    private var syncIndex = 0

    fun setup() {
        // setup sync event
        scanner.onScanSync += {
            if(!syncEveryPoint)
                syncReceived(it)
        }

        // setup every point event
        scanner.onScanData += {
            if(syncEveryPoint)
                syncReceived(it)
        }
        reset()
    }

    fun update() {
        // main thread
        if(currentIndex != syncIndex) {
            if(syncLimited) {
                syncPointsLimited(1)
            }
            else {
                syncPoints()
            }
        }
    }

    fun reset() {
        currentIndex = 0
        syncIndex = 0
    }

    fun syncPointsLimited(limit : Int) {
        val distance = syncIndex - currentIndex
        val syncLength = if (limit < distance) limit else distance

        val vertices = scanner.getVertexBuffer()
        for(i in 0 .. syncLength) {
            pointCloud.addVertex(vertices[currentIndex+i])
        }
        currentIndex += syncLength + 1
    }

    private fun syncPoints() {
        val vertices = scanner.getVertexBuffer()
        for(i in currentIndex .. syncIndex) {
            pointCloud.addVertex(vertices[i])
        }
        currentIndex = syncIndex + 1
    }

    private fun syncReceived(index : Int) {
        // scan thread
        syncIndex = index
    }
}