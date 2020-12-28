package com.hollow.attendace_app.admin.setting

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.hollow.attendace_app.MainActivity
import com.hollow.attendace_app.R
import com.hollow.attendace_app.admin.AdminAbout
import com.hollow.attendace_app.admin.AdminHome


class UserSetting : AppCompatActivity() {

    private val fAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_setting)

        bottomNavigation = findViewById(R.id.setting_navigation)
        bottomNavigation.selectedItemId = R.id.dashboard
        supportFragmentManager.beginTransaction().replace(R.id.settingFragment, CreateUser()).commit()

        bottomNavigation.setOnNavigationItemSelectedListener {
                item ->
            when(item.itemId) {
                R.id.home -> startActivity(Intent(this, AdminHome::class.java))
                R.id.dashboard -> { }
                R.id.about -> startActivity(Intent(this, AdminAbout::class.java))
            }
            false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_usersettingmenu, menu)
        return true
    }

    fun logOut(item: MenuItem) {
        fAuth.signOut()
        startActivity(Intent(this@UserSetting, MainActivity::class.java))
        finish()
    }

    fun createUser(item: MenuItem){
        supportFragmentManager.beginTransaction().replace(R.id.settingFragment, CreateUser()).commit()
    }

    fun resetPass(item: MenuItem){
        supportFragmentManager.beginTransaction().replace(R.id.settingFragment, ResetPass()).commit()
    }
}