package com.hollow.attendace_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_admin_about.*


class AdminAbout : AppCompatActivity() {

    private lateinit var fStore : FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_about)

        val bottomNavigation : BottomNavigationView = findViewById(R.id.Admin_navigation)
        val test1: TextView = findViewById(R.id.test1)
        val test2: TextView = findViewById(R.id.test2)
        val date: TextView = findViewById(R.id.date)
        val hour: TextView = findViewById(R.id.hour)
        val user: TextView = findViewById(R.id.userid)
        val data: TextView = findViewById(R.id.data)
        val btn: Button = findViewById(R.id.button)

        bottomNavigation.selectedItemId = R.id.about
        fStore = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()

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
                R.id.write -> {
                    startActivity(Intent(this, AdminTest::class.java)); true;finish()
                }
            }
            false
        }

//        val calendar = Calendar.getInstance().get(Calendar.HOUR)
//        val date2 = SimpleDateFormat("dd-MM-yyyy",Locale("english")).format(Calendar.getInstance().time)
//        test1.text = date2.toString()
//        test2.text = calendar.toString()
        btn.setOnClickListener {

            val dates = date.text.toString().trim()
            val hours = hour.text.toString().trim()
            val users = user.text.toString().trim()
            val datas = data.text.toString().trim()
            var hType = "morning"
            if(hours.toInt() in 10..12) {
                hType = "noon"
            } else if (hours.toInt() in 13..16) {
                hType = "afternoon"
            }
//            data class User(
//                var username: String? = "",
//                var email: String? = ""
//            )
//            val user = User("ikhz", "user")
            val uData = mapOf<String,String>(
                "Name" to "ikhz",
                "gps" to "nowhere",
                "time" to "whenever")
            val ref = fStore.getReference("presence")
            ref.child(dates).child(hType).child(users).setValue(uData)
        }

    }
}