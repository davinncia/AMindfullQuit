package com.davinciapp.amindfullquit.meditation.log_fragment

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.davinciapp.amindfullquit.ChartOnScaleGestureListener
import com.davinciapp.amindfullquit.R
import com.davinciapp.amindfullquit.RoundDataAdapter
import com.davinciapp.amindfullquit.di.ViewModelFactory

class LogFragment : Fragment(), ChartItemAdapter.ChartItemClickListener {

    //DATA
    private var chartItems = ArrayList<ChartItemUi>()


    //VIEW
    private lateinit var chartRecyclerView: RecyclerView
    private lateinit var chartAdapter: ChartItemAdapter
    private lateinit var logDataAdapter: RoundDataAdapter

    private lateinit var chartItemDateView: TextView
    private lateinit var chartItemTimeView: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_mediation_log, container, false)

        chartItemDateView = rootView.findViewById(R.id.tv_date_chart_meditation)
        chartItemTimeView = rootView.findViewById(R.id.tv_quantity_chart_meditation)

        initDataRecyclerView(rootView)
        initChartRecyclerView(rootView)

        val factory = ViewModelFactory.getInstance(requireActivity().application)
        val viewModel = ViewModelProviders.of(this, factory).get(LogViewModel::class.java)

        //Empty view
        viewModel.emptyGraphLiveData.observe(viewLifecycleOwner, Observer { empty ->
            if (empty) {
                rootView.findViewById<View>(R.id.layout_empty_view_chart_meditation).visibility = View.VISIBLE
                chartRecyclerView.visibility = View.GONE
            } else {
                rootView.findViewById<View>(R.id.layout_empty_view_chart_meditation).visibility = View.GONE
                chartRecyclerView.visibility = View.VISIBLE
            }
        })

        viewModel.maxSessionLiveData.observe(viewLifecycleOwner, Observer {
            rootView.findViewById<TextView>(R.id.text_view_chart_legend_max_meditation).text = it.toString()
        })

        viewModel.getChartItems().observe(viewLifecycleOwner, Observer { items ->
            chartItems = ArrayList(items)
            chartAdapter.populateChart(chartItems)
        })

        viewModel.logDataLiveData.observe(viewLifecycleOwner, Observer {
            logDataAdapter.populateData(it)
        })

        chartRecyclerView.dimensions{ h, _ ->
            viewModel.setMaxBarHeight(h)
        }

        return rootView
    }

    //-------------------------------------------------------------------------------------------//
    //                                 R E C Y C L E R   V I E W S
    //-------------------------------------------------------------------------------------------//
    private fun initDataRecyclerView(rootView: View){

        val recyclerView: RecyclerView = rootView.findViewById(R.id.recycler_view_log_data_meditation)

        val helper = LinearSnapHelper()
        helper.attachToRecyclerView(recyclerView)

        logDataAdapter = RoundDataAdapter(requireContext())

        recyclerView.apply {
            setHasFixedSize(true)
            adapter = logDataAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun initChartRecyclerView(rootView: View){

        chartRecyclerView = rootView.findViewById(R.id.recycler_view_chart_meditation)
        chartAdapter = ChartItemAdapter(ArrayList(), this@LogFragment)

        val gestureListener = ChartOnScaleGestureListener(chartAdapter.barWidth)
        gestureListener.setZoomListener( object: ChartOnScaleGestureListener.ZoomListener{
            override fun onZooming(barWidth: Int) {
                chartAdapter.zoom(barWidth)
            }

            override fun zoomingStopped() {
                chartAdapter.isZooming = false
            }
        })
        val scaleGestureDetector = ScaleGestureDetector(requireContext(), gestureListener)


        chartRecyclerView.apply {

            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = chartAdapter

            setOnTouchListener { _, event ->
                scaleGestureDetector.onTouchEvent(event)
                false //Still allow scrolling
            }

        }

    }

    //-------------------------------------------------------------------------------------------//
    //                                         C L I C K S
    //-------------------------------------------------------------------------------------------//
    override fun onChartItemClick(position: Int) {
        //TODO  VIEW MODEL
        chartItemDateView.text = chartItems[position].description
        chartItemTimeView.text = "${chartItems[position].minutes}min"
    }


    //-------------------------------------------------------------------------------------------//
    //                                        H E L P E R S
    //-------------------------------------------------------------------------------------------//
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


    companion object {
        @JvmStatic
        fun newInstance() = LogFragment()
    }
}