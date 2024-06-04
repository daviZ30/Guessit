package com.dezeta.guessit.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dezeta.guessit.databinding.FriendRowBinding
import com.dezeta.guessit.domain.entity.User

class FriendAdapter(var onClick: (user: User) -> Unit, var onLongClick: (user: User) -> Unit) :
    RecyclerView.Adapter<FriendAdapter.host>() {
    var dataset = mutableListOf<User>()

    inner class host(var binding: FriendRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.tvRowFriendEmail.text = user.name
            binding.tvRowFriendPoint.text = user.point.toString()

            Glide.with(binding.root)
                .load(user.img_url)
                .into(binding.imgRowFriendProfile)
            binding.cvRowFriend.setOnClickListener {
                onClick(user)
            }
            binding.cvRowFriend.setOnLongClickListener {
                onLongClick(user)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): host {
        val layoutInflater = LayoutInflater.from(parent.context)
        return host(FriendRowBinding.inflate(layoutInflater, parent, false))
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    fun update(lista: MutableList<User>) {
        dataset = lista
        dataset.sortByDescending { it.point }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: host, position: Int) {
        val nombre = dataset[position]
        holder.bind(nombre)
    }
}