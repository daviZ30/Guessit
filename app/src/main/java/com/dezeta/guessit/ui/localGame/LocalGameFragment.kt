package com.dezeta.guessit.ui.localGame

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dezeta.guessit.R
import com.dezeta.guessit.databinding.FragmentLocalGameBinding
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.showSnackbar
import kotlin.random.Random


class LocalGameFragment : Fragment() {
    private var random = Random(98765498)
    private var _binding: FragmentLocalGameBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ViewModelLocalGame by viewModels()
    private var guessDialog: Guess? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocalGameBinding.inflate(inflater, container, false)
        binding.viewmodel = this.viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.updateStatistics()
        binding.btnNewLevel.setOnClickListener {
            findNavController().navigate(R.id.action_localGameFragment_to_createFragment)
        }
        binding.btnLocalGame.setOnClickListener {
            showConfirmationDialog()
        }
        binding.btnDeleteLevel.setOnClickListener {
            showCustomDialog(viewModel.getLevelList(),true)


        }
        viewModel.getState().observe(viewLifecycleOwner) {
            when (it) {
                is LocalGameState.DeleteError -> {
                    showSnackbar(requireView(), ContextCompat.getString(requireContext(),R.string.deleteError))
                    println("Error: ${it.exception}")
                }

                is LocalGameState.Success -> {
                    showSnackbar(requireView(), ContextCompat.getString(requireContext(),R.string.deleteSuccess))
                }

            }
        }

    }

    private fun showCustomDialog(list: List<Guess>,delete:Boolean) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_localgame)
        dialog.setCancelable(true)
        val navOptions = NavOptions.Builder()
            .setEnterAnim(android.R.anim.fade_in)
            .setExitAnim(android.R.anim.fade_out)
            .setPopEnterAnim(android.R.anim.slide_in_left)
            .setPopExitAnim(android.R.anim.slide_out_right)
            .build()
        val spinner = dialog.findViewById(R.id.spinner) as Spinner
        val btnAccept = dialog.findViewById(R.id.btnDialog) as Button

        val adapterspDialog =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                list
            )

        adapterspDialog.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapterspDialog

        btnAccept.setOnClickListener {
            if (spinner.selectedItem != null) {
                guessDialog = spinner.selectedItem as Guess
                when{
                    delete && guessDialog!= null ->{
                        viewModel.deletefromId(guessDialog!!.id)
                        viewModel.createdGames.value = (viewModel.createdGames.value!!.toInt() - 1).toString()
                    }
                    !delete && guessDialog!= null ->{
                        val bundle = Bundle().apply {
                            putSerializable("serie", guessDialog)
                            putBoolean("local", true)
                        }
                        findNavController().navigate(R.id.action_localGameFragment_to_dailyFragment, bundle,navOptions)
                    }
                    else -> {
                        showSnackbar(requireView(),ContextCompat.getString(requireContext(),R.string.levelNotFound))
                    }
                }
                dialog.hide()
            } else {
                showSnackbar(requireView(), ContextCompat.getString(requireContext(),R.string.introduceLevel))
            }
        }
        dialog.show()
    }

    private fun showConfirmationDialog() {
        val navOptions = NavOptions.Builder()
            .setEnterAnim(android.R.anim.fade_in)
            .setExitAnim(android.R.anim.fade_out)
            .setPopEnterAnim(android.R.anim.slide_in_left)
            .setPopExitAnim(android.R.anim.slide_out_right)
            .build()
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(ContextCompat.getString(requireContext(),R.string.chooseLevel))
        builder.setPositiveButton(ContextCompat.getString(requireContext(),R.string.setPositiveButton)) { _, _ ->
           showCustomDialog(viewModel.getLevelList(),false)
        }

        builder.setNegativeButton(ContextCompat.getString(requireContext(),R.string.setNegativeButton)) { _, _ ->
            val lista = viewModel.getLevelList()
            if (lista.isNotEmpty()) {
                val serie = lista[random.nextInt(lista.size)]
                val bundle = Bundle().apply {
                    putSerializable("serie", serie)
                    putBoolean("local", true)
                }

                findNavController().navigate(R.id.action_localGameFragment_to_dailyFragment, bundle, navOptions)
            } else {
                showSnackbar(requireView(), ContextCompat.getString(requireContext(),R.string.introduceLevel))
            }
        }
        builder.show()
    }
}