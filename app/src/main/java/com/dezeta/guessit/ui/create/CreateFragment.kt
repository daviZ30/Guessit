package com.dezeta.guessit.ui.create

import android.R
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dezeta.guessit.databinding.FragmentCreateBinding
import com.dezeta.guessit.deleteImage
import com.dezeta.guessit.domain.entity.DifficultySpinner
import com.dezeta.guessit.loadImageBitmapFromInternalStorage
import com.dezeta.guessit.showSnackbar
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

class CreateFragment : Fragment() {

    private lateinit var img: ImgSelected
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ViewModelCreate by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        binding.viewmodel = this.viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    inner class TextWatcher(private var t: TextInputLayout) : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            t.isErrorEnabled = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        viewModel.img1Uri = null
        viewModel.img2Uri = null
        viewModel.img3Uri = null
        binding.tieName.addTextChangedListener(
            TextWatcher(
                binding.tilName
            )
        )

        viewModel.getState().observe(viewLifecycleOwner) {
            when (it) {
                CreateState.idEmpyError -> {
                    with(binding.tilId) {
                        error = ContextCompat.getString(
                            requireContext(),
                            com.dezeta.guessit.R.string.idEmptyError
                        )
                        requestFocus()
                    }
                }

                CreateState.idDuplicateError -> {
                    with(binding.tilId) {
                        error = ContextCompat.getString(
                            requireContext(),
                            com.dezeta.guessit.R.string.idDuplicateError
                        )
                        requestFocus()
                    }
                }

                CreateState.nameEmtyError -> {
                    binding.tilName.error = ContextCompat.getString(
                        requireContext(),
                        com.dezeta.guessit.R.string.nameEmtyError
                    )
                    binding.tilName.requestFocus()
                }

                CreateState.nameDuplicateError -> {
                    binding.tilName.error =
                        ContextCompat.getString(
                            requireContext(),
                            com.dezeta.guessit.R.string.nameDuplicateError
                        )
                    binding.tilName.requestFocus()
                }

                CreateState.difficultyEmtyError -> showSnackbar(
                    requireView(),
                    ContextCompat.getString(
                        requireContext(),
                        com.dezeta.guessit.R.string.difficultEmtyError
                    )
                )

                CreateState.imageEmtyError -> showSnackbar(
                    requireView(),
                    ContextCompat.getString(
                        requireContext(),
                        com.dezeta.guessit.R.string.imageEmtyError
                    )
                )

                CreateState.imageEqualsError -> showSnackbar(
                    requireView(),
                    ContextCompat.getString(
                        requireContext(),
                        com.dezeta.guessit.R.string.imageEqualsError
                    )
                )

                CreateState.Success -> onSuccess()
            }
        }
        binding.spDifficulty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val itemSelected = parent?.getItemAtPosition(position)
                viewModel.difficulty.value = itemSelected as DifficultySpinner
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.difficulty.value = null
            }
        }
        binding.btnAdd.setOnClickListener {
            viewModel.validate()
        }
        binding.btnAddImg1.setOnClickListener { pickPhotoFromGallery(ImgSelected.IMG1) }
        binding.btnAddImg2.setOnClickListener { pickPhotoFromGallery(ImgSelected.IMG2) }
        binding.btnAddImg3.setOnClickListener { pickPhotoFromGallery(ImgSelected.IMG3) }
    }

    private fun onSuccess() {
        showSnackbar(
            requireView(),
            ContextCompat.getString(requireContext(), com.dezeta.guessit.R.string.CreateSuccess)
        )
        findNavController().popBackStack()
    }

    private fun setup() {

        val systemLanguage = Locale.getDefault().language
        val adaptersp =
            ArrayAdapter(
                requireContext(),
                R.layout.simple_spinner_item,
                viewModel.difficultList(systemLanguage)
            )
        adaptersp.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spDifficulty.adapter = adaptersp
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private val startGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data?.data
            var imageName = ""
            when (img) {
                ImgSelected.IMG1 -> {
                    imageName = viewModel.name.value!! + "_img1"
                    deleteImage(imageName)
                    viewModel.saveToInternalStorage(uriToBitmap(data!!)!!, imageName)
                    viewModel.img1Uri = imageName
                    binding.imgCreate1.setImageBitmap(loadImageBitmapFromInternalStorage(imageName))
                }

                ImgSelected.IMG2 -> {
                    imageName = viewModel.name.value!! + "_img2"
                    deleteImage(imageName)
                    viewModel.saveToInternalStorage(uriToBitmap(data!!)!!, imageName)
                    viewModel.img2Uri = imageName
                    binding.imgCreate2.setImageBitmap(loadImageBitmapFromInternalStorage(imageName))
                }

                ImgSelected.IMG3 -> {
                    imageName = viewModel.name.value!! + "_img3"
                    deleteImage(imageName)
                    viewModel.saveToInternalStorage(uriToBitmap(data!!)!!, imageName)
                    viewModel.img3Uri = imageName
                    binding.imgCreate3.setImageBitmap(loadImageBitmapFromInternalStorage(imageName))
                }
            }
        }
    }

    private fun pickPhotoFromGallery(imgS: ImgSelected) {
        if (viewModel.validateName()) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            img = imgS
            startGallery.launch(intent)
        }
    }
}