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
import com.hollow.attendace_app.R

class ResetPass: Fragment() {

    private lateinit var fAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.resetpass_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button: Button = requireView().findViewById(R.id.sendpass)
        val emailView: EditText = requireView().findViewById(R.id.email)
        val passView: EditText = requireView().findViewById(R.id.pass)

        button.setOnClickListener {
            val email = emailView.text.toString().trim()
            val pass = passView.text.toString().trim()

            if (email.isEmpty()) {
                emailView.error = "Email tidak boleh kosong"
            } else if (pass.isEmpty() || pass.length < 6) {
                passView.error = ("Password tidak boleh kosong dan kurang dari 6")
            } else {
                val user = fAuth.currentUser

                user!!.updatePassword(pass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Password telah di update", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Password gagal di update", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}