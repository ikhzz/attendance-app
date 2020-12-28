package com.hollow.attendace_app.admin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
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
import com.hollow.attendace_app.MainActivity
import com.hollow.attendace_app.R
import com.hollow.attendace_app.admin.setting.UserSetting
import kotlinx.android.synthetic.main.activity_admin_home.*
import java.text.SimpleDateFormat
import java.util.*

class AdminHome : AppCompatActivity() {
    // Admin Home Parameter
    private val fDbs: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val fAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    private val dates = SimpleDateFormat("dd-MM-yyyy", Locale("english")).format(Calendar.getInstance().time)
    private lateinit var day : String
    private lateinit var user: TextView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var decor: DividerItemDecoration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        // Prepare View and Bottom Navigation
        user = findViewById(R.id.user)
        bottomNavigation = findViewById(R.id.Admin_navigation)
        bottomNavigation.selectedItemId = R.id.home
        // Prepare Divider Decoration
        decor = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        decor.setDrawable(resources.getDrawable(R.drawable.divider))
        // Run setAdmin Method
        setAdmin()
        // Bottom Navigation Selector for Every Activity
        bottomNavigation.setOnNavigationItemSelectedListener {
                item ->
            when(item.itemId) {
                R.id.dashboard -> startActivity(Intent(this, UserSetting::class.java))
                R.id.home -> {}
                R.id.about -> startActivity(Intent(this, AdminAbout::class.java))
            }
            false
        }
        // Time Check to Query Recent Data
        day = when (hour) {
            in 6..10 -> "Pagi"
            in 11..14 -> "Siang"
            in 15..17 -> "Sore"
            else -> "Bukan Jam Absen"
        }

        if (day != "Bukan Jam Absen") {
            // Query Data Basen Dates And Hour
            val ref = fDbs.getReference("presence").child(dates).child(day)
            ref.addValueEventListener(object: ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val child = snapshot.children
                    val list: ArrayList<ArrayList<String>> = arrayListOf()
                    for(i in child) {
                        val group: ArrayList<String> = arrayListOf()
                        group.add(i.key.toString())
                        for(j in i.children) {
                            group.add(j.value.toString())
                        }
                        list.add(group)
                    }
                    // Send Data to RecyclerView
                    rec.apply {
                        layoutManager = LinearLayoutManager(this@AdminHome)
                        adapter = RecentAdapter(list)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        } else {
            Toast.makeText(applicationContext, "Tidak ada data karena bukan jam absen", Toast.LENGTH_LONG).show()
        }
    }
    // Method for Logout Action Bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_adminmenu, menu)
        return true
    }
    // Method to set Admin UserName
    private fun setAdmin() {
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
    // Method Logout
    fun logOut(item: MenuItem) {
        fAuth.signOut()
        startActivity(Intent(this@AdminHome, MainActivity::class.java))
        finish()
    }
}