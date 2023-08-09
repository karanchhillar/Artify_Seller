package com.example.artifyseller

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.artifyseller.databinding.FragmentHomeBinding
import com.example.artifyseller.databinding.FragmentProfileBinding
import com.example.artifyseller.viewmodel.ViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var vm : ViewModel
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
//    private lateinit var name : String
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)

        auth = FirebaseAuth.getInstance()
        vm = ViewModelProvider(this).get(ViewModel::class.java)

        vm.retrive_user_data {
//            name= it.name.toString()
            binding.nameTextView.text = "Welcome, ${it.name.toString()}"
        }

//        binding.nameTextView.text = "Welcome, $name"

        return binding.root
    }

}