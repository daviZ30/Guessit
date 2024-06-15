package com.dezeta.guessit.ui.level

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dezeta.guessit.R
import com.dezeta.guessit.databinding.FragmentNivelesBinding
import com.dezeta.guessit.showSnackbar
import com.github.nikartm.button.FitButton
import kotlin.properties.Delegates

class LevelFragment : Fragment() {
    var btnList = mutableListOf<FitButton>()
    private var _binding: FragmentNivelesBinding? = null
    private val viewModel: ViewModelLevel by viewModels()

    private var isDarkThemeOn by Delegates.notNull<Boolean>()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNivelesBinding.inflate(inflater, container, false)
        isDarkThemeOn =
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        btnList = mutableListOf()
        for (i in 0 until binding.table.childCount) {
            val row: TableRow = binding.table.getChildAt(i) as TableRow
            for (j in 0 until row.childCount) {
                val btn: View = row.getChildAt(j)
                if (btn is FitButton) {
                    btnList.add(btn)
                    btn.setOnClickListener(MiClickListener())
                    if (isDarkThemeOn)
                        btn.setButtonColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white_transparente
                            )
                        )
                }
            }
        }
        viewModel.loadUser()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is LevelState.Success<*> -> {
                    setup()
                }
            }
        }
    }

    private fun setup() {
        for (i in 0 until viewModel.user.completeLevel) {
            CompleteLevel(btnList[i])
        }
        if (viewModel.user.level > viewModel.user.completeLevel && viewModel.user.completeLevel < 24) {
            btnList[viewModel.user.completeLevel].isEnabled = true
            btnList[viewModel.user.completeLevel].setButtonColor(Color.parseColor("#142B3B"))
        } else if (viewModel.user.completeLevel < 24) {
            btnList[viewModel.user.completeLevel].isEnabled = true
            btnList[viewModel.user.completeLevel].setButtonColor(Color.parseColor("#44B4C4"))
        }

    }

    inner class MiClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            val botonId = view.id
            when (botonId) {
                R.id.btnLevel1 -> {
                    if (viewModel.user.level + 1 == 1) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.levelSerie
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("online14"))
                        putBoolean("local", false)
                        putInt("level", 1)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel2 -> {
                    if (viewModel.user.level + 1 == 2) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.levelSerie
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("online2"))
                        putBoolean("local", false)
                        putInt("level", 2)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel3 -> {
                    if (viewModel.user.level + 1 == 3) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.LevelTest
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("guess0", viewModel.getGuess("test11"))
                        putSerializable("guess1", viewModel.getGuess("test17"))
                        putSerializable("guess2", viewModel.getGuess("test21"))
                        putInt("level", 3)
                        putBoolean("local", false)
                    }
                    findNavController().navigate(
                        R.id.action_LevelFragment_to_testFragment,
                        bundle
                    )
                }

                R.id.btnLevel4 -> {
                    if (viewModel.user.level + 1 == 4) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.LevelTest
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("guess0", viewModel.getGuess("test25"))
                        putSerializable("guess1", viewModel.getGuess("test5"))
                        putSerializable("guess2", viewModel.getGuess("test22"))
                        putInt("level", 4)
                        putBoolean("local", false)
                    }
                    findNavController().navigate(
                        R.id.action_LevelFragment_to_testFragment,
                        bundle
                    )
                }

                R.id.btnLevel5 -> {
                    if (viewModel.user.level + 1 == 5) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.levelCountry
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("country7"))
                        putBoolean("local", false)
                        putInt("level", 5)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel6 -> {
                    if (viewModel.user.level + 1 == 6) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.levelCountry
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("country17"))
                        putBoolean("local", false)
                        putInt("level", 6)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel7 -> {
                    if (viewModel.user.level + 1 == 7) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.levelPlayer
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("player1"))
                        putBoolean("local", false)
                        putInt("level", 7)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel8 -> {
                    if (viewModel.user.level + 1 == 8) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.level10Duel
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putInt("score", 10)
                        putInt("level", 8)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_duelFragment, bundle)
                }

                R.id.btnLevel9 -> {
                    if (viewModel.user.level + 1 == 9) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.levelPlayer
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("player17"))
                        putBoolean("local", false)
                        putInt("level", 9)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel10 -> {
                    if (viewModel.user.level + 1 == 10) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.levelSerie
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("online29"))
                        putBoolean("local", false)
                        putInt("level", 10)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel11 -> {
                    if (viewModel.user.level + 1 == 11) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.levelSerie
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("online27"))
                        putBoolean("local", false)
                        putInt("level", 11)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel12 -> {
                    if (viewModel.user.level + 1 == 12) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.levelCountry
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("country14"))
                        putBoolean("local", false)
                        putInt("level", 12)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel13 -> {
                    if (viewModel.user.level + 1 == 13) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.level10Duel
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putInt("score", 10)
                        putInt("level", 13)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_duelFragment, bundle)
                }

                R.id.btnLevel14 -> {
                    if (viewModel.user.level + 1 == 14) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.LevelTest
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("guess0", viewModel.getGuess("test12"))
                        putSerializable("guess1", viewModel.getGuess("test5"))
                        putSerializable("guess2", viewModel.getGuess("test22"))
                        putInt("level", 14)
                        putBoolean("local", false)
                    }
                    findNavController().navigate(
                        R.id.action_LevelFragment_to_testFragment,
                        bundle
                    )
                }

                R.id.btnLevel15 -> {
                    if (viewModel.user.level + 1 == 15) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.levelPlayer
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("player6"))
                        putBoolean("local", false)
                        putInt("level", 15)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel16 -> {
                    if (viewModel.user.level + 1 == 16) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.levelCountry
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("country16"))
                        putBoolean("local", false)
                        putInt("level", 16)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel17 -> {
                    if (viewModel.user.level + 1 == 17) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.levelSerie
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("online31"))
                        putBoolean("local", false)
                        putInt("level", 17)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel18 -> {
                    if (viewModel.user.level + 1 == 18) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.levelSerie
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("online20"))
                        putBoolean("local", false)
                        putInt("level", 18)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel19 -> {
                    if (viewModel.user.level + 1 == 19) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.levelPlayer
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("player21"))
                        putBoolean("local", false)
                        putInt("level", 19)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel20 -> {
                    if (viewModel.user.level + 1 == 20) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.level20Duel
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putInt("score", 20)
                        putInt("level", 20)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_duelFragment, bundle)
                }

                R.id.btnLevel21 -> {
                    if (viewModel.user.level + 1 == 21) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.levelPlayer
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("player15"))
                        putBoolean("local", false)
                        putInt("level", 21)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel22 -> {
                    if (viewModel.user.level + 1 == 22) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.levelSerie
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("online28"))
                        putBoolean("local", false)
                        putInt("level", 22)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }

                R.id.btnLevel23 -> {
                    if (viewModel.user.level + 1 == 23) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.levelSerie
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("online9"))
                        putBoolean("local", false)
                        putInt("level", 23)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)

                }

                R.id.btnLevel24 -> {
                    if (viewModel.user.level + 1 == 24) {
                        showConfirmationDialog(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.levelPlayer
                            )
                        )
                        return
                    }
                    val bundle = Bundle().apply {
                        putSerializable("serie", viewModel.getGuess("player9"))
                        putBoolean("local", false)
                        putInt("level", 24)
                    }
                    findNavController().navigate(R.id.action_LevelFragment_to_dailyFragment, bundle)
                }
            }
        }
    }

    private fun showConfirmationDialog(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(ContextCompat.getString(requireContext(), R.string.payLevel))
        builder.setMessage(message)
        builder.setPositiveButton(
            ContextCompat.getString(
                requireContext(),
                R.string.accept
            )
        ) { _, _ ->
            if (viewModel.user.point >= 25) {
                viewModel.user.point -= 25
                viewModel.updateLevel()
                viewModel.loadUser()
            } else {
                showSnackbar(
                    requireView(),
                    ContextCompat.getString(requireContext(), R.string.notPayLevel)
                )
            }
        }

        builder.setNegativeButton(
            ContextCompat.getString(
                requireContext(),
                R.string.cancel
            )
        ) { _, _ -> }
        builder.show()
    }


    private fun CompleteLevel(btn: FitButton) {
        btn.isEnabled = true
        btn.setButtonColor(Color.parseColor("#FF39A121"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}