package com.davinciapp.amindfullquit.meditation.log_fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.davinciapp.amindfullquit.R

class ChartItemAdapter(private var chartItems: List<ChartItemUi>,
                       private val listener: ChartItemClickListener)
    : RecyclerView.Adapter<ChartItemAdapter.ChartItemViewHolder>() {

    var barWidth = 80
    var isZooming = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_chart, parent, false)
        return ChartItemViewHolder(
            view, listener
        )
    }

    override fun getItemCount() = chartItems.size

    override fun onBindViewHolder(holder: ChartItemViewHolder, position: Int) {

        holder.bind(chartItems[position])

    }

    fun populateChart(chartItems: List<ChartItemUi>){
        this.chartItems = chartItems
        notifyDataSetChanged()
    }

    fun zoom(barWidth: Int){
        this.barWidth = barWidth
        isZooming = true
        notifyDataSetChanged()
    }

    inner class ChartItemViewHolder(itemView: View, listener: ChartItemClickListener) : RecyclerView.ViewHolder(itemView){

        init {
            itemView.setOnClickListener { listener.onChartItemClick(adapterPosition) }
        }

        private val barView: View = itemView.findViewById(R.id.view_bar_chart_item)
        private val barFrame: View = itemView.findViewById(R.id.frame_chart_bar_item)

        fun bind(item: ChartItemUi){
            //Apply animation if user is not zooming
            if (!isZooming)
                barView.animation = AnimationUtils.loadAnimation(barView.context, R.anim.fade_transition_animation)
            //Bar height
            barView.layoutParams.height = item.height
            //Bar width
            barFrame.layoutParams.width = barWidth
        }

    }


    interface ChartItemClickListener{
        fun onChartItemClick(position: Int)
    }
}