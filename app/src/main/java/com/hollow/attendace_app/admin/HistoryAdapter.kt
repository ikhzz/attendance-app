package com.hollow.attendace_app.admin

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hollow.attendace_app.R
import kotlinx.android.synthetic.main.recfull.view.*

class HistoryAdapter(private val data: ArrayList<ArrayList<String>>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.recfull, parent, false)
        return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datas = data[position]

        holder.itemView.datevalue.text = datas[0]
        holder.itemView.presenceValue.text = datas[1]

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailHistory::class.java)
            intent.putExtra("date", datas[0])
            intent.putExtra("part", datas[1])

            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}