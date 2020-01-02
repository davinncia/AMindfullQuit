package com.example.amindfullquit.meditation.log_fragment

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.amindfullquit.R

class LogFragment : Fragment(), ChartItemAdapter.ChartItemClickListener {

    //DATA
    private var chartItems = ArrayList<ChartItemUi>()


    //VIEW
    private lateinit var chartRecyclerView: RecyclerView
    private lateinit var chartAdapter: ChartItemAdapter
    private lateinit var logDataAdapter: LogDataAdapter

    private lateinit var chartItemDetailsView: TextView

    private var barWidth = 100

    var scaleGestureDetector: ScaleGestureDetector? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_log, container, false)

        chartItemDetailsView = rootView.findViewById(R.id.tv_details_chart_meditation)

        initDataRecyclerView(rootView)
        initChartRecyclerView(rootView)

        val viewModel = ViewModelProviders.of(this).get(LogViewModel::class.java)
        viewModel.getChartItems().observe(this, Observer { items ->
            chartItems = ArrayList(items)
            chartAdapter.populateChart(chartItems)
        })

        viewModel.logDataLiveData.observe(this, Observer { logDataAdapter.populateData(it) })

        chartRecyclerView.dimensions{ h, _ ->
            viewModel.setMaxBarHeight(h)
        }

        return rootView
    }

    private fun initDataRecyclerView(rootView: View){

        val recyclerView: RecyclerView = rootView.findViewById(R.id.recycler_view_log_data_meditation)

        val helper = LinearSnapHelper()
        helper.attachToRecyclerView(recyclerView)

        logDataAdapter = LogDataAdapter(requireContext())

        recyclerView.apply {
            setHasFixedSize(true)
            adapter = logDataAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

    }
    private fun initChartRecyclerView(rootView: View){

        chartRecyclerView = rootView.findViewById(R.id.recycler_view_chart_meditation)

        chartAdapter = ChartItemAdapter(ArrayList(), this@LogFragment)

        scaleGestureDetector = ScaleGestureDetector(requireContext(), MyOnScaleGestureListener())


        chartRecyclerView.apply {

            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = chartAdapter

            setOnTouchListener { v, event ->
                scaleGestureDetector?.onTouchEvent(event)
                false
            }

        }

    }

    override fun onChartItemClick(position: Int) {
        //TODO  VIEWMODEL
        chartItemDetailsView.text = chartItems[position].description
    }


    //VIEW SIZE HELPER, extension function
    private fun <T : View> T.dimensions(function: (Int, Int) -> Unit) {
        if (height == 0 || width == 0)
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    function(height, width)
                }    
            })
        else function(height, width)
    }


    private fun zoomIn(){
        if (barWidth > 200) return //Big enough

        barWidth = (barWidth * 1.2F).toInt()
        chartAdapter.zoom(barWidth)
    }

    private fun zoomOut(){
        if (barWidth < 30) return //Small enough

        barWidth = (barWidth * 0.8F).toInt()
        chartAdapter.zoom(barWidth)
    }

    inner class MyOnScaleGestureListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        
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
            chartAdapter.isZooming = false
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = LogFragment()
    }
}