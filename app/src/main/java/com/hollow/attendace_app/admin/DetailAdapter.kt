package com.hollow.attendace_app.admin

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.hollow.attendace_app.R
import kotlinx.android.synthetic.main.detrec.view.*
import java.io.File

class DetailAdapter(private val data: ArrayList<ArrayList<String>>, private val date: ArrayList<String>): RecyclerView.Adapter<DetailAdapter.ViewHolder>() {

    private var fStore: FirebaseStorage = FirebaseStorage.getInstance()

    class ViewHolder (view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.detrec, parent, false)
        return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val datas = data[position]
        val dates = date[position]
        holder.itemView.names.text = datas[1]
        holder.itemView.times.text = datas[2]
        val file = File.createTempFile("images", "bmp")

        val ref = fStore.reference.child("/presence/${date[0]}/${date[1]}/${datas[0]}")
        ref.getFile(file).addOnSuccessListener {
            val uri = Uri.parse(file.absolutePath)
            holder.itemView.prevs.setImageURI(uri)
        }.removeOnFailureListener{}
    }

    override fun getItemCount(): Int = data.size
}