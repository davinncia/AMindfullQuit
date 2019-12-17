package com.example.amindfullquit.smoking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.amindfullquit.R

class SmokingActivity : AppCompatActivity(), SmokingChartAdapter.ItemClickListener {

    private lateinit var mChartRecyclerView: RecyclerView
    private lateinit var mChartAdapter: SmokingChartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smoking)

        mChartRecyclerView = findViewById(R.id.recycler_view_chart_smoking)

        initChartRecyclerView()

        val viewModel = ViewModelProviders.of(this)[SmokingDataViewModel::class.java]
        viewModel.getConsumption().observe(this, Observer { data ->
            mChartAdapter.populateChart(data)
        })
    }

    private fun initChartRecyclerView(){

        mChartAdapter = SmokingChartAdapter(ArrayList(), this, this)
        val viewManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        mChartRecyclerView.apply {
            setHasFixedSize(true)
            adapter = mChartAdapter
            layoutManager = viewManager
        }
    }

    override fun onChartItemClick(position: Int) {

    }
}
