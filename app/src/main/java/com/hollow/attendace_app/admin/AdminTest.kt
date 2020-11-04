package com.hollow.attendace_app.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hollow.attendace_app.R
import kotlinx.android.synthetic.main.activity_admin_test.*

class AdminTest : AppCompatActivity() {

    private lateinit var fStore : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_test)

        val bottomNavigation : BottomNavigationView = findViewById(R.id.Admin_navigation)

        bottomNavigation.selectedItemId = R.id.write

        bottomNavigation.setOnNavigationItemSelectedListener {
                item ->
            when(item.itemId) {
                R.id.dashboard -> {startActivity(Intent(this, AdminDash::class.java)); true; finish()}
                R.id.write -> { true; finish()}
                R.id.about -> {startActivity(Intent(this, AdminAbout::class.java)); true; finish()}
                R.id.home -> {startActivity(Intent(this, AdminHome::class.java)); true; finish()}
            }
            false
        }
        fStore = FirebaseDatabase.getInstance()
        val ref = fStore.getReference("presence").child("02-10-2020").child("morning")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.children
                var list: Array<Array<String>> = arrayOf()

                for (i in data) {
                    var list2 : Array<String> = arrayOf<String>()
                    for(j in i.children) {
                        list2 += j.value.toString()
                    }
                    list += list2
                }
                rec.apply {
                    layoutManager = LinearLayoutManager(this@AdminTest)
                    adapter = TestAdapter(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}