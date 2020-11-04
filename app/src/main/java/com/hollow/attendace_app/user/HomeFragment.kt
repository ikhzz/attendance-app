package com.hollow.attendace_app.user


import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hollow.attendace_app.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.IllegalArgumentException
import java.util.*
import java.util.stream.Stream


class HomeFragment: Fragment() {

    private lateinit var fStore : FirebaseDatabase
    private lateinit var imageUri : Uri
    private lateinit var storageRef: FirebaseStorage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val image : ImageView? = getView()?.findViewById(R.id.profPic)
        fStore = FirebaseDatabase.getInstance()
        storageRef = FirebaseStorage.getInstance()
        val sp: SharedPreferences? = context?.getSharedPreferences("data", Context.MODE_PRIVATE)
        val str: String? = sp?.getString("url", "")

        if(!(str.isNullOrEmpty())) {
            //Toast.makeText(activity,"tes1",Toast.LENGTH_SHORT).show()
            try {
                //Toast.makeText(activity,str,Toast.LENGTH_SHORT).show()
                val url : Uri = Uri.parse(str)
                image?.setImageURI(url)

            } catch (e : IllegalArgumentException) {

            }
        }

        image?.setOnClickListener{
            setImage()
        }


//        val ref = fStore.getReference("profile").child("ikhz")
//        ref.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for(i in snapshot.children) {
//                    if(i.key == "image") {
//                        //Toast.makeText(activity,i.value.toString(),Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//        })
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
            imageUri = data.data!!

            val input = activity?.contentResolver?.openInputStream(data.data!!)
            val draw = Drawable.createFromStream(input, data.data.toString())
            var bitmap: Bitmap = (draw as BitmapDrawable).bitmap

            saveData(bitmap)

            val image: ImageView? = view?.findViewById(R.id.profPic)

            //image?.setImageURI(setUri)
            //uploadImg()
        }
//        if(requestCode == 2) {
//            val sp: SharedPreferences? = context?.getSharedPreferences("data",Context.MODE_PRIVATE)
//            val str: String? = sp?.getString("url","")
//            val image : ImageView? = getView()?.findViewById(R.id.profPic)
//            try {
//                imageUri = Uri.parse(str)
//                image?.setImageURI(imageUri)
//            } catch (e : IllegalArgumentException) {
//
//            }
//        }
    }
    private fun uploadImg() {
        val imgName = "tes"
        val ref: StorageReference = storageRef.reference.child("images/$imgName")

        ref.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot -> // Get a URL to the uploaded content
                val downloadUrl = taskSnapshot.uploadSessionUri?.path
                val a = ref.downloadUrl.continueWith{
                    tast ->
                    val b = tast.result
                    Toast.makeText(activity,b.toString(),Toast.LENGTH_SHORT).show()

                }
            }
            .addOnFailureListener {
                // Handle unsuccessful uploads
                // ...
            }
    }
    private fun saveData(bitmap: Bitmap) {
        //activity?.applicationContext.getf
        var f3 = File(Environment.getExternalStorageState() + "/tes/")
        f3.mkdir()
        val path = Environment.getExternalStorageState()
        val file = File(f3, "tes.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {

        }
        val sp: SharedPreferences? = context?.getSharedPreferences("data", Context.MODE_PRIVATE)
        val edit = sp?.edit()
        edit?.putString("url", file.absolutePath)
        edit?.apply()
        toast(file.absolutePath)
        val image: ImageView? = view?.findViewById(R.id.profPic)
        image?.setImageURI(Uri.parse(file.absolutePath))
        //return Uri.parse(file.absolutePath)
    }
    private fun toast(str: String) {
        Toast.makeText(activity,str,Toast.LENGTH_LONG).show()
    }
//    private fun setProfile() {
//        val intent = Intent()
//        intent.type = "Uri"
//        intent.action = Intent.ACTION_OPEN_DOCUMENT
//        startActivityForResult(intent, 2)
//    }
}