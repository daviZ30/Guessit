package com.dezeta.guessit.ui.friend

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dezeta.guessit.R
import com.dezeta.guessit.adapter.FriendAdapter
import com.dezeta.guessit.databinding.FragmentDuelBinding
import com.dezeta.guessit.databinding.FragmentFriendBinding
import com.dezeta.guessit.ui.duel.ViewModelDuel

class FriendFragment : Fragment() {

    private var _binding: FragmentFriendBinding? = null
    private lateinit var adapterFriend: FriendAdapter
    private val binding get() = _binding!!
    private val viewModel: FriendViewModel by viewModels()
    override fun onStart() {
        super.onStart()
        viewModel.getAllUserAccounts()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFriendBinding.inflate(inflater, container, false)
        setup()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is FriendState.AddFriend -> {
                    adapterFriend.update(viewModel.userList)
                }
            }
        }
    }

    private fun setup() {
        adapterFriend = FriendAdapter()
        with(binding.rvFriend) {
            adapter = adapterFriend
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

}