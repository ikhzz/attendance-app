package com.hollow.attendace_app.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hollow.attendace_app.R
import kotlinx.android.synthetic.main.activity_detail_history.*

class DetailHistory : AppCompatActivity() {

    private val fDbs: FirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_history)

        val date = (intent.getStringExtra("date")).toString()
        val part = (intent.getStringExtra("part")).toString()
        val dates: ArrayList<String> = arrayListOf(date,part)
        val ref = fDbs.getReference("presence").child(date).child(part)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var data: ArrayList<ArrayList<String>> = arrayListOf()
                for(i in snapshot.children) {
                    var group: ArrayList<String> = arrayListOf()
                    group.add(i.key.toString())
                    for(j in i.children) {
                        group.add(j.value.toString())
                    }
                    data.add(group)
                }
                recDet.apply {
                    layoutManager = LinearLayoutManager(this@DetailHistory)
                    adapter = DetailAdapter(data, dates)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}