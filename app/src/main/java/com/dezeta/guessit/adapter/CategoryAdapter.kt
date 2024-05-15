package com.dezeta.guessit.adapter

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dezeta.guessit.R
import com.dezeta.guessit.databinding.CategoryRowBinding
import com.dezeta.guessit.databinding.DialogCategoryBinding
import com.dezeta.guessit.databinding.SearchRowBinding

class CategoryAdapter(val listRed: List<String>,val listGreen: List<String>) :  RecyclerView.Adapter<CategoryAdapter.host>() {
    var dataset = mutableListOf<String>()
    inner class host(var binding: CategoryRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(name: String) {
            binding.tvRowCategory.text = name
            if(listRed.find { it == name } != null)
                binding.tvRowCategory.setTextColor(Color.parseColor("#FF0000"))
            if(listGreen.find { it == name } != null)
                binding.tvRowCategory.setTextColor(Color.parseColor("#2C9C12"))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): host {
        var layoutInflater = LayoutInflater.from(parent.context)
        return host(CategoryRowBinding.inflate(layoutInflater, parent, false))
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    fun update(lista: MutableList<String>) {
        dataset = lista
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: host, position: Int) {
        var nombre = dataset[position]
        holder.bind(nombre)
    }
}