package com.dezeta.guessit.ui.level

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dezeta.guessit.R
import com.dezeta.guessit.databinding.FragmentNivelesBinding
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.showSnackbar
import com.github.nikartm.button.FitButton

class LevelFragment : Fragment() {
    var btnList = mutableListOf<FitButton>()
    private var _binding: FragmentNivelesBinding? = null
    private val viewModel: ViewModelLevel by viewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNivelesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadUser()
        btnList = mutableListOf()
        for (i in 0 until binding.table.childCount) {
            val row: TableRow = binding.table.getChildAt(i) as TableRow
            for (j in 0 until row.childCount) {
                val btn: View = row.getChildAt(j)
                if (btn is FitButton) {
                    btnList.add(btn)
                    btn.setOnClickListener(MiClickListener())

                }
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is LevelState.Success<*> -> {
                    setup((state.data as User).level)

                }
            }
        }

    }

    private fun setup(levels: Int) {
        for (i in 0 until levels) {
            LevelUnlocked(btnList[i])
        }
        if (levels <= btnList.size - 1) {
            btnList[levels].isEnabled = true
            btnList[levels].setButtonColor(Color.parseColor("#44B4C4"))
        }

    }

    inner class MiClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            val botonId = view.id
            when (botonId) {

                R.id.btnLevel1 -> {
                    if (viewModel.user.level + 1 == 1) {
                        showConfirmationDialog("Tendrás que adivinar una serie")
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("online14"))
                        putBoolean("local", false)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel2 -> {
                    if (viewModel.user.level + 1 == 2) {
                        showConfirmationDialog("Tendrás que adivinar una serie")
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("online2"))
                        putBoolean("local", false)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel3 -> {
                    if (viewModel.user.level + 1 == 3) {
                        showConfirmationDialog("Tendrás que adivinar tres cuestionarios")
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("guess0", viewModel.getGuess("test11"))
                        putSerializable("guess1", viewModel.getGuess("test17"))
                        putSerializable("guess2", viewModel.getGuess("test21"))

                        putBoolean("local", false)
                    }
                    findNavController().navigate(
                        R.id.action_LevelFragment_to_testFragment,
                        bundle
                    )
                }

                R.id.btnLevel4 -> {
                    if (viewModel.user.level + 1 == 4) {
                        showConfirmationDialog("Tendrás que adivinar tres cuestionarios")
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("guess0", viewModel.getGuess("test25"))
                        putSerializable("guess1", viewModel.getGuess("test5"))
                        putSerializable("guess2", viewModel.getGuess("test22"))

                        putBoolean("local", false)
                    }
                    findNavController().navigate(
                        R.id.action_LevelFragment_to_testFragment,
                        bundle
                    )
                }

                R.id.btnLevel5 -> {
                    if (viewModel.user.level + 1 == 5) {
                        showConfirmationDialog("Tendrás que adivinar un pais")
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("country7"))
                        putBoolean("local", false)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel6 -> {
                    if (viewModel.user.level + 1 == 6) {
                        showConfirmationDialog("Tendrás que adivinar un pais")
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("country17"))
                        putBoolean("local", false)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel7 -> {
                    if (viewModel.user.level + 1 == 7) {
                        showConfirmationDialog("Tendrás que adivinar un futbolista")
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("player1"))
                        putBoolean("local", false)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel8 -> {
                    if (viewModel.user.level + 1 == 8) {
                        showConfirmationDialog("Tendrás que realizar una racha de 10 en Duelo")
                        return
                    }
                    val bundle = Bundle().apply {
                        putInt("score",10)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_duelFragment, bundle)
                }

                R.id.btnLevel9 -> {
                    if (viewModel.user.level + 1 == 9) {
                        showConfirmationDialog("Tendrás que adivinar un futbolista")
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("player17"))
                        putBoolean("local", false)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel10 -> {
                    if (viewModel.user.level + 1 == 10) {
                        showConfirmationDialog("Tendrás que adivinar una serie")
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("online29"))
                        putBoolean("local", false)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel11 -> {
                    if (viewModel.user.level + 1 == 11) {
                        showConfirmationDialog("Tendrás que adivinar una serie")
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("online27"))
                        putBoolean("local", false)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel12 -> {
                    if (viewModel.user.level + 1 == 12) {
                        showConfirmationDialog("Tendrás que adivinar un pais")
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("country14"))
                        putBoolean("local", false)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel13 -> {
                    if (viewModel.user.level + 1 == 13) {
                        showConfirmationDialog("Tendrás que hacer una racha de 10 en Duelo")
                        return
                    }
                    val bundle = Bundle().apply {
                        putInt("score",10)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_duelFragment, bundle)
                }

                R.id.btnLevel14 -> {
                    if (viewModel.user.level + 1 == 14) {
                        showConfirmationDialog("Tendrás que adivinar tres test")
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("guess0", viewModel.getGuess("test12"))
                        putSerializable("guess1", viewModel.getGuess("test5"))
                        putSerializable("guess2", viewModel.getGuess("test22"))

                        putBoolean("local", false)
                    }
                    findNavController().navigate(
                        R.id.action_LevelFragment_to_testFragment,
                        bundle
                    )
                }

                R.id.btnLevel15 -> {
                    if (viewModel.user.level + 1 == 15) {
                        showConfirmationDialog("Tendrás que adivinar un futbolista")
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("player6"))
                        putBoolean("local", false)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel16 -> {
                    if (viewModel.user.level + 1 == 16) {
                        showConfirmationDialog("Tendrás que adivinar un pais")
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("country16"))
                        putBoolean("local", false)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel17 -> {
                    if (viewModel.user.level + 1 == 17) {
                        showConfirmationDialog("Tendrás que adivinar una serie")
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("online31"))
                        putBoolean("local", false)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel18 -> {
                    if (viewModel.user.level + 1 == 18) {
                        showConfirmationDialog("Tendrás que adivinar una serie")
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("online20"))
                        putBoolean("local", false)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel19 -> {
                    if (viewModel.user.level + 1 == 19) {
                        showConfirmationDialog("Tendrás que adivinar un futbolista")
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("player21"))
                        putBoolean("local", false)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel20 -> {
                    if (viewModel.user.level + 1 == 20) {
                        showConfirmationDialog("Tendrás que hacer una racha de 20 en Duelo")
                        return
                    }
                    val bundle = Bundle().apply {
                        putInt("score",20)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_duelFragment, bundle)
                }

                R.id.btnLevel21 -> {
                    if (viewModel.user.level + 1 == 21) {
                        showConfirmationDialog("Tendrás que adivinar un futbolista")
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("player15"))
                        putBoolean("local", false)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel22 -> {
                    if (viewModel.user.level + 1 == 22) {
                        showConfirmationDialog("Tendrás que adivinar una serie")
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("online28"))
                        putBoolean("local", false)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel23 -> {
                    if (viewModel.user.level + 1 == 23) {
                        showConfirmationDialog("Tendrás que adivinar una serie")
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("online9"))
                        putBoolean("local", false)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)

                }

                R.id.btnLevel24 -> {
                    if (viewModel.user.level + 1 == 24) {
                        showConfirmationDialog("Tendrás que adivinar un futbolista")
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("player9"))
                        putBoolean("local", false)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }
            }
        }
    }

    private fun showConfirmationDialog(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("¿Desea gastar 25 puntos para desbloquear el nivel?")
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar") { _, _ ->
            if (viewModel.user.point >= 25) {
                viewModel.user.point -= 25
                viewModel.updateLevel()
                viewModel.loadUser()
            } else {
                showSnackbar(
                    requireView(),
                    "No dispones de 25 puntos o más para desbloquear el nivel"
                )
            }
        }

        builder.setNegativeButton("Cancelar") { _, _ ->

        }
        builder.show()
    }

    private fun LevelUnlocked(btn: FitButton) {
        println("Nivel Desbloqueado")
        btn.isEnabled = true
        btn.setButtonColor(Color.parseColor("#142B3B"))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}