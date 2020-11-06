package com.hollow.attendace_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hollow.attendace_app.admin.AdminHome
import com.hollow.attendace_app.user.UserHome

class MainActivity : AppCompatActivity() {

    private val fAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private val fDbs : FirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val email = findViewById<TextView>(R.id.email)
        val pass = findViewById<TextView>(R.id.pass)
        val btn = findViewById<Button>(R.id.btn)

        btn.setOnClickListener {
            val emailval: String = email.text.toString().trim()
            val passval: String = pass.text.toString().trim()

            if (emailval.isEmpty()) {
                email.error = "Email tidak boleh kosong"
            } else if (passval.isEmpty() || passval.length < 6) {
                pass.error = ("Password tidak boleh kosong dan kurang dari 6")
            } else {
                fAuth.signInWithEmailAndPassword(emailval,passval).addOnCompleteListener {
                    task ->
                    if (task.isSuccessful) {
                        checkUser()
                    } else {
                        toast("Login Gagal: unSuccessful Login")
                    }
                }
            }
        }


    }
    fun moveReg() {
        startActivity(Intent(this, UserHome::class.java))
    }
    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        if (fAuth.currentUser != null) {
            checkUser()
        }
    }

    private fun checkUser() {
        val ref = fDbs.reference.child("profile").child(fAuth.uid.toString())
        ref.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                for(i in snapshot.children) {
                    when(i.key == "level") {
                        i.value == "admin" -> {
                            startActivity(Intent(this@MainActivity, AdminHome::class.java)) ;finish()
                        } i.value == "user" -> {
                        startActivity(Intent(this@MainActivity, UserHome::class.java)) ;finish()
                    } else -> {
                        toast("User Belum Di Daftarkan")
                    }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                toast("Login Gagal: Database error")
            }
        })
    }
}