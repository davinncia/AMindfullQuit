package com.example.amindfullquit.meditation.log_fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.amindfullquit.R

class ChartItemAdapter(private var mItems: List<ChartItemUi>,
                       private val mListener: ChartItemClickListener,
                       private val mContext: Context)
    : RecyclerView.Adapter<ChartItemAdapter.ChartItemViewHolder>() {


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

    fun populateChart(chartItems: List<ChartItemUi>){
        mItems = chartItems
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ChartItemViewHolder, position: Int) {

        //Apply animation
        holder.barView.animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_transition_animation)

        holder.barView.layoutParams.height = mItems[position].height
    }

    class ChartItemViewHolder(itemView: View, listener: ChartItemClickListener) : RecyclerView.ViewHolder(itemView){

        init {
            //TODO: Implementation of item click (java style)
            itemView.setOnClickListener { listener.onChartItemClick(adapterPosition) }
        }

        val barView: View = itemView.findViewById<View>(R.id.view_bar_chart_item)

    }


    interface ChartItemClickListener{
        fun onChartItemClick(position: Int)
    }
}