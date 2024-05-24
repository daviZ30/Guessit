package com.dezeta.guessit.ui.daily

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
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

class DailyFragment : Fragment() {
    //TODO Guardar variable help con preferencias
    //TODO Modificar la ayuda para que salga algo de la clase info o las tres ultimas letras si es online y la ultima letra si el local
    //TODO CAmbiar la forma de dectetar local
    var listRed = mutableListOf<String>()
    var listGreen = mutableListOf<String>()

    var NumImage = 1
    var LastImage = 1
    var img: Img? = null

    private var images: List<Img>? = null

    private lateinit var adapterList: SearchAdapter

    private val viewModel: ViewModelDaily by viewModels()

    /*
     movieListView.setOnItemClickListener { _, _, position, _ ->
            val selectedMovie = movieTitles[position]
            Toast.makeText(this, "Película seleccionada: $selectedMovie", Toast.LENGTH_SHORT).show()
        }
     */
    private var _binding: FragmentDailyBinding? = null
    private val binding get() = _binding!!

    private fun getImage(): String? {
        images?.forEach {
            println(it)
            if (it.order == NumImage)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyBinding.inflate(inflater, container, false)
        arguments.let {
            if (it != null) {
                viewModel.serie = it.getSerializable("serie") as Guess
                if (viewModel.serie!!.guessType == GuessType.COUNTRY || viewModel.serie!!.guessType == GuessType.FOOTBALL) {
                    binding.btnCategoty.visibility = View.GONE
                }
                with(viewModel) {
                    images = getImages(viewModel.serie!!)
                    local = it.getBoolean("local")
                    if (local) {
                        help = false
                        with(binding) {
                            tvDailyHelp.text = "Puedes activar la ayuda en los ajustes"
                            image.setImageBitmap(loadImageBitmapFromInternalStorage(getImage()!!))
                            btnCategoty.visibility = View.GONE
                            cvShowList.visibility = View.GONE
                        }

                    } else {
                        binding.tvDailyHelp.text =
                            "Si saltas la imagen tendrás que sacrificar una vida"
                        help = true
                        loadImage()
                    }

                }
            }
        }
        return binding.root
    }

    private fun loadImage() {
        with(binding.lottieLoadAnimation) {
            visibility = View.VISIBLE
            setAnimation(R.raw.load_image)
            playAnimation()
        }
        Glide.with(requireContext())
            .load(getImage())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.lottieLoadAnimation.cancelAnimation()
                    binding.lottieLoadAnimation.visibility = View.GONE
                    binding.image.setImageDrawable(resource)
                    return true
                }

            })
            .into(binding.image)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        adapterList = SearchAdapter() {
            binding.tieSearch.setText(it)
        }
        //  adapterList.update(mutableListOf("Hola","Adios"))

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
            if (binding.tieSearch.text.toString().trim().uppercase(Locale.ROOT)
                == viewModel.serie!!.name.uppercase(Locale.ROOT)
            ) {
                if(!viewModel.local){
                    viewModel.updatePoint()
                    refreshHeader()
                }
                showCongratulatoryMessage()
                findNavController().popBackStack()
            } else {
                showError()
                val newSerie = viewModel.getSerieFromName(binding.tieSearch.text.toString().trim())
                when {
                    newSerie == null -> {}
                    newSerie.category == viewModel.serie!!.category -> {
                        listGreen.add(newSerie.category.toString())
                    }

                    else -> {
                        listRed.add(newSerie.category.toString())
                    }
                }
            }

        }
        binding.btnNext.setOnClickListener {

            if (NumImage < 3 && !viewModel.local) {
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                if (NumImage == LastImage) {
                    showError()
                }
                NumImage++
                if (NumImage > LastImage) {
                    LastImage = NumImage
                }
                loadImage()
            } else if (NumImage < 3 && viewModel.local) {
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                if (NumImage == LastImage) {
                    showError()
                }
                NumImage++
                LastImage = NumImage
                binding.image.setImageBitmap(loadImageBitmapFromInternalStorage(getImage()!!))
            }
        }
        binding.btnPrevious.setOnClickListener {

            if (NumImage > 1 && !viewModel.local) {
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                NumImage--
                loadImage()
            } else if (NumImage > 1 && viewModel.local) {
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                NumImage--
                showError()
                binding.image.setImageBitmap(loadImageBitmapFromInternalStorage(getImage()!!))
            }
        }
    }

    private fun showConfirmationDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("¿Desea sacrificar dos vidas para mostrar la lista completa?")
        builder.setPositiveButton("Si") { _, _ ->
            showError()
            showError()
            when(viewModel.serie?.guessType){
                GuessType.COUNTRY ->{
                    adapterList.update(viewModel.getCountryNameList())
                }
                GuessType.FOOTBALL ->{
                    adapterList.update(viewModel.getPlayerNameList().toMutableList())
                }
                GuessType.SERIE ->{

                }
                else -> {
                    adapterList.update(viewModel.getSerieList().map { it.name }.toMutableList())
                }
            }

        }

        builder.setNegativeButton("No") { _, _ ->
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

        var categoryAdapter = CategoryAdapter(listRed, listGreen)
        categoryAdapter.update(viewModel.categoryList().toMutableList())
        rvCategoryList.adapter = categoryAdapter

        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showCongratulatoryMessage() {
        var mesage = ""
        if(viewModel.local)
            mesage = "Has superado el nivel: ${viewModel.serie!!.name}."
        else
            mesage = "Has superado el nivel: ${viewModel.serie!!.name}. Has obtenido ${viewModel.point} puntos"
        val builder = AlertDialog.Builder(context)
        builder.setTitle("¡Felicidades!")
        builder.setMessage(mesage)
        builder.setPositiveButton("Aceptar") { dialog, _ ->

            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showHelpRedMessage() {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Una categoría en rojo indica que has seleccionado una serie con una categoría, pero esta no coincide con la serie que debes encontrar.")
        builder.setPositiveButton("Ok") { dialog, _ ->

            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showHelpGreenMessage() {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Una categoría en verde significa que la categoría de la serie que has seleccionado es la misma que la de la serie a adivinar.")
        builder.setPositiveButton("Ok") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }


    private fun showHelpMessage() {
        val builder = AlertDialog.Builder(context)
        var guessName = ""
        when (viewModel.serie?.guessType) {
            GuessType.COUNTRY -> guessName = "El pais"
            GuessType.SERIE -> guessName = "La serie"
            GuessType.FOOTBALL -> guessName = "El jugador"
            else -> guessName = "La entidad o concepto"

        }
        builder.setMessage(
            "$guessName termina por: ${
                viewModel.serie!!.name.substring(
                    viewModel.serie!!.name.length - 2,
                    viewModel.serie!!.name.length
                )
            }"
        )
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun showDegoratoryMessage() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Has fallado")
        builder.setMessage("Has superado el limite de intentos")
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showError() {
        viewModel.error++
        viewModel.point -= 5
        when (viewModel.error) {
            1 -> binding.imgError1.setImageResource(R.drawable.cancelar)
            2 -> binding.imgError2.setImageResource(R.drawable.cancelar)
            3 -> {
                binding.imgError3.setImageResource(R.drawable.cancelar)
                if (!viewModel.local)
                    binding.btnHelp.visibility = View.VISIBLE
            }

            4 -> binding.imgError4.setImageResource(R.drawable.cancelar)
            5 -> {
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

        override fun afterTextChanged(s: Editable) {

        }
    }


}