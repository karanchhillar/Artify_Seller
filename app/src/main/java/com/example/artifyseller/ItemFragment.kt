package com.example.artifyseller

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.artifyseller.adapter.ItemAdapter
import com.example.artifyseller.databinding.FragmentItemBinding
import com.example.artifyseller.databinding.FragmentProfileBinding
import com.example.artifyseller.login.SignIn
import com.example.artifyseller.viewmodel.ViewModel

class ItemFragment : Fragment() {

    private var _binding : FragmentItemBinding? = null
    private lateinit var vm : ViewModel
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentItemBinding.inflate(inflater,container,false)

        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProvider(this).get(ViewModel::class.java)
        val itemAdapter = ItemAdapter()

        vm.myItem.observe(viewLifecycleOwner, Observer {
            itemAdapter.setItemList1(it)
        })

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = itemAdapter

        binding.plusButton.setOnClickListener {
            val intent  = Intent(activity , ItemAdd::class.java)
            activity?.startActivity(intent)
        }
    }
}