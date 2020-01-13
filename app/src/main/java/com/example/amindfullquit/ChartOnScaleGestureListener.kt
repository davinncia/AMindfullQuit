package com.example.amindfullquit

import android.view.ScaleGestureDetector

class ChartOnScaleGestureListener(var barWidth: Int) : ScaleGestureDetector.OnScaleGestureListener{

    private var zoomListener: ZoomListener? = null

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        val scaleFactor = detector.scaleFactor

        if (scaleFactor > 1.04) {
            zoomIn()
            return true
        } else if (scaleFactor < 0.96){
            zoomOut()
            return true
        }
        return false
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        zoomListener?.zoomingStopped()
    }

    private fun zoomIn(){
        if (barWidth > 200) return //Big enough

        barWidth = (barWidth * 1.2F).toInt()
        zoomListener?.onZooming(barWidth)
    }

    private fun zoomOut(){
        if (barWidth < 30) return //Small enough

        barWidth = (barWidth * 0.8F).toInt()
        zoomListener?.onZooming(barWidth)
    }

    fun setZoomListener(listener: ZoomListener){
        this.zoomListener = listener
    }

    interface ZoomListener{
        fun onZooming(barWidth: Int)
        fun zoomingStopped()
    }

}