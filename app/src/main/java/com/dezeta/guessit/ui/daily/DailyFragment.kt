package com.dezeta.guessit.ui.daily

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toLowerCase
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dezeta.guessit.R
import com.dezeta.guessit.adapter.CategoryAdapter
import com.dezeta.guessit.adapter.SearchAdapter
import com.dezeta.guessit.databinding.FragmentDailyBinding
import com.dezeta.guessit.domain.entity.Img
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.domain.entity.GuessType
import com.dezeta.guessit.loadImageBitmapFromInternalStorage
import com.google.android.material.navigation.NavigationView
import java.util.Locale
import kotlin.properties.Delegates

class DailyFragment : Fragment() {
    private lateinit var preferences: SharedPreferences.Editor
    var listRed = mutableListOf<String>()
    var listGreen = mutableListOf<String>()
    var level: Int? = null
    var NumImage = 1
    var LastImage = 1
    var img: Img? = null
    private var isDarkThemeOn by Delegates.notNull<Boolean>()

    private var images: List<Img>? = null

    private lateinit var adapterList: SearchAdapter

    private val viewModel: ViewModelDaily by viewModels()

    private var _binding: FragmentDailyBinding? = null
    private val binding get() = _binding!!

    private fun getImage(n: Int): String? {
        images?.forEach {
            println(it)
            if (it.order == n)
                img = it
        }
        return img?.img_url
    }

    private fun refreshHeader() {
        val navigationView = requireActivity().findViewById<View>(R.id.nav_view) as NavigationView
        val headerView: View = navigationView.getHeaderView(0)

        val tvDrawerPoint = headerView.findViewById<TextView>(R.id.navTvPoint)
        val p: Int = tvDrawerPoint.text.toString().toInt() + viewModel.point
        tvDrawerPoint.text = p.toString()

    }

    override fun onStart() {
        super.onStart()
        viewModel.loadUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyBinding.inflate(inflater, container, false)
        preferences =
            requireActivity().getSharedPreferences(
                getString(R.string.prefs_file),
                Context.MODE_PRIVATE
            ).edit()

        isDarkThemeOn =
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        arguments.let {
            if (it != null) {
                level = it.getInt("level")
                viewModel.guess = it.getSerializable("serie") as Guess
                if (viewModel.guess!!.guessType == GuessType.COUNTRY || viewModel.guess!!.guessType == GuessType.FOOTBALL) {
                    binding.btnCategoty.visibility = View.GONE
                }
                with(viewModel) {
                    images = getImages(viewModel.guess!!)
                    local = it.getBoolean("local")
                    if (local) {
                        help = false
                        with(binding) {
                            tvDailyHelp.text =
                                ContextCompat.getString(requireContext(), R.string.helpLocal)
                            btnCategoty.visibility = View.GONE
                            cvShowList.visibility = View.GONE
                        }
                        binding.imgDaily1.setImageBitmap(
                            loadImageBitmapFromInternalStorage(
                                getImage(
                                    1
                                )!!
                            )
                        )
                        binding.imgDaily2.setImageBitmap(
                            loadImageBitmapFromInternalStorage(
                                getImage(
                                    2
                                )!!
                            )
                        )
                        binding.imgDaily3.setImageBitmap(
                            loadImageBitmapFromInternalStorage(
                                getImage(
                                    3
                                )!!
                            )
                        )

                    } else {
                        binding.tvDailyHelp.text =
                            ContextCompat.getString(requireContext(), R.string.helpOnline)
                        help = true
                        for (i in 1..3) {
                            loadImage(i)
                        }
                    }
                }
            }
        }
        return binding.root
    }

    private fun loadImage(i: Int) {
        var img = ""

        val view = when (i) {
            1 -> {
                img = getImage(i)!!
                binding.imgDaily1
            }

            2 -> {
                img = getImage(i)!!
                binding.imgDaily2
            }

            3 -> {
                img = getImage(i)!!
                binding.imgDaily3
            }

            else -> null
        }

        with(binding.lottieLoadAnimation) {
            visibility = View.VISIBLE
            setAnimation(R.raw.load_image)
            playAnimation()
        }
        if (i == 3) {
            Glide.with(requireContext())
                .load(img)
                .timeout(30000)
                .override(720, 480)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        // loadImage(3)
                        binding.lottieLoadAnimation.visibility = View.INVISIBLE
                        binding.lottieLoadAnimation.cancelAnimation()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        view!!.setImageDrawable(resource)
                        binding.lottieLoadAnimation.visibility = View.INVISIBLE
                        binding.lottieLoadAnimation.cancelAnimation()
                        return true
                    }

                })
                .into(view!!)

        } else {
            Glide.with(requireContext())
                .load(img)
                .timeout(30000)
                .override(720, 480)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        view!!.setImageDrawable(resource)
                        return true
                    }
                })
                .into(view!!)
        }
    }


    private fun nextImage() {
        when (NumImage) {
            1 -> {
                binding.imgDaily1.visibility = View.VISIBLE
                binding.imgDaily2.visibility = View.INVISIBLE
                binding.imgDaily3.visibility = View.INVISIBLE
            }

            2 -> {
                binding.imgDaily1.visibility = View.INVISIBLE
                binding.imgDaily2.visibility = View.VISIBLE
                binding.imgDaily3.visibility = View.INVISIBLE
            }

            3 -> {
                binding.imgDaily1.visibility = View.INVISIBLE
                binding.imgDaily2.visibility = View.INVISIBLE
                binding.imgDaily3.visibility = View.VISIBLE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        setupTheme()
        viewModel.getState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is DailyState.Success -> {
                    findNavController().popBackStack()
                }

                else -> {}
            }
        }
        adapterList = SearchAdapter() {
            binding.tieSearch.setText(it)
        }

        binding.btnCategoty.setOnClickListener {
            showCustomDialog()
        }
        binding.swShowList.setOnClickListener {
            showConfirmationDialog()
        }

        binding.btnHelp.setOnClickListener {
            showHelpMessage()
            binding.btnHelp.visibility = View.GONE
        }

        binding.tieSearch.addTextChangedListener(
            TextWatcher(
                binding.tieSearch.text.toString()
            )
        )
        with(binding.rvSearch) {
            adapter = adapterList
            layoutManager = LinearLayoutManager(requireContext())
        }
        binding.btnGuessDaily.setOnClickListener {
            var guess = binding.tieSearch.text.toString().trim().uppercase(Locale.ROOT)
            if (viewModel.guess!!.guessType == GuessType.COUNTRY && viewModel.getLanguage() == "en") {
                guess = viewModel.translateToSpanish[guess].toString()
            }
            if (guess.uppercase(Locale.ROOT)
                == viewModel.guess!!.name.uppercase(Locale.ROOT)
            ) {
                if (!viewModel.local && level == 0) {
                    viewModel.updatePoint()
                    showCongratulatoryMessage()
                    refreshHeader()
                    findNavController().popBackStack()
                } else if (!viewModel.local && level != 0) {
                    if (level != 24) {
                        showLevelMessage()
                        viewModel.updateLevel(level!!)
                    } else {
                        viewModel.update2500Point()
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.Congratulation
                            )
                        )
                        builder.setMessage(
                            ContextCompat.getString(
                                requireContext(),
                                R.string.point2500
                            )
                        )
                        builder.setPositiveButton("Ok") { _, _ -> }
                        builder.show()
                    }
                } else {
                    showCongratulatoryMessage()
                    findNavController().popBackStack()
                }

            } else {
                showError()
                val newSerie =
                    viewModel.getSerieFromName(binding.tieSearch.text.toString().trim())
                when {
                    newSerie == null -> {}
                    newSerie.category == viewModel.guess!!.category -> {
                        listGreen.add(newSerie.category.toString())
                    }

                    else -> {
                        listRed.add(newSerie.category.toString())
                    }
                }
            }

        }
        binding.btnNext.setOnClickListener {
            binding.btnPrevious.isEnabled = true
            if (NumImage == 2) {
                binding.btnNext.isEnabled = false
            }
            if (NumImage < 3) {
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                if (NumImage == LastImage) {
                    showError()
                }
                NumImage++
                if (NumImage > LastImage) {
                    LastImage = NumImage
                }
                nextImage()
            }
        }
        binding.btnPrevious.setOnClickListener {
            binding.btnNext.isEnabled = true
            if (NumImage == 2) {
                binding.btnPrevious.isEnabled = false
            }
            if (NumImage > 1) {
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                NumImage--
                nextImage()
            }
        }
    }

    private fun setupTheme() {
        if (isDarkThemeOn) {
            binding.imgError1.setColorFilter(Color.WHITE)
            binding.imgError2.setColorFilter(Color.WHITE)
            binding.imgError3.setColorFilter(Color.WHITE)
            binding.imgError4.setColorFilter(Color.WHITE)
            binding.imgError5.setColorFilter(Color.WHITE)

        }
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(
            ContextCompat.getString(
                requireContext(),
                R.string.ConfirmationDailyDialog
            )
        )
        builder.setPositiveButton(
            ContextCompat.getString(
                requireContext(),
                R.string.setPositiveButton
            )
        ) { _, _ ->
            showError()
            when (viewModel.guess?.guessType) {
                GuessType.COUNTRY -> {
                    adapterList.update(viewModel.getCountryNameList())
                }

                GuessType.FOOTBALL -> {
                    adapterList.update(viewModel.getPlayerNameList().toMutableList())
                }

                GuessType.SERIE -> {

                }

                else -> {
                    adapterList.update(viewModel.getSerieList().map { it.name }.toMutableList())
                }
            }
            binding.swShowList.isEnabled = false
        }

        builder.setNegativeButton(
            ContextCompat.getString(
                requireContext(),
                R.string.setNegativeButton
            )
        ) { _, _ ->
            binding.swShowList.isChecked = false
        }
        builder.show()
    }

    private fun showCustomDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_category)

        val rvCategoryList = dialog.findViewById(R.id.rvDialogCategory) as RecyclerView
        val btnClose = dialog.findViewById(R.id.btnOk) as Button
        val imgCategoryRed = dialog.findViewById(R.id.imgDialogCategoryRed) as ImageView
        val imgCategoryGreen = dialog.findViewById(R.id.imgDialogCategoryGreen) as ImageView

        imgCategoryRed.setOnClickListener {
            showHelpRedMessage()
        }
        imgCategoryGreen.setOnClickListener {
            showHelpGreenMessage()
        }

        val categoryAdapter = CategoryAdapter(listRed, listGreen)
        categoryAdapter.update(viewModel.categoryList().toMutableList())
        rvCategoryList.adapter = categoryAdapter

        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showCongratulatoryMessage() {
        var name = viewModel.guess!!.name
        if (viewModel.guess!!.guessType == GuessType.COUNTRY && viewModel.getLanguage() == "en") {
            name = viewModel.translateToEnglish[name.uppercase(Locale.ROOT)].toString()
        }
        val mesage: String = if (viewModel.local)
            ContextCompat.getString(
                requireContext(),
                R.string.CompleteLevel
            ) + " " + { name }
        else
            "${
                ContextCompat.getString(
                    requireContext(),
                    R.string.CompleteLevel
                )
            } ${name}. ${
                ContextCompat.getString(
                    requireContext(),
                    R.string.preDailyPoint
                )
            } ${viewModel.point} ${
                ContextCompat.getString(
                    requireContext(),
                    R.string.postDailyPoint
                )
            }"
        val builder = AlertDialog.Builder(context)
        builder.setTitle(ContextCompat.getString(requireContext(), R.string.Congratulation))
        builder.setMessage(mesage)
        builder.setPositiveButton("Ok") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showLevelMessage() {
        val mesage = "${
            ContextCompat.getString(
                requireContext(),
                R.string.CompleteLevel
            )
        } ${level}."
        val builder = AlertDialog.Builder(context)
        builder.setTitle(
            ContextCompat.getString(
                requireContext(),
                R.string.Congratulation
            )
        )
        builder.setMessage(mesage)
        builder.setPositiveButton("Ok") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showHelpRedMessage() {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(ContextCompat.getString(requireContext(), R.string.HelpRedMessage))
        builder.setPositiveButton("Ok") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showHelpGreenMessage() {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(ContextCompat.getString(requireContext(), R.string.HelpGreenMessage))
        builder.setPositiveButton("Ok") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }


    private fun showHelpMessage() {
        val builder = AlertDialog.Builder(context)
        val guessName: String = when (viewModel.guess?.guessType) {
            GuessType.COUNTRY -> ContextCompat.getString(requireContext(), R.string.preHelpCountry)
            GuessType.SERIE -> ContextCompat.getString(requireContext(), R.string.preHelpSerie)
            GuessType.FOOTBALL -> ContextCompat.getString(
                requireContext(),
                R.string.preHelpFootball
            )

            else -> ContextCompat.getString(requireContext(), R.string.preHelpLocal)
        }
        var name = viewModel.guess!!.name.lowercase(Locale.ROOT)
        if (viewModel.guess!!.guessType == GuessType.COUNTRY && viewModel.getLanguage() == "en") {
            name = viewModel.translateToEnglish[name.uppercase(Locale.ROOT)].toString()
        }
        builder.setMessage(
            "$guessName ${ContextCompat.getString(requireContext(), R.string.endWith)} ${
                name.substring(
                    name.length - 2,
                    name.length
                )
            }"
        )
        builder.setPositiveButton("Ok") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun showDegoratoryMessage() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(ContextCompat.getString(requireContext(), R.string.failed))
        builder.setMessage(ContextCompat.getString(requireContext(), R.string.failedMessage))
        builder.setPositiveButton("Ok") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showError() {
        viewModel.error++
        viewModel.point -= 5
        when (viewModel.error) {
            1 -> {
                binding.imgError1.clearColorFilter()
                binding.imgError1.setImageResource(R.drawable.cancelar)
            }

            2 -> {
                binding.imgError2.clearColorFilter()
                binding.imgError2.setImageResource(R.drawable.cancelar)
            }

            3 -> {
                binding.imgError3.clearColorFilter()
                binding.imgError3.setImageResource(R.drawable.cancelar)
                if (!viewModel.local)
                    binding.btnHelp.visibility = View.VISIBLE
            }

            4 -> {
                binding.imgError4.clearColorFilter()
                binding.imgError4.setImageResource(R.drawable.cancelar)
            }

            5 -> {
                binding.imgError5.clearColorFilter()
                binding.imgError5.setImageResource(R.drawable.cancelar)
                showDegoratoryMessage()
                findNavController().popBackStack()
            }

            else -> {
                showDegoratoryMessage()
                findNavController().popBackStack()
            }
        }
    }

    inner class TextWatcher(var text: String) : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (viewModel.help) {
                with(binding.tieSearch.text.toString()) {
                    if (this.length < 3 || this.isEmpty())
                        adapterList.update(mutableListOf())
                    else
                        adapterList.update(viewModel.getFilterList(this))
                }
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }
}