package com.davinciapp.amindfullquit.meditation.timer_fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.davinciapp.amindfullquit.R
import com.davinciapp.amindfullquit.di.ViewModelFactory


class TimerFragment : Fragment(), SeekCircle.OnProgressChangeListener {
    //TODO: keep screen awake

    private lateinit var viewModel: TimerViewModel

    //DATA
    private var isCounting = false

    //UI
    private lateinit var progressView: TextView
    private lateinit var seekCircle: SeekCircle
    private lateinit var startButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var stopButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_meditation_timer, container, false)

        (activity as AppCompatActivity).setSupportActionBar(rootView.findViewById(R.id.toolbar_meditation))
        (activity as AppCompatActivity).supportActionBar?.title = ""

        progressView = rootView.findViewById(R.id.tv_progress)
        seekCircle = rootView.findViewById(R.id.seekCircle)
        startButton = rootView.findViewById(R.id.btn_start_meditation)
        pauseButton = rootView.findViewById(R.id.ib_pause_meditation)
        stopButton = rootView.findViewById(R.id.ib_stop_meditation)

        seekCircle.onProgressChangeListener = this

        startButton.setOnClickListener { handleButtonsClick(it) }
        pauseButton.setOnClickListener { handleButtonsClick(it) }
        stopButton.setOnClickListener { handleButtonsClick(it) }

        viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity().application))
            .get(TimerViewModel::class.java)

        // Inflate the layout for this fragment
        return rootView
    }

    private fun handleButtonsClick(view: View){

        when(view.id){
            R.id.btn_start_meditation -> {
                if (progressView.text.toString().toInt() < 1) return
                viewModel.setMeditationLength(progressView.text.toString().toInt())

                isCounting = true
                seekCircle.startCountDown()
                startButton.visibility = View.INVISIBLE
                progressView.visibility = View.INVISIBLE
                pauseButton.visibility = View.VISIBLE
                stopButton.visibility = View.VISIBLE
                progressView.text = "0"
            }

            R.id.ib_pause_meditation -> {
                isCounting = if (isCounting) {
                    seekCircle.pauseCountDown()
                    pauseButton.setImageResource(R.drawable.ic_play)
                    false
                } else {
                    seekCircle.startCountDown()
                    pauseButton.setImageResource(R.drawable.ic_pause)
                    true
                }

            }

            R.id.ib_stop_meditation -> {
                pauseButton.setImageResource(R.drawable.ic_pause)
                pauseButton.visibility = View.GONE
                stopButton.visibility = View.GONE
                startButton.visibility = View.VISIBLE
                progressView.visibility = View.VISIBLE
                isCounting = false
                seekCircle.stopCountDown()
                //Save time done (?)
            }
        }
    }


    override fun onProgressChanged(progress: Int) {
        progressView.text = "$progress"

        if (isCounting && progress == 0){ //Finished
            viewModel.saveSession()

            pauseButton.visibility = View.GONE
            stopButton.visibility = View.GONE
            progressView.visibility = View.VISIBLE
            progressView.text = "Well done!"
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        seekCircle.stopCountDown()
    }

    //-------------------------------------------------------------------------------------------//
    //                                          M E N U
    //-------------------------------------------------------------------------------------------//
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_info, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("debuglog", "touch")
        return when (item.itemId) {
            R.id.item_info_menu_info -> {
                Toast.makeText(requireContext(), "Infos", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = TimerFragment()
    }
}
