package com.hollow.attendace_app

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
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
    private lateinit var pd: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val email = findViewById<TextView>(R.id.email)
        val pass = findViewById<TextView>(R.id.pass)
        val btn = findViewById<Button>(R.id.btn)
        pd = findViewById(R.id.bar)

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        btn.isActivated = isConnected

        btn.setOnClickListener {
            if(btn.isActivated){
                val emailval: String = email.text.toString().trim()
                val passval: String = pass.text.toString().trim()

                if (emailval.isEmpty()) {
                    email.error = "Email tidak boleh kosong"
                } else if (passval.isEmpty() || passval.length < 6) {
                    pass.error = ("Password tidak boleh kosong dan kurang dari 6")
                } else {
                    pd.visibility = View.VISIBLE
                    fAuth.signInWithEmailAndPassword(emailval,passval).addOnCompleteListener {
                            task ->
                        if (task.isSuccessful) {
                            checkUser()
                        } else {
                            toast("Login Gagal")
                            pd.visibility = View.GONE
                        }
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Tidak terkoneksi jaringan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        if (fAuth.currentUser != null) {
            pd.visibility = View.VISIBLE
            checkUser()
        }
    }

    private fun checkUser() {
        val ref = fDbs.reference.child("profile").child(fAuth.uid.toString())
        ref.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    if (i.key == "level") {
                        when (i.value) {
                            "admin" -> {
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        AdminHome::class.java
                                    )
                                ); finish()
                            }
                            "user" -> {
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        UserHome::class.java
                                    )
                                );finish()
                            }
                            else -> {
                                toast("User Belum Di Daftarkan")
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}