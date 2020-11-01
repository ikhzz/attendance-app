package com.hollow.attendace_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_detail_history.*

class DetailHistory : AppCompatActivity() {

    private lateinit var fStore : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_history)

        val date = intent.getStringExtra("date")
        val part = intent.getStringExtra("part")
        fStore = FirebaseDatabase.getInstance()

        val ref = fStore.getReference("presence").child(date).child(part)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var data: ArrayList<ArrayList<String>> = arrayListOf()
                for(i in snapshot.children) {
                    var group: ArrayList<String> = arrayListOf()
                    for(j in i.children) {
                        group.add(j.value.toString())
                    }
                    data.add(group)
                }
                //Toast.makeText(this@DetailHistory,snapshot.toString(),Toast.LENGTH_LONG).show()
                Toast.makeText(this@DetailHistory,data.joinToString(),Toast.LENGTH_LONG).show()
                recDet.apply {
                    layoutManager = LinearLayoutManager(this@DetailHistory)
                    adapter = DetailAdapter(data)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}