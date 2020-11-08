package com.hollow.attendace_app.user

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hollow.attendace_app.R

class UserHome : AppCompatActivity() {

    private lateinit var sp : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)

        val bottomNavigation : BottomNavigationView = findViewById(R.id.User_navigation)
        sp = getSharedPreferences("data", MODE_PRIVATE)

        supportFragmentManager.beginTransaction().replace(R.id.userFragment, HomeFragment()).commit()
        bottomNavigation.setOnNavigationItemSelectedListener{
            item ->
            var fragment: Fragment? = null
            when(item.itemId) {
                R.id.home -> {fragment = HomeFragment()}
                R.id.dashboard -> {fragment = AttendanceFragment()}
            }
            if (fragment != null) {
                supportFragmentManager.beginTransaction().replace(R.id.userFragment, fragment).commit()
            };
            true
        }

    }
}