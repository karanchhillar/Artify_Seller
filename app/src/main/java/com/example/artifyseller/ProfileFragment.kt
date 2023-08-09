package com.example.artifyseller

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.artifyseller.databinding.FragmentProfileBinding
import com.example.artifyseller.login.SignIn
import com.example.artifyseller.viewmodel.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import java.util.zip.Inflater

class ProfileFragment : Fragment() {

    private lateinit var vm : ViewModel
    private var _binding : FragmentProfileBinding? = null
//    private val binding get() = _binding!!
    private lateinit var binding : FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val auth = FirebaseAuth.getInstance()

        vm = ViewModelProvider(this).get(ViewModel::class.java)

        vm.name_text.observe(viewLifecycleOwner, Observer {
            binding.name.setText(it)
        })

        vm.profile_photo.observe(viewLifecycleOwner, Observer {
            Picasso.get().load(it).into(binding.profileImage)
        })

        vm.retrive_user_data {
//            binding.name.setText(it.name.toString())
            binding.phoneNumber.setText(it.phone_number.toString())
            binding.email.setText(it.email.toString())
            binding.dateofbirth.setText(it.DOB.toString())
            binding.address.setText(it.address.toString())
//            Picasso.get().load(it.profile_photo).into(binding.profileImage)
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

    }

}