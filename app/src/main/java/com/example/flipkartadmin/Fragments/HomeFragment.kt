package com.example.flipkartadmin.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.flipkartadmin.R
import com.example.flipkartadmin.activity.AllOrderActivity
import com.example.flipkartadmin.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)

        binding.cat.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment4_to_categoryFragment2)
        }

        binding.prod.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment4_to_productFragment3)
        }

        binding.slid.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment4_to_sliderFragment4)
        }

        binding.details.setOnClickListener {
            startActivity(Intent(requireContext() , AllOrderActivity::class.java))
        }
        return binding.root
    }


}