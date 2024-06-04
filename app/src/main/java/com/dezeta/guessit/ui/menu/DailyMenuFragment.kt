package com.dezeta.guessit.ui.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dezeta.guessit.R
import com.dezeta.guessit.databinding.FragmentDailyMenuBinding
import com.dezeta.guessit.utils.CloudStorageManager


class DailyMenuFragment : Fragment() {

    private var _binding: FragmentDailyMenuBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ViewModelMenu by viewModels()
    private lateinit var manager: CloudStorageManager

    override fun onStart() {
        super.onStart()
        viewModel.getAllUserAccounts()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentDailyMenuBinding.inflate(inflater, container, false)
        binding.viewmodel = this.viewModel
        manager = CloudStorageManager()
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnDailyCountry.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("serie", viewModel.getCountry())
                putBoolean("local", false)
            }
            findNavController().navigate(R.id.action_categoryFragment_to_dailyFragment, bundle)
        }
        binding.btnDailyGame.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("serie", viewModel.getSerie())
                putBoolean("local", false)
            }
            findNavController().navigate(R.id.action_categoryFragment_to_dailyFragment, bundle)
        }
        binding.btnDailyFootball.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("serie", viewModel.getPlayer())
                putBoolean("local", false)
            }
            findNavController().navigate(R.id.action_categoryFragment_to_dailyFragment, bundle)
        }
        viewModel.getState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is ExtraState.refreshUserList -> {
                    refreshUserList()
                }
                is ExtraState.refreshUserProfile ->{
                    Glide.with(binding.root)
                        .load(state.url)
                        .into(state.view)
                }

                else -> {}
            }
        }
    }


    private fun refreshUserList() {
        viewModel.userList.sortByDescending { it.point }

        when (viewModel.userList.size) {
            1 -> {
                var user = viewModel.userList[0]

                with(binding) {
                    viewModel.getUserProfileImageByEmail(manager, user.email, imgProfile1)
                    tvProfile1.text = user.name
                    tvProfile1Points.text = user.point.toString()
                }
            }

            2 -> {
                with(binding) {
                    viewModel.getUserProfileImageByEmail(manager, viewModel.userList[0].email, imgProfile1)
                    viewModel.getUserProfileImageByEmail(manager, viewModel.userList[1].email, imgProfile2)
                    tvProfile1.text = viewModel.userList[0].name
                    tvProfile1Points.text = viewModel.userList[0].point.toString()

                    tvProfile2.text = viewModel.userList[1].name
                    tvProfile2Points.text = viewModel.userList[1].point.toString()
                }
            }

            in 3..Int.MAX_VALUE -> {
                with(binding) {
                    viewModel.getUserProfileImageByEmail(manager, viewModel.userList[0].email,imgProfile1)
                    viewModel.getUserProfileImageByEmail(manager, viewModel.userList[1].email,imgProfile2)
                    viewModel.getUserProfileImageByEmail(manager, viewModel.userList[2].email,imgProfile3)
                    tvProfile1.text = viewModel.userList[0].name
                    tvProfile1Points.text = viewModel.userList[0].point.toString()

                    tvProfile2.text = viewModel.userList[1].name
                    tvProfile2Points.text = viewModel.userList[1].point.toString()

                    tvProfile3.text = viewModel.userList[2].name
                    tvProfile3Points.text = viewModel.userList[2].point.toString()
                }
            }

            else -> {

            }
        }
    }

}