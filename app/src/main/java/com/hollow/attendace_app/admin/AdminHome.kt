package com.hollow.attendace_app.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hollow.attendace_app.MainActivity
import com.hollow.attendace_app.R

class AdminHome : AppCompatActivity() {

    private lateinit var fStore : FirebaseDatabase
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        val bottomNavigation : BottomNavigationView = findViewById(R.id.Admin_navigation)
        val view: TextView = findViewById(R.id.user)
        val btn: Button = findViewById(R.id.logout)

        bottomNavigation.selectedItemId = R.id.home

        bottomNavigation.setOnNavigationItemSelectedListener {
                item ->
            when(item.itemId) {
                R.id.dashboard -> {startActivity(Intent(this, AdminDash::class.java)); true; finish()}
                R.id.home -> { true; finish()}
                R.id.about -> {startActivity(Intent(this, AdminAbout::class.java)); true; finish()}
                R.id.write -> {startActivity(Intent(this, AdminTest::class.java)); true; finish()}
            }
            false
        }

        fStore = FirebaseDatabase.getInstance()
        fAuth = FirebaseAuth.getInstance()
        val a = fAuth.uid.toString()

        val ref = fStore.getReference("profile").child(a)


        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.child("level")
                val childd = snapshot.children
//                childd.forEach {
//                    Toast.makeText(this@AdminHome,  "${it.key} dan ${it.value}", Toast.LENGTH_LONG).show()
//                }
                //val mapps = listOf<Any>(childd.forEach{it.key}).toString()
                //view.text = mapps

            }
        })
        btn.setOnClickListener {
            fAuth.signOut()
            startActivity(Intent(this@AdminHome, MainActivity::class.java))
            finish()
        }
    }
}