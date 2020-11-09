package com.hollow.attendace_app.user

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.os.Process.myPid
import android.os.Process.myUid
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.hollow.attendace_app.R
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AttendanceFragment : Fragment() {
    private val permissionValue: Int = 10
    private lateinit var locationRequest: LocationRequest
    private lateinit var gloc : FusedLocationProviderClient
    private lateinit var glocCallback: LocationCallback
    private var fAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private var fStore: FirebaseStorage = FirebaseStorage.getInstance()
    private var fDbs: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val dates = SimpleDateFormat("dd-MM-yyyy", Locale("english")).format(Calendar.getInstance().time)
    private lateinit var day : String
    private lateinit var name : String
    private val hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    private val min: Int = Calendar.getInstance().get(Calendar.MINUTE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.attendance_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val date: TextView? = getView()?.findViewById(R.id.datevalue)
        val presencevalue: TextView? = getView()?.findViewById(R.id.presenceValue)
        val button: Button? = getView()?.findViewById(R.id.button)


        gloc = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationRequest = LocationRequest()
        locationRequest.numUpdates = 1
        locationRequest.priority = PRIORITY_HIGH_ACCURACY
        glocCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                if (p0 == null) {
                    toast("null callback") ; return
                } else {
                    for(i in p0.locations){
                        when {
                            i.latitude in -0.9007687..-0.9002286 && i.longitude in 119.8913144..119.8916685 -> takeImage()
                            else -> toast("Tidak berada di kawasan Kantor")
                        }
                    }
                }
            }
        }

        day = when (hour) {
            in 6..10 -> "Pagi"
            in 11..14 -> "Siang"
            in 15..17 -> "Sore"
            else -> "Bukan Jam Absen"
        }
        getName()
        date?.text = dates
        presencevalue?.text = day
        button?.setOnClickListener {
            if (day != "Bukan Jam Absen") {
                if (context?.checkPermission(ACCESS_FINE_LOCATION, myPid(), myUid()) == PackageManager.PERMISSION_GRANTED) {
                    checkLoc()
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(), arrayOf(
                            ACCESS_FINE_LOCATION
                        ), permissionValue
                    )
                }
            } else {
                toast("Bukan Jam Absen")
            }
        }
    }

    private fun takeImage() {
        val intent = Intent()
        intent.action = MediaStore.ACTION_IMAGE_CAPTURE
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            val bitmap = data.extras?.get("data") as Bitmap
            val uri = getUri(requireContext().applicationContext, bitmap)
            uploadImg(uri)
            sendData()
        }
    }

    private fun toast(data: String){
        Toast.makeText(activity, data, Toast.LENGTH_LONG).show()
    }

    private fun checkLoc() {
        val req = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest).build()
        val client = context?.let { LocationServices.getSettingsClient(it) }
        val locRes: Task<LocationSettingsResponse> = client?.checkLocationSettings(req)!!
        locRes.addOnSuccessListener{
            startCheck()
        }
    }

    private fun startCheck() {
        if ((context?.checkPermission(ACCESS_FINE_LOCATION, myPid(), myUid()) == PackageManager.PERMISSION_GRANTED)) {
            gloc.requestLocationUpdates(locationRequest, glocCallback, Looper.getMainLooper())
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(ACCESS_FINE_LOCATION),
                permissionValue
            )
        }
    }

    private fun uploadImg(data: Uri) {
        val imgName = fAuth.uid
        val ref = fStore.reference.child("presence/$dates/$day/$imgName")

        ref.putFile(data)
            .addOnSuccessListener { // Get a URL to the uploaded content
                toast("Foto Absen Telah Di Upload")
            }
            .addOnFailureListener {
                toast("Foto Absen Gagal Di Upload")
            }
    }

    private fun sendData() {
        val data = mapOf(
            "name" to name,
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
                        name = i.value.toString()
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
}





