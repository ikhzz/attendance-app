package com.hollow.attendace_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recfull.view.*

class HistoryAdapter(private val data: Array<Array<String>>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.recfull, parent, false)
        return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datas = data[position]

        holder.itemView.data.text = datas[0]
        holder.itemView.data2.text = datas[1].toString()
        //holder.itemView.data3.text = datas[2]
        holder.itemView.data4.text = datas.joinToString()
    }

    override fun getItemCount(): Int {
        return data.size
    }
}