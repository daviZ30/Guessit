package com.dezeta.guessit.ui.menu

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dezeta.guessit.R
import com.dezeta.guessit.databinding.FragmentMenuBinding
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.ui.main.MainActivity
import com.dezeta.guessit.utils.Locator
import com.dezeta.guessit.utils.NetworkConnection
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    var testNum = 0
    lateinit var testlist: List<Guess>
    private val binding get() = _binding!!
    var offlineMode = false
    private val viewModel: ViewModelMenu by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // findNavController().navigate(R.id.loginFragment)
        val preferences = requireActivity().getSharedPreferences(
            getString(R.string.prefs_file),
            Context.MODE_PRIVATE
        )
        offlineMode = preferences.getBoolean(Locator.OFFLINE_MODE, false)

        if (!offlineMode) {
            testlist = viewModel.getTestList().shuffled()
        }
    }

    override fun onStart() {
        super.onStart()
        if (isOnline() && !offlineMode)
            (requireActivity() as MainActivity).updateHeader()
    }

    private fun resetOnline(online: Boolean) {
        try {
            val fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
            with(binding) {
                cvDaily.startAnimation(fadeInAnimation)
                cvTest.startAnimation(fadeInAnimation)
                cvLevels.startAnimation(fadeInAnimation)
                cvDuel.startAnimation(fadeInAnimation)
                fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationRepeat(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        cvDaily.visibility = View.VISIBLE
                        cvTest.visibility = View.VISIBLE
                        cvLevels.visibility = View.VISIBLE
                        cvDuel.visibility = View.VISIBLE
                    }
                })
            }
            if (!online) {
                with(binding) {
                    tvMenuMessage.visibility = View.VISIBLE
                    if (offlineMode)
                        tvMenuMessage.text =
                            ContextCompat.getString(requireContext(), R.string.tvMessageOfflineMode)
                    else
                        tvMenuMessage.text =
                            ContextCompat.getString(requireContext(), R.string.tvMessageOfline)
                    cvDaily.isEnabled = false
                    cvTest.isEnabled = false
                    cvLevels.isEnabled = false
                    cvDuel.isEnabled = false
                    cvDaily.setCardBackgroundColor(Color.GRAY)
                    cvTest.setCardBackgroundColor(Color.GRAY)
                    cvLevels.setCardBackgroundColor(Color.GRAY)
                    cvDuel.setCardBackgroundColor(Color.GRAY)
                }
            } else {
                with(binding) {
                    tvMenuMessage.visibility = View.INVISIBLE
                    cvDaily.isEnabled = true
                    cvTest.isEnabled = true
                    cvLevels.isEnabled = true
                    cvDuel.isEnabled = true
                    cvDaily.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.BlueRange1_2
                        )
                    )
                    cvTest.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.BlueRange1_4
                        )
                    )
                    cvLevels.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.BlueRange3
                        )
                    )
                    cvDuel.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.BlueRange4
                        )
                    )
                }
            }
        } catch (e: Exception) {
            findNavController().navigate(R.id.MenuFragment)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (offlineMode) {
            resetOnline(false)
        }
        val navOptions = NavOptions.Builder()
            .setEnterAnim(android.R.anim.fade_in)
            .setExitAnim(android.R.anim.fade_out)
            .setPopEnterAnim(android.R.anim.slide_in_left)
            .setPopExitAnim(android.R.anim.slide_out_right)
            .build()

        GlobalScope.launch {
            NetworkConnection.isConnected.collect {
                withContext(Dispatchers.Main) {
                    if (!offlineMode) {
                        if (it)
                            (requireActivity() as MainActivity).updateHeader()
                        else
                            (requireActivity() as MainActivity).setOfflineHeader()
                        resetOnline(it)
                    }
                }
            }
        }
        binding.cvLevels.setOnClickListener {
            findNavController().navigate(
                R.id.action_FirstFragment_to_SecondFragment
            )
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

            findNavController().navigate(
                R.id.action_MenuFragment_to_testFragment,
                bundle, navOptions
            )
        }
        binding.cvDaily.setOnClickListener {
            findNavController().navigate(
                R.id.action_MenuFragment_to_categoryFragment
            )
        }
        binding.cvDuel.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_duelFragment,
                null,
                navOptions)
        }
        binding.cvLocalGame.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_localGameFragment,
                null,
                navOptions)
        }
        binding.btnFriend.setOnClickListener {
            if (isOnline() && !offlineMode)
                findNavController().navigate(R.id.action_MenuFragment_to_friendFragment,
                    null,
                    navOptions)
        }
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_MenuFragment_to_settingsFragment,
                null,
                navOptions)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun isOnline(): Boolean {
        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            ) {
                return true
            }
        }
        return false
    }
}