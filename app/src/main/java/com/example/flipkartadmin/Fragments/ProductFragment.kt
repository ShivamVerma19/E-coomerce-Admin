package com.example.flipkartadmin.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.flipkartadmin.R
import com.example.flipkartadmin.databinding.FragmentProductBinding

class ProductFragment : Fragment() {

    private lateinit var binding : FragmentProductBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductBinding.inflate(layoutInflater)

        binding.floatingActionButton.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_productFragment3_to_addProductFragment3)
        }
        return binding.root
    }
}