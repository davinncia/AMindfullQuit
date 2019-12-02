package com.example.amindfullquit

import android.animation.Animator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MeditationActivity : AppCompatActivity(), SeekCircle.OnProgressChangeListener {

    //DATA
    private var mProgress = 0
    private var isCounting = false

    //UI
    private lateinit var mProgressView: TextView
    private lateinit var mCircleProgress: FloatingActionButton
    private lateinit var mSeekCircle: SeekCircle
    private lateinit var mButton: Button

    private lateinit var progressAnimator: Animator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation)

        mProgressView = findViewById(R.id.tv_progress)
        mCircleProgress = findViewById(R.id.iv_pointer_progress);
        mSeekCircle = findViewById(R.id.seekCircle)
        mButton = findViewById(R.id.btn_meditation)

        mSeekCircle.setMyListener(this)

        mButton.setOnClickListener { handleButtonClick(it) }

    }


    override fun onProgressChanged(progress: Int) {
        mProgressView.text = "$progress"
        mProgress = progress
    }

    private fun handleButtonClick(view: View){
        if (!isCounting) {

            isCounting = true
            mSeekCircle.startCountDown(mProgress)
            mButton.text = "Pause"

            mCircleProgress.show()
            progressAnimator = mSeekCircle.countingAnimator(mCircleProgress, mProgress) //This should happen only once
            progressAnimator.start()

        } else {

            isCounting = false
            mSeekCircle.pauseCountDown()
            mButton.text = "Start"

            mCircleProgress.hide()
            progressAnimator.cancel()
        }
    }
}
