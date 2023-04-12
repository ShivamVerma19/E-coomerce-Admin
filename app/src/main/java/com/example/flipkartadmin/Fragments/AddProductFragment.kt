package com.example.flipkartadmin.Fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.flipkartadmin.R
import com.example.flipkartadmin.adapter.AddProductImageAdapter
import com.example.flipkartadmin.databinding.FragmentAddProductBinding
import com.example.flipkartadmin.model.AddProductModel
import com.example.flipkartadmin.model.CategoryModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_add_product.*
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.fragment_slider.*
import java.util.*
import kotlin.collections.ArrayList

class AddProductFragment : Fragment() {
    private lateinit var binding : FragmentAddProductBinding
    private lateinit var list : ArrayList<Uri>
    private lateinit var listImages : ArrayList<String>
    private lateinit var adapter : AddProductImageAdapter
    private var coverImage : Uri? = null
    private lateinit var dialog : Dialog
    private var coverImageUrl : String? = ""
    private lateinit var categoryList : ArrayList<String>

    private var imageUrl : Uri? = null
    private var pickImage = 100
    private var pickImage2 = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddProductBinding.inflate(layoutInflater)
        list = ArrayList()
        listImages = ArrayList()

        setProductAdapter()
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)

        binding.coverImgBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(intent,pickImage)
        }

        binding.prodImgBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(intent,pickImage2)
        }

        adapter = AddProductImageAdapter(list)
        binding.productRv.adapter = adapter

        binding.submit.setOnClickListener {
            validateData()
        }
        return binding.root
    }

    private fun validateData() {
        if(binding.prodName.text.toString().isEmpty()){
            binding.prodName.requestFocus()
            binding.prodName.error = "Empty product name"
        }
        else if(binding.prodDesc.text.toString().isEmpty()){
            binding.prodDesc.requestFocus()
            binding.prodDesc.error = "Empty product desc"
        }
        else if(binding.prodMRP.text.toString().isEmpty()){
            binding.prodMRP.requestFocus()
            binding.prodMRP.error = "Empty product desc"
        }
        else if(binding.prodSP.text.toString().isEmpty()){
            binding.prodSP.requestFocus()
            binding.prodSP.error = "Empty product desc"
        }
        else if(coverImage == null){
            Toast.makeText(requireContext() , "Please select cover image" , Toast.LENGTH_SHORT).show()
        }
        else if(list.size < 1){
            Toast.makeText(requireContext() , "Please select product image" , Toast.LENGTH_SHORT).show()
        }
        else{
            uploadCoverImage()
        }
    }

    private fun uploadCoverImage() {
        dialog.show()

        val filename = UUID.randomUUID().toString() + ".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("products/$filename")
        refStorage.putFile(coverImage!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {
                    coverImageUrl = it.toString()

                    uploadProductImage()
                }
            }
            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext() , "Error in storage" , Toast.LENGTH_SHORT).show()
                Log.d("Failure" , it.toString())
            }
    }

    private var i = 0
    private fun uploadProductImage() {
        dialog.show()

        val filename = UUID.randomUUID().toString() + ".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("products/$filename")
        refStorage.putFile(list[i])
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {
                    listImages.add(it.toString())

                    if(list.size == listImages.size){
                        storeData()
                    }
                    else{
                        i += 1
                        uploadProductImage()
                    }
                }
            }
            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext() , "Error in storage" , Toast.LENGTH_SHORT).show()
                Log.d("Failure" , it.toString())
            }
    }

    private fun storeData() {
        val db = Firebase.firestore.collection("products")
        val key = db.document().id

        val data = AddProductModel(
            binding.prodName.text.toString() ,
            binding.prodDesc.text.toString() ,
            coverImageUrl.toString() ,
            categoryList[binding.spinner.selectedItemPosition] ,
            key ,
            binding.prodMRP.text.toString() ,
            binding.prodSP.text.toString() ,
            listImages
        )

        db.document(key).set(data)
            .addOnSuccessListener {
                dialog.dismiss()
                Toast.makeText(requireContext() , "Product Added" , Toast.LENGTH_SHORT).show()

                binding.prodName.text = null
                binding.prodDesc.text = null
                binding.prodMRP.text  = null
                binding.prodSP.text = null

                binding.prodImage.visibility = View.GONE
                list.clear()
                adapter.notifyDataSetChanged()
                binding.spinner.setSelection(0)

            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext() , "Error in adding product" , Toast.LENGTH_SHORT).show()
            }
    }


    private fun setProductAdapter() {
        categoryList = ArrayList()

        Firebase.firestore.collection("category").get()
            .addOnSuccessListener {
                categoryList.clear()

                for(doc in it.documents){
                    val data = doc.toObject(CategoryModel::class.java)
                    categoryList.add(data!!.catName!!)
                }

                categoryList.add(0 , "Select Category")
                val arrayAdapter = ArrayAdapter(requireContext() , R.layout.dropdown_addproduct , categoryList)
                binding.spinner.adapter = arrayAdapter
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == pickImage2){
            imageUrl = data?.data
            list.add(imageUrl!!)
            Log.d("Check" , list.size.toString())
            adapter.notifyDataSetChanged()
        }
        else {
            coverImage = data?.data
            prodImage.setImageURI(coverImage)
            prodImage.visibility = VISIBLE

        }
    }


}