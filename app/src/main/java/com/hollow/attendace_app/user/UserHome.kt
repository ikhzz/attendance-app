package com.hollow.attendace_app.user

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.os.Process
import android.os.StrictMode
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.hollow.attendace_app.MainActivity
import com.hollow.attendace_app.R
import org.apache.commons.net.ntp.NTPUDPClient
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.InetAddress
import java.text.SimpleDateFormat
import java.util.*

class UserHome : AppCompatActivity() {

    private val fAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var fStore: FirebaseStorage = FirebaseStorage.getInstance()
    private var fDbs: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var switch: Boolean = true
    private val permissionValue: Int = 10
    private lateinit var gloc : FusedLocationProviderClient
    private lateinit var glocCallback: LocationCallback
    private lateinit var sp : SharedPreferences
    private lateinit var fab: FloatingActionButton
    private lateinit var email: TextView
    private lateinit var userName: TextView
    private lateinit var dateView: TextView
    private lateinit var dayView: TextView
    private lateinit var profPic: ImageView
    private lateinit var locationRequest: LocationRequest
    private lateinit var staffName: String
    private lateinit var dates: String
    private lateinit var hour: String
    private lateinit var day : String
    private val min: Int = Calendar.getInstance().get(Calendar.MINUTE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)

        email = findViewById(R.id.userEmail)
        userName = findViewById(R.id.userName)
        dateView = findViewById(R.id.date)
        dayView = findViewById(R.id.day)
        profPic = findViewById(R.id.profPic)
        fab = findViewById(R.id.sendData)
        sp = getSharedPreferences("data", MODE_PRIVATE)
        gloc = LocationServices.getFusedLocationProviderClient(this)
        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val t = "ntp02.oal.ul.pt"
        val a = NTPUDPClient()
        a.defaultTimeout = 5000
        val addr = InetAddress.getByName(t)
        val inf = a.getTime(addr)
        hour = SimpleDateFormat("HH", Locale("english")).format(inf.message.transmitTimeStamp.time)
        dates = SimpleDateFormat("dd-MM-yyyy", Locale("english")).format(inf.message.transmitTimeStamp.date)
        day = when (hour.toInt()) {
            in 6..10 -> "Pagi"
            in 11..14 -> "Siang"
            in 15..17 -> "Sore"
            else -> "Bukan Jam Absen"
        }
        dateView.text = dates
        dayView.text = day
        email.text = fAuth.currentUser?.email
        getImage()
        getName()


        profPic.setOnClickListener {
            setImage()
        }

        fab.setOnClickListener{
            if(switch){
                if(day != "Bukan Jam Absen"){
                    buildLocationRequest()
                } else {
                    toast(day)
                }
            }else{
                toast("Sedang Menuggu Upload")
            }
        }

        locationRequest = LocationRequest()
        locationRequest.numUpdates = 1
        locationRequest.interval = 1
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        glocCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                if (p0 == null) {
                    toast("null callback") ; return
                } else {
                    for(i in p0.locations){
                        when {
                            i.latitude in -0.9030771..-0.9020997 && i.longitude in 119.8013618..119.9015238 -> takeImage()
                            else -> toast("Tidak berada di kawasan Kantor")
                        }
                    }
                }
            }
        }
    }

    private fun setImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 1)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            profPic.setImageURI(data.data!!)
            uploadImg(data.data!!, true)
        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null){
            val bitmap = data.extras?.get("data") as Bitmap
            val uri = getUri(applicationContext, bitmap)
            uploadImg(uri, false)
            sendData()
        }
    }

    private fun uploadImg(data: Uri, opt: Boolean) {
        switch = false
        val imgName = fAuth.uid
        val ref = if (opt) fStore.reference.child("images/profile/$imgName") else fStore.reference.child("presence/$dates/$day/$imgName")
        val msg: String = if (opt) "Foto Profil Telah Di Upload" else "Bukti Absen Telah di Upload"

        ref.putFile(data)
            .addOnSuccessListener { // Get a URL to the uploaded content
                toast(msg)
                switch = true
            }
            .addOnFailureListener {
                toast(msg)
                switch = true
            }
    }

    private fun getImage(){
        val path = getExternalFilesDir(null).toString()
        val file = File(path, "profImage.jpg")
        val ref = fStore.reference.child("images/profile/${fAuth.uid}")
        ref.getFile(file).addOnSuccessListener {
            val uri = Uri.parse(file.absolutePath)
            profPic.setImageURI(uri)
        }.removeOnFailureListener{}
    }

    private fun buildLocationRequest(){
        val req = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest).build()
        val client = LocationServices.getSettingsClient(this)
        val locRes: Task<LocationSettingsResponse> = client?.checkLocationSettings(req)!!
        locRes.addOnSuccessListener{
            checkLocation()
        }
    }

    private fun checkLocation(){
        if ((checkPermission( Manifest.permission.ACCESS_FINE_LOCATION, Process.myPid(), Process.myUid()) == PackageManager.PERMISSION_GRANTED)) {
            gloc.requestLocationUpdates(locationRequest, glocCallback, Looper.getMainLooper())
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionValue)
        }
    }

    private fun takeImage() {
        val intent = Intent()
        intent.action = MediaStore.ACTION_IMAGE_CAPTURE
        startActivityForResult(intent, 2)
    }

    private fun sendData() {
        val data = mapOf(
            "name" to staffName,
            "time" to "$hour:$min",
        )
        val ref = fDbs.getReference("presence")
        ref.child(dates).child(day).child(fAuth.uid.toString()).setValue(data)
        toast("absen telah diterima")
    }

    private fun getName() {
        val ref = fDbs.reference.child("profile").child(fAuth.uid.toString())
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    if (i.key == "name") {
                        staffName = i.value.toString()
                        userName.text = staffName
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                toast("Database Error")
            }
        })
    }

    private fun getUri(context: Context, image: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(context.contentResolver, image, "Title", null)
        return Uri.parse(path)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_adminmenu, menu)
        return true
    }

    fun logOut(item: MenuItem) {
        if(switch){
            fAuth.signOut()
            startActivity(Intent(this@UserHome, MainActivity::class.java))
            finish()
        } else { toast("Menuggu Upload Foto Profil") }
    }

    private fun toast(str: String) {
        Toast.makeText(this,str, Toast.LENGTH_LONG).show()
    }
}