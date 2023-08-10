package com.example.artifyseller

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.example.artifyseller.databinding.FragmentProfileBinding
import com.example.artifyseller.login.SignIn
import com.example.artifyseller.viewmodel.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private lateinit var vm : ViewModel
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater,container,false)

        val auth = FirebaseAuth.getInstance()

        vm = ViewModelProvider(this).get(ViewModel::class.java)

        vm.retrive_user_data {
            binding.name.setText(it.name.toString())
            binding.phoneNumber.setText(it.phone_number.toString())
            binding.email.setText(it.email.toString())
            binding.dateofbirth.setText(it.DOB.toString())
            binding.address.setText(it.address.toString())
            Picasso.get().load(it.profile_photo).into(binding.profileImage)
        }
        binding.buttonChange.setOnClickListener {
            val intent  = Intent(activity , UserInformation::class.java)
            activity?.startActivity(intent)
            activity?.finish()

        }
        val logoutButton : ImageView = binding.logoutImage

        logoutButton.setOnClickListener {
            auth.signOut()
            val intent  = Intent(activity , SignIn::class.java)
            activity?.startActivity(intent)
            activity?.finish()
        }

        return binding.root
    }


}