package com.hollow.attendace_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.hollow.attendace_app.admin.AdminHome

class Register : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val emailView : EditText = findViewById(R.id.email)
        val passView : EditText = findViewById(R.id.pass)
        val button : Button = findViewById(R.id.btn)

        mAuth = FirebaseAuth.getInstance()

        button.setOnClickListener {
            val email: String = emailView.text.toString().trim()
            val pass: String = passView.text.toString().trim()

            if(email.isEmpty()) {
                emailView.error = "Email tidak boleh kosong"
            } else if (pass.isEmpty() || pass.length < 6) {
                passView.error = ("Password tidak boleh kosong dan kurang dari 6")
            } else {
//                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
//                    task ->
//                    if (task.isSuccessful) {
//                        Toast.makeText(this, email + " Telah teregister", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, AdminHome::class.java)); finish()

//                    } else {
//                        Toast.makeText(this, "$email gagal teregister", Toast.LENGTH_LONG).show()
//                    }
//                }
            }
        }
    }
}