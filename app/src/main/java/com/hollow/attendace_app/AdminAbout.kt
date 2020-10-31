package com.hollow.attendace_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_admin_about.*
import java.util.*
import kotlin.collections.ArrayList


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

        val ref = fStore.getReference("presence")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val refs = snapshot.children
                var list: Array<Array<String>> = arrayOf()



                var list6 = arrayOf(
                    arrayOf("td","td"), arrayOf("td","td","td")
                )
                for (i in refs) {
                    for(j in i.children){
                        var list2 : Array<Array<String>> = arrayOf()
                        var list3: Array<String> = arrayOf()
                        list3 += i.key.toString()
                        list3 += j.key.toString()
                        var list4: Array<String> = arrayOf()
                        for (k in j.children) {

                            for (l in k.children) {
                                list4 += l.value.toString()
                                //["data"]
                            }

                        }

                        list2 += list3
                        //list2 += list4
                        list += list2
                    }

                }
                //[[tanggal,morning,fajar,nowhere],
                recFull.apply {
                    layoutManager = LinearLayoutManager(this@AdminAbout)
                    adapter = HistoryAdapter(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


//        val calendar = Calendar.getInstance().get(Calendar.HOUR)
//        val date2 = SimpleDateFormat("dd-MM-yyyy",Locale("english")).format(Calendar.getInstance().time)
//        test1.text = date2.toString()
//        test2.text = calendar.toString()
//        btn.setOnClickListener {
//
//            val dates = date.text.toString().trim()
//            val hours = hour.text.toString().trim()
//            val users = user.text.toString().trim()
//            val datas = data.text.toString().trim()
//            var hType = "morning"
//            if(hours.toInt() in 10..12) {
//                hType = "noon"
//            } else if (hours.toInt() in 13..16) {
//                hType = "afternoon"
//            }
////            data class User(
////                var username: String? = "",
////                var email: String? = ""
////            )
////            val user = User("ikhz", "user")
//            val uData = mapOf<String,String>(
//                "Name" to "ikhz",
//                "gps" to "nowhere",
//                "time" to "whenever")
//            val ref = fStore.getReference("presence")
//            ref.child(dates).child(hType).child(users).setValue(uData)
//        }

    }
}