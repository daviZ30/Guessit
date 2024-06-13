package com.dezeta.guessit.ui.menu

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.Glide
import com.dezeta.guessit.R
import com.dezeta.guessit.databinding.FragmentDailyMenuBinding
import com.dezeta.guessit.utils.CloudStorageManager
import com.dezeta.guessit.utils.Locator
import com.dezeta.guessit.utils.MyWorker
import ir.mahozad.android.PieChart
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


class DailyMenuFragment : Fragment() {
    private lateinit var workManager: WorkManager
    private var _binding: FragmentDailyMenuBinding? = null
    private lateinit var editPreferences: SharedPreferences.Editor
    private var isDarkThemeOn by Delegates.notNull<Boolean>()
    private val binding get() = _binding!!

    private val viewModel: ViewModelMenu by viewModels()
    private lateinit var manager: CloudStorageManager

    override fun onStart() {
        super.onStart()
        Locator.managerFragment = requireActivity().supportFragmentManager
        viewModel.getAllUserAccounts()
        viewModel.loadUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDailyMenuBinding.inflate(inflater, container, false)
        workManager = WorkManager.getInstance(requireActivity().applicationContext)
        binding.viewmodel = this.viewModel
        manager = CloudStorageManager()
        binding.lifecycleOwner = this
        editPreferences =
            requireActivity().getSharedPreferences(
                getString(R.string.prefs_file),
                Context.MODE_PRIVATE
            ).edit()

        return binding.root
    }


    override fun onStop() {
        super.onStop()
        Locator.managerFragment = null
        binding.lottieLoadAnimationMenu.cancelAnimation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        binding.btnDailyCountry.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("serie", viewModel.getCountry())
                putBoolean("local", false)
            }
            binding.btnDailyCountry.isEnabled = false
            viewModel.loadUserAndSave("country")

            val request = OneTimeWorkRequestBuilder<MyWorker>()
                .setInputData(
                    workDataOf(
                        "btnType" to "country"
                    )
                )
                .setConstraints(constraints)
                //.setInitialDelay(calculateDelayToMidnight(), TimeUnit.MILLISECONDS)
                .setInitialDelay(10, TimeUnit.SECONDS)
                .build()

            workManager.enqueue(request)


            findNavController().navigate(R.id.action_categoryFragment_to_dailyFragment, bundle)

        }
        binding.btnDailyGame.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("serie", viewModel.getSerie())
                putBoolean("local", false)
            }
            binding.btnDailyGame.isEnabled = false
            viewModel.loadUserAndSave("serie")
            val request = OneTimeWorkRequestBuilder<MyWorker>()
                .setInputData(
                    workDataOf(
                        "btnType" to "serie"
                    )
                )
                .setConstraints(constraints)
                .setInitialDelay(10, TimeUnit.SECONDS)
                .build()

            workManager.enqueue(request)

            findNavController().navigate(R.id.action_categoryFragment_to_dailyFragment, bundle)

        }
        binding.btnDailyFootball.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("serie", viewModel.getPlayer())
                putBoolean("local", false)
            }
            binding.btnDailyFootball.isEnabled = false
            viewModel.loadUserAndSave("football")
            val request = OneTimeWorkRequestBuilder<MyWorker>()
                .setInputData(
                    workDataOf(
                        "btnType" to "football"
                    )
                )
                .setConstraints(constraints)
                .setInitialDelay(10, TimeUnit.SECONDS)
                .build()

            workManager.enqueue(request)
            findNavController().navigate(R.id.action_categoryFragment_to_dailyFragment, bundle)
        }
        viewModel.getState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is ExtraState.refreshUserList -> {

                    refreshUserList()
                }

                is ExtraState.Country24 -> {
                    binding.btnDailyCountry.isEnabled = true
                }

                is ExtraState.refreshUserProfile -> {
                    Glide.with(binding.root)
                        .load(state.url)
                        .into(state.view)
                }

                is ExtraState.UserSuccess -> {
                    binding.pieChart.visibility = View.VISIBLE
                    setup()
                    binding.btnDailyCountry.isEnabled = viewModel.user!!.countryEnable
                    binding.btnDailyGame.isEnabled = viewModel.user!!.serieEnable
                    binding.btnDailyFootball.isEnabled = viewModel.user!!.footballEnable
                }
            }
        }
    }

    private fun setup() {
        val total =
            viewModel.user!!.statCountry + viewModel.user!!.statFootball + viewModel.user!!.statSerie
        val countryFloat: Float = viewModel.user!!.statCountry.toFloat() / total
        val footballFloat: Float = viewModel.user!!.statFootball.toFloat() / total
        val serieFloat: Float = viewModel.user!!.statSerie.toFloat() / total

        if (countryFloat.isNaN() && footballFloat.isNaN() && serieFloat.isNaN()) {
            binding.pieChart.visibility = View.INVISIBLE
            binding.layoutLegen.visibility = View.INVISIBLE
            with(binding.lottieLoadAnimationMenu) {
                visibility = View.VISIBLE
                setAnimation(R.raw.no_data)
                repeatCount = LottieDrawable.INFINITE
                playAnimation()
            }
        } else {
            binding.lottieLoadAnimationMenu.visibility = View.INVISIBLE
            binding.pieChart.visibility = View.VISIBLE
            binding.layoutLegen.visibility = View.VISIBLE
            val slices = listOf(
                PieChart.Slice(
                    countryFloat,
                    ContextCompat.getColor(requireContext(), R.color.BlueRange3),
                    label = viewModel.user!!.statCountry.toString()
                ), // Pais
                PieChart.Slice(
                    footballFloat,
                    ContextCompat.getColor(requireContext(), R.color.BlueRange4),
                    label = viewModel.user!!.statFootball.toString()
                ), // Jugador
                PieChart.Slice(
                    serieFloat,
                    ContextCompat.getColor(requireContext(), R.color.BlueRange1),
                    label = viewModel.user!!.statSerie.toString()
                ) // Serie
            )

            binding.pieChart.slices = slices.filter { it.fraction != 0f }

        }
    }

    private fun refreshUserList() {
        viewModel.userList.sortByDescending { it.point }

        when (viewModel.userList.size) {
            1 -> {
                val user = viewModel.userList[0]

                with(binding) {
                    viewModel.getUserProfileImageByEmail(manager, user.email, imgProfile1)
                    tvProfile1.text = user.name
                    tvProfile1Points.text = user.point.toString()
                }
            }

            2 -> {
                with(binding) {
                    viewModel.getUserProfileImageByEmail(
                        manager,
                        viewModel.userList[0].email,
                        imgProfile1
                    )
                    viewModel.getUserProfileImageByEmail(
                        manager,
                        viewModel.userList[1].email,
                        imgProfile2
                    )
                    tvProfile1.text = viewModel.userList[0].name
                    tvProfile1Points.text = viewModel.userList[0].point.toString()

                    tvProfile2.text = viewModel.userList[1].name
                    tvProfile2Points.text = viewModel.userList[1].point.toString()
                }
            }

            in 3..Int.MAX_VALUE -> {
                with(binding) {
                    viewModel.getUserProfileImageByEmail(
                        manager,
                        viewModel.userList[0].email,
                        imgProfile1
                    )
                    viewModel.getUserProfileImageByEmail(
                        manager,
                        viewModel.userList[1].email,
                        imgProfile2
                    )
                    viewModel.getUserProfileImageByEmail(
                        manager,
                        viewModel.userList[2].email,
                        imgProfile3
                    )
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