package com.hollow.attendace_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.hollow.attendace_app.admin.AdminHome
import com.hollow.attendace_app.user.UserHome

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun moveReg(view: View) {
        startActivity(Intent(this, UserHome::class.java))
    }
}