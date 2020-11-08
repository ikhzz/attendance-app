package com.hollow.attendace_app.admin

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.hollow.attendace_app.R
import kotlinx.android.synthetic.main.recview.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RecentAdapter(private val data: ArrayList<ArrayList<String>>): RecyclerView.Adapter<RecentAdapter.ViewHolder>()  {
    private var fStore: FirebaseStorage = FirebaseStorage.getInstance()
    private val dates = SimpleDateFormat("dd-MM-yyyy", Locale("english")).format(Calendar.getInstance().time)
    private val hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    private lateinit var day : String

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.recview, parent, false)
        return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datas = data[position]
        holder.itemView.name.text = datas[1]
        holder.itemView.name.text = datas[2]
        val file = File.createTempFile("images", "bmp")

        day = when (hour) {
            in 6..10 -> "Pagi"
            in 11..14 -> "Siang"
            in 15..17 -> "Sore"
            else -> "Bukan Jam Absen"
        }
        val ref = fStore.reference.child(dates).child(day)
        ref.getFile(file).addOnSuccessListener {
            val uri = Uri.parse(file.absolutePath)
            holder.itemView.prev.setImageURI(uri)
        }.removeOnFailureListener{}
    }

    override fun getItemCount(): Int {
        return data.size
    }
}