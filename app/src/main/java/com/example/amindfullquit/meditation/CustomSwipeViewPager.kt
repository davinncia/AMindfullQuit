package com.example.amindfullquit.meditation

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class CustomSwipeViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {


    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        //return super.onInterceptTouchEvent(ev)

        //This will avoid onTouch to be intercepted when setting the timer.
        return false
    }
}