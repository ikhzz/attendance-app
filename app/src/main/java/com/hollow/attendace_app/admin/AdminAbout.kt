package com.hollow.attendace_app.admin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import kotlinx.android.synthetic.main.activity_admin_about.*


class AdminAbout : AppCompatActivity() {
    // Prepare History Activity
    private val fDbs: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val fAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var decor: DividerItemDecoration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_about)
        // Prepare Bottom Navigation and Divider Decoration
        bottomNavigation = findViewById(R.id.Admin_navigation)
        bottomNavigation.selectedItemId = R.id.about
        decor = DividerItemDecoration(this,DividerItemDecoration.VERTICAL)
        decor.setDrawable(resources.getDrawable(R.drawable.divider))
        // Bottom Navigation Selector for Every Activity
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.dashboard -> startActivity(Intent(this, UserSetting::class.java))
                R.id.about -> {}
                R.id.home -> startActivity(Intent(this, AdminHome::class.java))
            }
            false
        }
        // Query All Data
        val ref = fDbs.getReference("presence")
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
                //Send Data to Recycler View
                recFull.apply {
                    layoutManager = LinearLayoutManager(this@AdminAbout)
                    adapter = HistoryAdapter(listData)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
    // Method for Logout Action Bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_adminmenu, menu)
        return true
    }
    //Method Logout
    fun logOut(item: MenuItem) {
        fAuth.signOut()
        startActivity(Intent(this@AdminAbout, MainActivity::class.java))
        finish()
    }
}