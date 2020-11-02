package com.hollow.attendace_app.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hollow.attendace_app.R

class AdminDash : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dash)

        val emailView : EditText = findViewById(R.id.email)
        val passView : EditText = findViewById(R.id.pass)
        val nameView : EditText = findViewById(R.id.name)
        val button : Button = findViewById(R.id.btnreg)
        val bottomNavigation : BottomNavigationView = findViewById(R.id.Admin_navigation)

        bottomNavigation.selectedItemId = R.id.dashboard

        bottomNavigation.setOnNavigationItemSelectedListener {
                item ->
            when(item.itemId) {
                R.id.home -> {startActivity(Intent(this, AdminHome::class.java)); true; finish()}
                R.id.dashboard -> { true; finish()}
                R.id.about -> {startActivity(Intent(this, AdminAbout::class.java)); true; finish()}
                R.id.write -> {startActivity(Intent(this, AdminTest::class.java)); true; finish()}
            }
            false
        }

        fStore = FirebaseDatabase.getInstance()
        fAuth = FirebaseAuth.getInstance()

        button.setOnClickListener {
            val email = emailView.text.toString().trim()
            val pass: String = passView.text.toString().trim()
            var name: String = nameView.text.toString().trim()

            if(email.isEmpty()) {
                emailView.error = "Email tidak boleh kosong"
            } else if (pass.isEmpty() || pass.length < 6) {
                passView.error = ("Password tidak boleh kosong dan kurang dari 6")
            } else {
                fAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                    task ->
                    if (task.isSuccessful) {
                        val user = fAuth.uid.toString()
                        Toast.makeText(this, "$email Telah terdaftar", Toast.LENGTH_LONG).show()
                        val ref = fStore.getReference("profile")
                        val uData = mapOf<String, String>(
                            "name" to name,
                            "level" to "admin",
                        )
                        ref.child(user).setValue(uData)

                    } else {
                        Toast.makeText(this, "$email gagal terdaftar", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}