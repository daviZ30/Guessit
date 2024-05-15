package com.dezeta.guessit.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dezeta.guessit.databinding.SearchRowBinding

class SearchAdapter(var onClick: (str:String) -> Unit) :  RecyclerView.Adapter<SearchAdapter.host>() {
    var dataset = mutableListOf<String>()
    inner class host(var binding: SearchRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(name: String) {
            binding.tvName.text = name
            binding.tvName.gravity = Gravity.BOTTOM
            binding.Container.setOnClickListener { onClick(name) }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): host {
        var layoutInflater = LayoutInflater.from(parent.context)
        return host(SearchRowBinding.inflate(layoutInflater, parent, false))
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