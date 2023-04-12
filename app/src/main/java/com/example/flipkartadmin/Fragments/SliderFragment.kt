package com.example.flipkartadmin.Fragments

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.flipkartadmin.R
import com.example.flipkartadmin.databinding.FragmentSliderBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_slider.*
import java.util.*

class SliderFragment : Fragment() {
    private lateinit var binding : FragmentSliderBinding
    private var imageUrl : Uri? = null
    private var pickImage = 100
    private lateinit var dialog : Dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ConstraintLayout? {
        binding = FragmentSliderBinding.inflate(layoutInflater)

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)

        binding.imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(intent,pickImage)
        }

        binding.uploadSlider.setOnClickListener {
            if(imageUrl == null){
                Toast.makeText(requireContext() , "Please select a image" , Toast.LENGTH_SHORT).show()
            }
            else{
                uploadImage(imageUrl!!)
            }
        }
        return binding.root
    }

    private fun uploadImage(uri: Uri){
        dialog.show()

        val filename = UUID.randomUUID().toString() + ".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("slider/$filename")
        refStorage.putFile(uri)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { 
                    storeData(it.toString())
                }
            }
            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext() , "Error in storage" , Toast.LENGTH_SHORT).show()
                Log.d("Failure" , it.toString())
            }
    }

    private fun storeData(image: String) {
        val db = Firebase.firestore

        val data = hashMapOf<String , Any>(
            "img" to image
        )

        db.collection("slider").document("items").set(data)
            .addOnSuccessListener {
                dialog.dismiss()
                Toast.makeText(requireContext() , "Slider uploaded successfully" , Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext() , "Something went wrong in storing data to firebase" , Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && requestCode == pickImage){
            imageUrl = data?.data
            imageView.setImageURI(imageUrl)
        }
    }

}