package com.example.amindfullquit.meditation

import android.animation.Animator
import android.content.Context
import android.graphics.*
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.ImageView
import com.example.amindfullquit.R
import kotlin.math.*

class SeekCircle : View {

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    //TIMER
    private lateinit var mTimer: CountDownTimer
    private var isCounting = false
    private var hasPaused = false

    //VALUES
    //Outer circle
    private var cx = 0F  //Center of circle
    private var cy = 0F

    private var radius = 0F
    private var angle = -90.0 //Starting on top

    //Inner circle (filling animation)
    private var fillRatePx = 0F
    private var fillRadius = 0F

    //Pointer
    private var pointerHalfSize = 100
    private var pointerX = 0F
    private var pointerY = 0F

    private var lastXPosition = 0
    private var lastYPosition = 0

    private var mOnProgressChangeListener: OnProgressChangeListener? = null


    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFFFFF")
        strokeWidth = 10F
        style = Paint.Style.STROKE
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFFFFF")
        strokeWidth = 10F
        style = Paint.Style.FILL
    }

    private val mPointer = resources.getDrawable(R.drawable.ic_launcher_foreground, null)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = resolveSizeAndState(suggestedMinimumWidth, widthMeasureSpec, 1)
        val h = resolveSizeAndState(suggestedMinimumHeight, heightMeasureSpec, 1)

        radius = (w / 3).toFloat()
        cx = w / 2F
        cy = h / 2F

        //Follow a circle around center
        pointerX = (radius * cos(Math.toRadians(angle)).toFloat() + cx)
        pointerY = (radius * sin(Math.toRadians(angle)).toFloat() + cy)


        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {

            canvas.drawARGB(255, 78, 168, 186) //Blue color
            canvas.drawCircle(cx, cy, radius, circlePaint) //Circle

            canvas.drawCircle(cx, cy, fillRadius, fillPaint)

            mPointer.setBounds(-pointerHalfSize, -pointerHalfSize, pointerHalfSize, pointerHalfSize)

            canvas.translate(pointerX, pointerY)
            mPointer.draw(canvas)
        }

    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (isCounting) return false

        when(event?.action){
            MotionEvent.ACTION_DOWN -> if (!isValidTouch(event.x, event.y)) return false
            MotionEvent.ACTION_MOVE -> updateOnTouch(event)
            MotionEvent.ACTION_UP -> Log.d("debuglog", "UP")
            else -> return super.onTouchEvent(event)
        }
        return true
    }

    private fun isValidTouch(x: Float, y: Float): Boolean =
        ((x in pointerX - 50..pointerX + 50) && (y in pointerY - 50..pointerY + 50))


    private fun updateOnTouch(event: MotionEvent){

        val currentX = event.x.toInt()
        val currentY = event.y.toInt()

        if ((abs(currentX - lastXPosition) > 20) || (abs(currentY - lastYPosition) > 20)){ //No need to be too accurate, save some memory

            //Distance between the two points
            //val d = sqrt((lastXPosition - currentX.toDouble()).pow(2) + (lastYPosition - currentY.toDouble()).pow(2))

            //Calculating inner angle of P2 & P3
            //val sweepAngle = Math.toDegrees(atan2(currentY - cy, currentX - cx) - atan2(lastYPosition - cy, lastXPosition - cx).toDouble())

            //Calculating angle from center
            val touchAngle = Math.toDegrees(atan2(currentY - cy.toDouble(), currentX - cx.toDouble()))

            lastXPosition = currentX
            lastYPosition = currentY

            updatePointerPosition(touchAngle)
        }
    }

    private fun updatePointerPosition(touchAngle: Double){

        angle = touchAngle

        updateProgress(angle.toInt())

        pointerX = (radius * cos(Math.toRadians(angle))).toFloat() + cx
        pointerY = (radius * sin(Math.toRadians(angle))).toFloat() + cy

        invalidate()
    }

    private fun updateProgress(angle: Int){
        //Top: -90°; Right: 0°; Bottom: 90°; Left: +/-180°
        val progress =
            if (angle in -180..-89) 45 + ((angle + 180) / 6)  //Top left part of circle is inverted
            else (angle + 90) / 6

        mOnProgressChangeListener?.onProgressChanged(progress)
    }

    ///////////////////
    //////COUNTING/////
    ///////////////////
    fun startCountDown(progress: Int){
        isCounting = true

        //keep radius & fillRate if has been paused
        if (!hasPaused) //initialize filling rate
            fillRatePx = (radius / progress)

        pointerX = -100F

        mTimer = object: CountDownTimer(progress * 1000L, 1000){


            override fun onFinish() {
                isCounting = false
                fillRadius = 0F
                hasPaused = false
            }

            override fun onTick(p0: Long) {

                if (isCounting) {
                    fillRadius += fillRatePx
                    //fillRadius = radius * (1 - (p0 / 1000.0) / progress).toFloat() //Inner circle growing each seconds
                    invalidate()
                    mOnProgressChangeListener?.onProgressChanged((p0 / 1000.0).roundToInt() - 1)
                }
                else
                    cancel()
            }
        }.start()


    }

    fun pauseCountDown(){
        isCounting = false
        hasPaused = true
    }

    ///////////////////
    /////LISTENER//////
    ///////////////////
    fun setMyListener(onProgressChangeListener: OnProgressChangeListener){
        mOnProgressChangeListener = onProgressChangeListener
    }

    interface OnProgressChangeListener {
        fun onProgressChanged(progress: Int)
    }



}