package com.hollow.attendace_app

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SoundEffectConstants
import android.view.View
import android.widget.*
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
    private lateinit var audiomanager: AudioManager
    private lateinit var cm: ConnectivityManager
    private lateinit var pd: ProgressBar
    private lateinit var email: EditText
    private lateinit var pass: EditText
    private lateinit var btn: Button
    private var isConnected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set Edit Text, Button and ProgressBar
        email = findViewById(R.id.email)
        pass = findViewById(R.id.pass)
        btn = findViewById(R.id.btn)
        pd = findViewById(R.id.bar)
        // Set Audio Manager
        audiomanager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        // Network Check
        cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        isConnected = activeNetwork?.isConnected == true
        btn.isActivated = isConnected

        btn.setOnClickListener {
        //  Play Click
            audiomanager.playSoundEffect(SoundEffectConstants.CLICK, 1.0f)
            // Check if Network Connected
            if(btn.isActivated){
            // Get Text From Edit Text
                val emailval: String = email.text.toString().trim()
                val passval: String = pass.text.toString().trim()
                // Check if Edit Text is empty or pass Edit Text is less than 6 char
                if (emailval.isEmpty()) {
                    email.error = "Email tidak boleh kosong"
                } else if (passval.isEmpty() || passval.length < 6) {
                    pass.error = ("Password tidak boleh kosong dan kurang dari 6")
                } else {
                    pd.visibility = View.VISIBLE
                    // Get Email and pass value if success run checkUser
                    fAuth.signInWithEmailAndPassword(emailval,passval).addOnCompleteListener {
                            task ->
                        if (task.isSuccessful) {
                            checkUser()
                        } else {
                        // Send Message Email not found
                            toast("Email Tidak Terdaftar")
                            pd.visibility = View.GONE
                        }
                    }
                }
            } else {
                // Send Toast Network is Not Connected
                toast("Tidak Ada Koneksi")
            }
        }
    }
    // Toast method
    private fun toast(text: String) {
        // Send toast Method
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
    // Method on start if current user is not null run checkUser method
    override fun onStart() {
        super.onStart()
        if (fAuth.currentUser != null) {
            pd.visibility = View.VISIBLE
            checkUser()
        }
    }
    // Method Check User and Send to Respective Activity
    private fun checkUser() {
        val ref = fDbs.reference.child("profile").child(fAuth.uid.toString())
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    if (i.key == "level") {
                        when (i.value) {
                            "admin" -> { startActivity( Intent(this@MainActivity, AdminHome::class.java)); finish() }
                            "user" -> { startActivity( Intent(this@MainActivity, UserHome::class.java)); finish() }
                            else -> { toast("User Belum Di Daftarkan") }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) { toast("Koneksi Ke database gagal")}
        })
    }
}