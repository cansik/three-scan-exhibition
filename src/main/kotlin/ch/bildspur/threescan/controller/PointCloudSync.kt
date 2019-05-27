package ch.bildspur.threescan.controller

import ch.bildspur.threescan.io.serial.ThreeScanClient
import ch.bildspur.threescan.model.pointcloud.PointCloud

class PointCloudSync(var scanner : ThreeScanClient, var pointCloud : PointCloud) {
    @Volatile private var currentIndex = 0
    private var syncIndex = 0

    fun setup() {
        // setup sync event
        scanner.onScanSync += {
            syncReceived(it)
        }
        reset()
    }

    fun update() {
        // main thread
        if(currentIndex != syncIndex)
            syncPoints()
    }

    fun reset() {
        currentIndex = 0
        syncIndex = 0
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