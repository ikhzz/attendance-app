package com.hollow.attendace_app.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hollow.attendace_app.R
import kotlinx.android.synthetic.main.activity_admin_about.*


class AdminAbout : AppCompatActivity() {

    private lateinit var fStore : FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_about)

        val bottomNavigation : BottomNavigationView = findViewById(R.id.Admin_navigation)

        bottomNavigation.selectedItemId = R.id.about
        fStore = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()
        val decor = DividerItemDecoration(this,DividerItemDecoration.VERTICAL)
        decor.setDrawable(resources.getDrawable(R.drawable.divider))

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.dashboard -> {
                    startActivity(Intent(this, AdminDash::class.java)); true; finish()
                }
                R.id.about -> {
                    true; finish()
                }
                R.id.home -> {
                    startActivity(Intent(this, AdminHome::class.java)); true;finish()
                }
            }
            false
        }

        val ref = fStore.getReference("presence")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val refs = snapshot.children
                var listData: ArrayList<ArrayList<String>> = arrayListOf()

                for (i in refs) {
                    for(j in i.children){
                        var listGroup : ArrayList<String> = arrayListOf()
                        listGroup.add( i.key.toString())
                        listGroup.add(j.key.toString())
                        listData.add(listGroup)
                    }
                }

                recFull.addItemDecoration(decor)
                recFull.apply {
                    layoutManager = LinearLayoutManager(this@AdminAbout)
                    adapter = HistoryAdapter(listData)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}