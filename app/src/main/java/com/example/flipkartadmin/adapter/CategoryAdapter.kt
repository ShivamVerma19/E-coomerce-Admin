package com.example.flipkartadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flipkartadmin.R
import com.example.flipkartadmin.databinding.ItemCategoryRvBinding
import com.example.flipkartadmin.model.CategoryModel

class CategoryAdapter(var context : Context , val list : ArrayList<CategoryModel>): RecyclerView.Adapter<CategoryAdapter.categoryViewHolder>() {

    inner class categoryViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        var binding = ItemCategoryRvBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): categoryViewHolder {
        return categoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_category_rv , parent , false))
    }

    override fun onBindViewHolder(holder: categoryViewHolder, position: Int) {

        holder.binding.itemCatTv.text = list[position].catName
        Glide.with(context).load(list[position].img).into(holder.binding.itemCatImg)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}