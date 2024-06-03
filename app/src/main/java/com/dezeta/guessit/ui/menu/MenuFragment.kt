package com.dezeta.guessit.ui.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dezeta.guessit.R
import com.dezeta.guessit.databinding.FragmentMenuBinding
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.ui.main.MainActivity
import kotlin.random.Random

class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    var testNum = 0
    lateinit var testlist: List<Guess>
    private val binding get() = _binding!!

    private val viewModel: ViewModelMenu by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // findNavController().navigate(R.id.loginFragment)
        testlist = viewModel.getTestList().shuffled()
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).updateHeader()
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
        binding.cvTest.setOnClickListener {
            val bundle = Bundle().apply {
                if (testNum + 2 >= viewModel.getTestList().size) {
                    testlist = viewModel.getTestList().shuffled()
                }

                putSerializable("guess0", testlist[testNum])
                putSerializable("guess1", testlist[testNum + 1])
                putSerializable("guess2", testlist[testNum + 2])

                testNum += 3

                putBoolean("local", false)
            }
            findNavController().navigate(R.id.action_MenuFragment_to_testFragment, bundle)
        }
        binding.cvDaily.setOnClickListener {

            findNavController().navigate(R.id.action_MenuFragment_to_categoryFragment)
        }
        binding.cvDuel.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_duelFragment)
        }
        binding.cvLocalGame.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_localGameFragment)
        }
        binding.btnAboutUs.setOnClickListener {
            findNavController().navigate(R.id.action_MenuFragment_to_friendFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}