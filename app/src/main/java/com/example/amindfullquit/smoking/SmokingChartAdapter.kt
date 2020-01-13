package com.example.amindfullquit.smoking

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.amindfullquit.R
import com.example.amindfullquit.meditation.log_fragment.ChartItemUi
import java.util.*

class SmokingChartAdapter(private var mItems: List<SmokingChartItemUi>,
                          private val mListener: ItemClickListener,
                          private val mContext: Context
) : RecyclerView.Adapter<SmokingChartAdapter.ChartItemViewHolder>() {

    var barWidth = 80
    var isZooming = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_chart, parent, false)
        return ChartItemViewHolder(
            view, mListener
        )
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun populateChart(chartItems: List<SmokingChartItemUi>){
        mItems = chartItems
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ChartItemViewHolder, position: Int) {

        holder.bind(mItems[position])

    }

    fun zoom(barWidth: Int){
        this.barWidth = barWidth
        isZooming = true
        notifyDataSetChanged()
    }

    inner class ChartItemViewHolder(itemView: View, listener: ItemClickListener) : RecyclerView.ViewHolder(itemView){

        init {
            itemView.setOnClickListener { listener.onChartItemClick(mItems[adapterPosition]) }
        }

        private val barView: View = itemView.findViewById<View>(R.id.view_bar_chart_item)
        private val barFrame: View = itemView.findViewById(R.id.frame_chart_bar_item)

        fun bind(item: SmokingChartItemUi){

            //Apply animation if user is not zooming
            if (!isZooming)
                barView.animation = AnimationUtils.loadAnimation(barView.context, R.anim.fade_transition_animation)
            //Bar color
            barView.background = mContext.getDrawable(R.drawable.chart_bar_red)
            //Bar Height
            barView.layoutParams.height = item.height
            //Bar width
            barFrame.layoutParams.width = barWidth

        }

    }


    interface ItemClickListener{
        fun onChartItemClick(chartItem: SmokingChartItemUi)
    }
}