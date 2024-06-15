package com.dezeta.guessit.ui.friend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dezeta.guessit.R
import com.dezeta.guessit.adapter.FriendAdapter
import com.dezeta.guessit.databinding.FragmentFriendBinding
import com.dezeta.guessit.utils.Locator

class FriendFragment : Fragment() {
    private var _binding: FragmentFriendBinding? = null
    private lateinit var adapterFriend: FriendAdapter
    private val binding get() = _binding!!
    private val viewModel: FriendViewModel by viewModels()
    override fun onStart() {
        super.onStart()
        with(binding.lottieLoadAnimation) {
            visibility = View.VISIBLE
            setAnimation(R.raw.load_image)
            playAnimation()
        }
        viewModel.userList = mutableListOf()
        viewModel.getAllFriend()
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
                    binding.lottieLoadAnimation.cancelAnimation()
                    binding.lottieLoadAnimation.visibility = View.INVISIBLE
                    adapterFriend.update(viewModel.userList)
                }

                else -> {}
            }
        }
        val navOptions = NavOptions.Builder()
            .setEnterAnim(android.R.anim.fade_in)
            .setExitAnim(android.R.anim.fade_out)
            .setPopEnterAnim(android.R.anim.slide_in_left)
            .setPopExitAnim(android.R.anim.slide_out_right)
            .build()
        binding.btnAddFriend.setOnClickListener {
            findNavController().navigate(
                R.id.action_friendFragment_to_addFriendFragment,
                null,
                navOptions
            )
        }
    }

    private fun setup() {
        adapterFriend = FriendAdapter({}) {
            if (it.email != Locator.email) {
                viewModel.removeFriend(it)
                adapterFriend.update(viewModel.userList)
            }

        }
        with(binding.rvFriend) {
            adapter = adapterFriend
            layoutManager = LinearLayoutManager(requireContext())
        }
    }


}