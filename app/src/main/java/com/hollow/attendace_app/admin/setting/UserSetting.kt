package com.hollow.attendace_app.admin.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hollow.attendace_app.R
import com.hollow.attendace_app.user.HomeFragment


class UserSetting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_setting)

        val bottomNavigation : BottomNavigationView = findViewById(R.id.setting_navigation)

        supportFragmentManager.beginTransaction().replace(R.id.settingFragment, CreateUser()).commit()
        bottomNavigation.setOnNavigationItemSelectedListener{
                item ->
            var fragment: Fragment? = null
            when(item.itemId) {
                R.id.home -> {fragment = CreateUser()
                }
                R.id.dashboard -> {fragment = ResetPass()
                }
            }
            if (fragment != null) {
                supportFragmentManager.beginTransaction().replace(R.id.settingFragment, fragment).commit()
            };
            true
        }
    }
}