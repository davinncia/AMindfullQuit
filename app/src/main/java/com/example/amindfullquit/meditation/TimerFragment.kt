package com.example.amindfullquit.meditation

import android.animation.Animator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.amindfullquit.R
import com.example.amindfullquit.SeekCircle
import com.google.android.material.floatingactionbutton.FloatingActionButton


class TimerFragment : Fragment(), SeekCircle.OnProgressChangeListener {

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

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_timer, container, false);

        mProgressView = rootView.findViewById(R.id.tv_progress)
        mCircleProgress = rootView.findViewById(R.id.iv_pointer_progress)
        mSeekCircle = rootView.findViewById(R.id.seekCircle)
        mButton = rootView.findViewById(R.id.btn_meditation)

        mSeekCircle.setMyListener(this)

        mButton.setOnClickListener { handleButtonClick(it) }
        // Inflate the layout for this fragment
        return rootView
    }

    private fun handleButtonClick(view: View){
        if (!isCounting) {

            isCounting = true
            mSeekCircle.startCountDown(mProgress)
            mButton.text = "Pause"

            progressAnimator = mSeekCircle.countingAnimator(mCircleProgress, mProgress) //This should happen only once
            progressAnimator.start()
            mCircleProgress.show()

        } else {

            isCounting = false
            mSeekCircle.pauseCountDown()
            mButton.text = "Start"

            mCircleProgress.hide()
            progressAnimator.cancel()
        }
    }

    override fun onProgressChanged(progress: Int) {
        mProgressView.text = "$progress"
        mProgress = progress
    }


    companion object {
        @JvmStatic
        fun newInstance() = TimerFragment()
    }
}
