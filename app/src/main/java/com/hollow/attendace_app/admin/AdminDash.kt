package com.hollow.attendace_app.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hollow.attendace_app.R
import kotlinx.android.synthetic.main.activity_admin_dash.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdminDash : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth
    private var fDbs : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val dates = SimpleDateFormat("dd-MM-yyyy", Locale("english")).format(Calendar.getInstance().time)
    private val hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    private lateinit var day : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dash)

        fAuth = FirebaseAuth.getInstance()
        val bottomNavigation : BottomNavigationView = findViewById(R.id.Admin_navigation)
        val decor = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)

        decor.setDrawable(resources.getDrawable(R.drawable.divider))
        bottomNavigation.selectedItemId = R.id.dashboard

        bottomNavigation.setOnNavigationItemSelectedListener {
                item ->
            when(item.itemId) {
                R.id.home -> {startActivity(Intent(this, AdminHome::class.java)); true; finish()}
                R.id.dashboard -> { true; finish()}
                R.id.about -> {startActivity(Intent(this, AdminAbout::class.java)); true; finish()}

            }
            false
        }
        day = when (hour) {
            in 6..10 -> "Pagi"
            in 11..14 -> "Siang"
            in 15..17 -> "Sore"
            else -> "Bukan Jam Absen"
        }
        if (day != "Bukan Jam Absen") {
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
                    rec.apply {
                        layoutManager = LinearLayoutManager(this@AdminDash)
                        adapter = RecentAdapter(list)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext,"Database Error",Toast.LENGTH_SHORT).show()
                }

            })
        } else {
            Toast.makeText(applicationContext, "Tidak ada data karena bukan jam absen", Toast.LENGTH_LONG).show()
        }
    }
}