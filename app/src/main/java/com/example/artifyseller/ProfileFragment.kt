package com.example.artifyseller

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.example.artifyseller.login.SignIn
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_profile, container, false)

        val auth = FirebaseAuth.getInstance()

        val logoutButton : ImageView = view.findViewById(R.id.logoutImage)

        logoutButton.setOnClickListener {
            auth.signOut()
            val intent  = Intent(activity , SignIn::class.java)
            activity?.startActivity(intent)
            activity?.finish()
        }

        return view
    }

}