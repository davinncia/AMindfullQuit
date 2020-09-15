package com.davinciapp.amindfullquit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.davinciapp.amindfullquit.meditation.log_fragment.LogDataUi

class RoundDataAdapter(private val mContext: Context) : RecyclerView.Adapter<RoundDataAdapter.LogDataViewHolder>() {

    private var mLogData = ArrayList<LogDataUi>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogDataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_meditation_log, parent, false)
        return LogDataViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return mLogData.size
    }

    fun populateData(data: List<LogDataUi>){
        mLogData = ArrayList(data)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: LogDataViewHolder, position: Int) {

        val currentLogData = mLogData[position]

        holder.nbrView.text = currentLogData.value
        holder.descriptionView.text = currentLogData.description

    }

    inner class LogDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nbrView: TextView = itemView.findViewById(R.id.tv_number_meditation_log_item)
        val descriptionView: TextView = itemView.findViewById(R.id.tv_description_meditation_log_item)
    }
}