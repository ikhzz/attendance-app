package com.hollow.attendace_app.user

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.*
import android.os.Bundle
import android.os.Looper
import android.os.Process.myPid
import android.os.Process.myUid
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.Task
import com.hollow.attendace_app.R
import java.lang.Exception
import java.security.Permission

import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class AttendanceFragment : Fragment() {
    private val PERMISSION: Int = 10
    private var view: ImageView?  = null
    private lateinit var locationRequest: LocationRequest
    private lateinit var gloc : FusedLocationProviderClient
    private lateinit var glocCallback: LocationCallback

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
        val loc: TextView? = getView()?.findViewById(R.id.loc)
        val daypart = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)


        val dates = SimpleDateFormat("dd-MM-yyyy",Locale("english")).format(Calendar.getInstance().time)
        var day = "Bukan Jam Absen"

        gloc = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationRequest = LocationRequest()
        locationRequest.interval = 1000
        locationRequest.numUpdates = 2
        locationRequest.priority = PRIORITY_HIGH_ACCURACY
        glocCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                if (p0 == null) {
                    toast("null callback") ; return
                } else {
                    for(i in p0.locations){
                        when {
                            i.latitude in -0.9028997..-0.9022747 && i.longitude in 119.8713618..119.8715238 -> takeImage()
                            else -> toast("Tidak berada di kawasan Kantor")
                        }
                    }
                }

            }
        }

        when (daypart) {
            in 6..10 -> day = "Pagi"
            in 11..14 -> day = "Siang"
            in 15..17 -> day = "Sore"
        }

        date?.text = dates
        presencevalue?.text = day
        button?.setOnClickListener {
            if (context?.checkPermission(ACCESS_FINE_LOCATION, myPid(), myUid() ) == PackageManager.PERMISSION_GRANTED) {
                checkLoc()
            } else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(ACCESS_FINE_LOCATION), PERMISSION)
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
            //val view: ImageView? = getView()?.findViewById(R.id.prev)
            //view?.setImageBitmap(data.extras?.get("data") as Bitmap)
            Toast.makeText(requireContext(),"tes", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toast(data: String){
        Toast.makeText(activity,data,Toast.LENGTH_LONG).show()
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
        if ((context?.checkPermission(ACCESS_FINE_LOCATION, myPid(), myUid() ) == PackageManager.PERMISSION_GRANTED)) {
            gloc.requestLocationUpdates(locationRequest, glocCallback, Looper.getMainLooper())
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(ACCESS_FINE_LOCATION), PERMISSION)
        }
    }
}





