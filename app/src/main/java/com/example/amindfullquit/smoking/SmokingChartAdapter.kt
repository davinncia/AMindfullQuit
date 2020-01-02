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

class SmokingChartAdapter(private var mItems: List<SmokingChartItemUi>,
                          private val mListener: ItemClickListener,
                          private val mContext: Context
) : RecyclerView.Adapter<SmokingChartAdapter.ChartItemViewHolder>() {

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

        holder.barView.background = mContext.getDrawable(R.drawable.chart_bar_red)
        //Apply animation
        holder.barView.animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_transition_animation)

        holder.barView.layoutParams.height = mItems[position].height
    }

    class ChartItemViewHolder(itemView: View, listener: ItemClickListener) : RecyclerView.ViewHolder(itemView){

        init {
            itemView.setOnClickListener { listener.onChartItemClick(adapterPosition) }
        }

        val barView: View = itemView.findViewById<View>(R.id.view_bar_chart_item)

    }


    interface ItemClickListener{
        fun onChartItemClick(position: Int)
    }
}