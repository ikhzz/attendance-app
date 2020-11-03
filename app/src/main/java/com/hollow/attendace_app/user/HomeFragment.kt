package com.hollow.attendace_app.user


import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hollow.attendace_app.R
import java.lang.IllegalArgumentException
import java.util.*


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
            Toast.makeText(activity,"tes1",Toast.LENGTH_SHORT).show()
            try {
                Toast.makeText(activity,str,Toast.LENGTH_SHORT).show()
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

            val image: ImageView? = view?.findViewById(R.id.profPic)

            image?.setImageURI(imageUri)
            uploadImg()
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
                    val sp: SharedPreferences? = context?.getSharedPreferences("data", Context.MODE_PRIVATE)
                    val edit = sp?.edit()
                    edit?.putString("url", b.toString())
                    edit?.apply()
                }
            }
            .addOnFailureListener {
                // Handle unsuccessful uploads
                // ...
            }
    }
//    private fun setProfile() {
//        val intent = Intent()
//        intent.type = "Uri"
//        intent.action = Intent.ACTION_OPEN_DOCUMENT
//        startActivityForResult(intent, 2)
//    }
}