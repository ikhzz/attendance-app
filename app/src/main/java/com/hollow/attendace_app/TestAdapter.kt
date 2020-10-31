package com.hollow.attendace_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recview.view.*

class TestAdapter(private val data: Array<Array<String>>) : RecyclerView.Adapter<TestAdapter.ViewHolder>() {
    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.recview, parent, false)
        return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datas = data[position]
        holder.itemView.day.text = datas[0]
    }

    override fun getItemCount(): Int {
        return data.size
    }
}