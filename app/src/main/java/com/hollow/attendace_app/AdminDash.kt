package com.hollow.attendace_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class AdminDash : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dash)

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

        val emailView : EditText = findViewById(R.id.email)
        val passView : EditText = findViewById(R.id.pass)
        val nameView : EditText = findViewById(R.id.name)
        val button : Button = findViewById(R.id.btnreg)

        fStore = FirebaseFirestore.getInstance()
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
                        val user = fAuth.currentUser
                        Toast.makeText(this, "$email Telah teregister", Toast.LENGTH_LONG).show()
                        val df  = fStore.collection("Profile").document(user!!.uid)
                        val data = hashMapOf<String, String>()
                        if (name.isEmpty()) {name = email}
                        data["Username"] = name
                        data["email"] = email
                        data["admin"] = "admins"

                        df.set(data)
                    } else {
                        Toast.makeText(this, "$email gagal teregister", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}