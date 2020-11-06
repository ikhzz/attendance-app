package com.hollow.attendace_app.admin

import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hollow.attendace_app.*
import kotlinx.android.synthetic.main.activity_admin_about.*
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
                var listData: ArrayList<ArrayList<String>> = arrayListOf()

                for (i in refs) {
                    for(j in i.children){
                        var listGroup : ArrayList<String> = arrayListOf()
                        listGroup.add( i.key.toString())
                        listGroup.add(j.key.toString())
//                        var dataGroups: MutableList<ArrayList<String>> = arrayListOf()
//                        for (k in j.children) {
//                            var dataGroup: ArrayList<String> = arrayListOf()
//                            for (l in k.children) {
//                                dataGroup.add(l.value.toString())
//                            }
//                            dataGroups.add(dataGroup)
//                        }
                        listData.add(listGroup)
                    }

                }
                //[[tanggal,morning,fajar,nowhere],
                recFull.addItemDecoration(decor)
                recFull.apply {
                    layoutManager = LinearLayoutManager(this@AdminAbout)
                    adapter = HistoryAdapter(listData)
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