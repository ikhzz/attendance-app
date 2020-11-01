package com.hollow.attendace_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.detrec.view.*

class DetailAdapter(private val data: ArrayList<ArrayList<String>>): RecyclerView.Adapter<DetailAdapter.ViewHolder>() {
    class ViewHolder (view: View): RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.detrec, parent, false)
        return DetailAdapter.ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datas = data[position]
        holder.itemView.data1.text = datas[0]
    }

    override fun getItemCount(): Int = data.size
}