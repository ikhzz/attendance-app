package com.hollow.attendace_app.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hollow.attendace_app.MainActivity
import com.hollow.attendace_app.R
import com.hollow.attendace_app.admin.setting.UserSetting

class AdminHome : AppCompatActivity() {

    private var fDbs: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var fAuth: FirebaseAuth
    private lateinit var user: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        fAuth = FirebaseAuth.getInstance()
        val bottomNavigation : BottomNavigationView = findViewById(R.id.Admin_navigation)
        user = findViewById(R.id.user)
        val btn: Button = findViewById(R.id.logout)
        val btnset: Button = findViewById(R.id.btnset)
        bottomNavigation.selectedItemId = R.id.home
        checkUser()

        bottomNavigation.setOnNavigationItemSelectedListener {
                item ->
            when(item.itemId) {
                R.id.dashboard -> {startActivity(Intent(this, AdminDash::class.java)); true; finish()}
                R.id.home -> { true; finish()}
                R.id.about -> {startActivity(Intent(this, AdminAbout::class.java)); true; finish()}
            }
            false
        }

        btn.setOnClickListener {
            fAuth.signOut()
            startActivity(Intent(this@AdminHome, MainActivity::class.java))
            finish()
        }

        btnset.setOnClickListener {
            startActivity(Intent(this@AdminHome, UserSetting::class.java))
        }

    }

    private fun checkUser() {
        val ref = fDbs.reference.child("profile").child(fAuth.uid.toString())
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val child = snapshot.children
                for(i in child) {
                    if (i.key == "name") user.text = i.value.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }


}