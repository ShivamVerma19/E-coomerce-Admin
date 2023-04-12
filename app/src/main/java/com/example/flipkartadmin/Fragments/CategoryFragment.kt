package com.example.flipkartadmin.Fragments

import android.app.Activity
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
import com.example.flipkartadmin.R
import com.example.flipkartadmin.adapter.CategoryAdapter
import com.example.flipkartadmin.databinding.FragmentCategoryBinding
import com.example.flipkartadmin.model.CategoryModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.fragment_slider.*
import java.util.*
import kotlin.collections.ArrayList

class CategoryFragment : Fragment() {
    private lateinit var binding : FragmentCategoryBinding
    private var imageUrl : Uri? = null
    private var pickImage = 101
    private lateinit var dialog : Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCategoryBinding.inflate(layoutInflater)
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)

        getData()
        binding.imageView2.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(intent,pickImage)
        }

        binding.uploadCategory.setOnClickListener {
            validateData(binding.categoryName.text.toString())
        }
        return binding.root
    }

    private fun getData() {
        val list = ArrayList<CategoryModel>()
        Firebase.firestore.collection("category").get()
            .addOnSuccessListener {
                list.clear()
                for(doc in it.documents){
                    val data = doc.toObject(CategoryModel::class.java)
                    list.add(data!!)
                }

                binding.catRv.adapter = CategoryAdapter(requireContext() , list)
            }
    }

    private fun validateData(catName: String) {
        if(catName.isEmpty()){
            Toast.makeText(requireContext() , "Please provide category name" , Toast.LENGTH_SHORT).show()
        }
        else if(imageUrl == null){
            Toast.makeText(requireContext() , "Please add image" , Toast.LENGTH_SHORT).show()
        }
        else{
            uploadImage(catName)
        }
    }

    private fun uploadImage(catName: String) {
        dialog.show()

        val filename = UUID.randomUUID().toString() + ".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("category/$filename")
        refStorage.putFile(imageUrl!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {
                    storeData(it.toString(),catName)
                }
            }
            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext() , "Error in storage" , Toast.LENGTH_SHORT).show()
                Log.d("Failure" , it.toString())
            }
    }

    private fun storeData(url: String, catName: String) {
        val db = Firebase.firestore

        val data = hashMapOf<String , Any>(
            "catName" to catName ,
            "img" to url
        )

        db.collection("category").add(data)
            .addOnSuccessListener {
                dialog.dismiss()
                binding.imageView2.setImageDrawable(resources.getDrawable(R.drawable.imgprv))
                binding.categoryName.text = null
                getData()
                Toast.makeText(requireContext() , "Category uploaded successfully" , Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext() , "Something went wrong in storing Category to firebase" , Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == pickImage){
            imageUrl = data?.data
            imageView2.setImageURI(imageUrl)
        }
    }

}