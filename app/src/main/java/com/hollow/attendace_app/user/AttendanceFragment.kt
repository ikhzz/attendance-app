package com.hollow.attendace_app.user

import android.app.Activity
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.location.*
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationServices
import com.hollow.attendace_app.R

import java.text.SimpleDateFormat
import java.util.*

class AttendanceFragment : Fragment() {
    private var view: ImageView?  = null

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

        val location = context?.getSystemService(LOCATION_SERVICE) as LocationManager
        val gloc = LocationServices.getFusedLocationProviderClient(requireActivity())

        try {
            location.requestSingleUpdate(LocationManager.GPS_PROVIDER,Listen(), null )




            } catch (e: SecurityException) {

        }




        if (daypart in 6..10){
            day = "Pagi"
        } else if(daypart in 11..14) {
            day = "Siang"
        } else if(daypart in 15..17) {
            day = "Sore"
        }
        date?.text = dates
        presencevalue?.text = day
        button?.setOnClickListener {
            takeImage()
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
            val view: ImageView? = getView()?.findViewById(R.id.prev)
            //photo = data.extras
            toast("i'm here")
            view?.setImageBitmap(data.extras?.get("data") as Bitmap)
        }
    }
    private fun toast(data: String){
        Toast.makeText(activity,data,Toast.LENGTH_LONG).show()
    }
    public class Listen : LocationListener {
        override fun onLocationChanged(location: Location?) {

        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        }

        override fun onProviderEnabled(provider: String?) {

        }

        override fun onProviderDisabled(provider: String?) {

        }

    }
}





