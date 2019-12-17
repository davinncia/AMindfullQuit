package com.example.amindfullquit.meditation.timer_fragment

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.amindfullquit.R


class TimerFragment : Fragment(), SeekCircle.OnProgressChangeListener {

    //DATA
    private var isCounting = false

    //UI
    private lateinit var mProgressView: TextView
    private lateinit var mSeekCircle: SeekCircle
    private lateinit var mStartButton: ImageButton
    private lateinit var mPauseButton: ImageButton
    private lateinit var mStopButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_timer, container, false);

        mProgressView = rootView.findViewById(R.id.tv_progress)
        mSeekCircle = rootView.findViewById(R.id.seekCircle)
        mStartButton = rootView.findViewById(R.id.btn_start_meditation)
        mPauseButton = rootView.findViewById(R.id.ib_pause_meditation)
        mStopButton = rootView.findViewById(R.id.ib_stop_meditation)

        mSeekCircle.setMyListener(this)

        mStartButton.setOnClickListener { handleButtonsClick(it) }
        mPauseButton.setOnClickListener { handleButtonsClick(it) }
        mStopButton.setOnClickListener { handleButtonsClick(it) }
        // Inflate the layout for this fragment
        return rootView
    }

    private fun handleButtonsClick(view: View){

        when(view.id){
            R.id.btn_start_meditation -> {
                isCounting = true
                mSeekCircle.startCountDown()
                mStartButton.visibility = View.GONE
                mProgressView.visibility = View.INVISIBLE
                mPauseButton.visibility = View.VISIBLE
                mStopButton.visibility = View.VISIBLE
                mProgressView.text = "0"
            }

            R.id.ib_pause_meditation -> {
                isCounting = if (isCounting) {
                    mSeekCircle.pauseCountDown()
                    mPauseButton.setImageDrawable(context?.getDrawable(R.drawable.ic_play))
                    false
                } else {
                    mSeekCircle.startCountDown()
                    mPauseButton.setImageDrawable(context?.getDrawable(R.drawable.ic_pause))
                    true
                }

            }

            R.id.ib_stop_meditation -> {
                mPauseButton.visibility = View.GONE
                mStopButton.visibility = View.GONE
                mStartButton.visibility = View.VISIBLE
                mProgressView.visibility = View.VISIBLE
                isCounting = false
                mSeekCircle.stopCountDown()
                //Save time done
            }
        }
    }


    override fun onProgressChanged(progress: Int) {
        mProgressView.text = "$progress"

        if (isCounting && progress == 0){
            mPauseButton.visibility = View.GONE
            mStopButton.visibility = View.GONE
            mProgressView.visibility = View.VISIBLE
            mProgressView.text = "Well done !"
        }

    }


    companion object {
        @JvmStatic
        fun newInstance() = TimerFragment()
    }
}
