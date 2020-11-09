package com.hollow.attendace_app.admin.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hollow.attendace_app.R

class CreateUser: Fragment() {

    private lateinit var fAuth: FirebaseAuth
    private var fDbs : FirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.createuser_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button: Button = requireView().findViewById(R.id.btnreg)
        val emailView: EditText = requireView().findViewById(R.id.email)
        val passView: EditText = requireView().findViewById(R.id.pass)
        val nameView: EditText = requireView().findViewById(R.id.name)

        fAuth = FirebaseAuth.getInstance()

        button.setOnClickListener {
            val email = emailView.text.toString().trim()
            val pass = passView.text.toString().trim()
            val name = nameView.text.toString().trim()

            if (email.isEmpty()) {
                emailView.error = "Email tidak boleh kosong"
            } else if (pass.isEmpty() || pass.length < 6) {
                passView.error = ("Password tidak boleh kosong dan kurang dari 6")
            } else if(name.isEmpty() || name.length < 3) {
                nameView.error = ("Nama tidak boleh kosong")
            }else{
                fAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                        task ->
                    if (task.isSuccessful) {
                        val user = fAuth.uid.toString()
                        Toast.makeText(requireContext(), "$email Telah terdaftar dengan nama: $name", Toast.LENGTH_LONG).show()
                        val ref = fDbs.getReference("profile")
                        val uData = mapOf(
                            "name" to name,
                            "level" to "user",
                        )
                        ref.child(user).setValue(uData)

                    } else {
                        Toast.makeText(requireContext(), "$email gagal terdaftar", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}