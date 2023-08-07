package com.example.artifyseller

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import com.example.artifyseller.login.SignIn

class ItemFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_item, container, false)

        val plusButton : ImageButton = view.findViewById(R.id.plusButton)

        plusButton.setOnClickListener {
            val intent  = Intent(activity , ItemAdd::class.java)
            activity?.startActivity(intent)
        }

        return  view
    }
}