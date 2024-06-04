package com.dezeta.guessit.ui.addFriend

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dezeta.guessit.R
import com.dezeta.guessit.adapter.FriendAdapter
import com.dezeta.guessit.databinding.FragmentAddFriendBinding
import com.dezeta.guessit.ui.friend.FriendState

class AddFriendFragment : Fragment() {

    private val viewModel: AddFriendViewModel by viewModels()

    private var _binding: FragmentAddFriendBinding? = null
    private lateinit var adapterFriend: FriendAdapter

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddFriendBinding.inflate(inflater, container, false)
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

                is FriendState.InsertFriend -> {
                    findNavController().popBackStack()
                }
            }
        }
        binding.tieAddFriend.addTextChangedListener(
            TextWatcher(
                binding.tieAddFriend.text.toString()
            )
        )
    }

    override fun onStart() {
        super.onStart()
        with(binding.lottieLoadAnimation) {
            visibility = View.VISIBLE
            setAnimation(R.raw.load_image)
            playAnimation()
        }
        viewModel.load()
    }

    private fun setup() {
        adapterFriend = FriendAdapter({ viewModel.addFriend(it) }) {}
        with(binding.rvAddFriend) {
            adapter = adapterFriend
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    inner class TextWatcher(var text: String) : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            with(binding.tieAddFriend.text.toString()) {
                adapterFriend.update(viewModel.getFilterList(this))
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }


}