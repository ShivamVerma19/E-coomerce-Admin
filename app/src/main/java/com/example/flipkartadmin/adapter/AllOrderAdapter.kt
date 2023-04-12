package com.example.flipkartadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.flipkartadmin.databinding.AllOrderItemLayoutBinding
import com.example.flipkartadmin.model.AllOrderModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AllOrderAdapter(val list : ArrayList<AllOrderModel>, val context: Context) : RecyclerView.Adapter<AllOrderAdapter.AllOrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllOrderViewHolder {
        val binding = AllOrderItemLayoutBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return AllOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllOrderViewHolder, position: Int) {
        holder.binding.productTitle.text = list[position].name
        holder.binding.productPrice.text = list[position].price

        holder.binding.cancelOrderButton.setOnClickListener {
            holder.binding.proceedOrderButton.visibility = GONE

            updateStatus("Cancelled", list[position].orderId!!)
        }

        when(list[position].status){
            "Ordered" -> {
                holder.binding.proceedOrderButton.text = "Dispatched"

                holder.binding.proceedOrderButton.setOnClickListener {
                    updateStatus("Dispatched", list[position].orderId!!)
                }
            }
            "Dispatched" -> {

                holder.binding.proceedOrderButton.text = "Delivered"
                holder.binding.proceedOrderButton.setOnClickListener {
                    updateStatus("Delivered", list[position].orderId!!)
                }
            }
            "Delivered" -> {
                holder.binding.cancelOrderButton.visibility = View.GONE
                holder.binding.proceedOrderButton.isEnabled = false
                holder.binding.proceedOrderButton.text = "Already Delivered"
            }

            "Cancelled" -> {
                holder.binding.proceedOrderButton.visibility = View.GONE
                holder.binding.cancelOrderButton.isEnabled = false
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateStatus(str : String , doc : String){
        val data = hashMapOf<String , Any>()
        data["status"] = str

        Firebase.firestore.collection("allOrders").document(doc)
            .update(data).addOnSuccessListener {
                Toast.makeText(context , "Status Updated" , Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {

            }
    }

    inner class AllOrderViewHolder(val binding : AllOrderItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}