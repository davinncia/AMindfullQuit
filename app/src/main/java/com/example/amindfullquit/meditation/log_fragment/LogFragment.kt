package com.example.amindfullquit.meditation.log_fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.amindfullquit.R
import com.example.amindfullquit.meditation.MeditationSession

class LogFragment : Fragment(), ChartItemAdapter.ChartItemClickListener {

    //DATA
    private var mChartItems = ArrayList<ChartItemUi>()

    //VIEW
    private lateinit var mChartRecyclerView: RecyclerView
    private lateinit var mChartAdapter: ChartItemAdapter
    private lateinit var mLogDataAdapter: LogDataAdapter

    private lateinit var mChartItemDetailsView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_log, container, false)

        mChartItemDetailsView = rootView.findViewById(R.id.tv_details_chart_meditation)

        initDataRecyclerView(rootView)
        initChartRecyclerView(rootView)

        val viewModel = ViewModelProviders.of(this)[LogViewModel::class.java]
        viewModel.getChartItems().observe(this, Observer { items ->
            mChartItems = ArrayList(items)
            mChartAdapter.populateChart(mChartItems)
        })

        viewModel.logDataLiveData.observe(this, Observer { mLogDataAdapter.populateData(it) })


        mChartRecyclerView.height {
            viewModel.setMaxBarHeight(it)
        }
        return rootView
    }



    private fun initDataRecyclerView(rootView: View){

        val recyclerView: RecyclerView = rootView.findViewById(R.id.recycler_view_log_data_meditation)
        val helper = LinearSnapHelper()
        helper.attachToRecyclerView(recyclerView)
        mLogDataAdapter = LogDataAdapter(this.requireContext())
        val viewManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        recyclerView.apply {
            setHasFixedSize(true)
            adapter = mLogDataAdapter
            layoutManager = viewManager
        }

    }
    private fun initChartRecyclerView(rootView: View){

        mChartRecyclerView = rootView.findViewById(R.id.recycler_view_chart_meditation)

        mChartAdapter = ChartItemAdapter(ArrayList(), this, this.requireContext())

        val viewManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        mChartRecyclerView.apply {

            setHasFixedSize(true)

            layoutManager = viewManager

            adapter = mChartAdapter
        }

    }

    override fun onChartItemClick(position: Int) {
        mChartItemDetailsView.text = mChartItems[position].description
    }

    //VIEW SIZE HELPER, extension function
    private fun <T : View> T.height(function: (Int) -> Unit) {
        if (height == 0)
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    function(height)
                }
            })
        else function(height)
    }


    companion object {
        @JvmStatic
        fun newInstance() = LogFragment()
    }
}