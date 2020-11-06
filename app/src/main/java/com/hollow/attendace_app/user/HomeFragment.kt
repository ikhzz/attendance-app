package com.hollow.attendace_app.user


import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.hollow.attendace_app.MainActivity
import com.hollow.attendace_app.R
import com.hollow.attendace_app.admin.AdminHome
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.IllegalArgumentException


class HomeFragment: Fragment() {

    private lateinit var fAuth : FirebaseAuth
    private lateinit var imageUri : Uri
    private var fStore: FirebaseStorage = FirebaseStorage.getInstance()
    private var fDbs: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var image : ImageView? = null
    private var email: TextView? = null
    private var user: TextView? = null
    private var sp: SharedPreferences? = null
    private var edit: SharedPreferences.Editor? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnlogout: Button? = getView()?.findViewById(R.id.logout)
        image  = getView()?.findViewById(R.id.profPic)
        email = getView()?.findViewById(R.id.emailval)
        user = getView()?.findViewById(R.id.userval)
        fAuth = FirebaseAuth.getInstance()

        sp = context?.getSharedPreferences("data", Context.MODE_PRIVATE)
        val spUri: String? = sp?.getString("url", "")
        email?.text = fAuth.currentUser?.email
        checkUser()
        if(spUri?.length!! < 2) {
            val path = context?.getExternalFilesDir(null).toString()
            val file = File(path, "profImage.jpg")
            val ref = fStore.reference.child("images/profile/${fAuth.uid}")
            ref.getFile(file).addOnSuccessListener {
                val uri = Uri.parse(file.absolutePath)
                image?.setImageURI(uri)
                toast("berhasil?")
            }.removeOnFailureListener{
                toast("gagal")
            }
            sp = context?.getSharedPreferences("data", Context.MODE_PRIVATE)
            edit = sp?.edit()
            edit?.putString("url", file.absolutePath)
            edit?.apply()
        } else {
            try {
                image?.setImageURI(Uri.parse(spUri))
            } catch (e : IllegalArgumentException) {}
        }

        image?.setOnClickListener{
            setImage()
        }
        btnlogout?.setOnClickListener {
            fAuth.signOut()
            startActivity(Intent(context,MainActivity::class.java))
            activity?.finish()
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
            imageUri = data.data!!

            val input = activity?.contentResolver?.openInputStream(data.data!!)
            val draw = Drawable.createFromStream(input, data.data.toString())
            val bitmap: Bitmap = (draw as BitmapDrawable).bitmap

            val setUri = saveData(bitmap)

            image?.setImageURI(setUri)
            uploadImg(imageUri)
        }
    }
    private fun uploadImg(data: Uri) {
        val imgName = fAuth.uid
        val ref = fStore.reference.child("images/profile/$imgName")

        ref.putFile(data)
            .addOnSuccessListener { // Get a URL to the uploaded content
                toast("Foto Profil Telah Di Upload")
            }
            .addOnFailureListener {
                toast("Foto Profil Gagal Di Upload")
            }
    }
    private fun saveData(bitmap: Bitmap): Uri {
        val path = context?.getExternalFilesDir(null).toString()
        val file = File(path, "profImage.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {}
        sp = context?.getSharedPreferences("data", Context.MODE_PRIVATE)
        edit = sp?.edit()
        edit?.putString("url", file.absolutePath)
        edit?.apply()

        return Uri.parse(file.absolutePath)
    }
    private fun toast(str: String) {
        Toast.makeText(activity,str,Toast.LENGTH_LONG).show()
    }

    private fun checkUser() {
        val ref = fDbs.reference.child("profile").child(fAuth.uid.toString())
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val child = snapshot.children
                for(i in child) {
                    if (i.key == "name") user?.text = i.value.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                toast("Login Gagal: Database error")
            }
        })
    }
}