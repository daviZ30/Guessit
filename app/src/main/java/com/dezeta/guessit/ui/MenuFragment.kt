package com.dezeta.guessit.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dezeta.guessit.R
import com.dezeta.guessit.databinding.FragmentMenuBinding
import com.dezeta.guessit.usecase.ViewModelMenu
import kotlin.random.Random

class MenuFragment : Fragment() {
    private var random = Random(98765498)
    private var _binding: FragmentMenuBinding? = null

    private val binding get() = _binding!!

    private val viewModel: ViewModelMenu by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // findNavController().navigate(R.id.loginFragment)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cvLevels.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.cvDaily.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("serie", viewModel.getSerie())
                putBoolean("local", false)
            }
            findNavController().navigate(R.id.action_FirstFragment_to_dailyFragment, bundle)
        }
        binding.cvDuel.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_duelFragment)
        }
        binding.cvLocalGame.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_localGameFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}