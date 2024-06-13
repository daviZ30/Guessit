package com.dezeta.guessit.ui.duel

import android.app.AlertDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dezeta.guessit.R
import com.dezeta.guessit.databinding.FragmentDuelBinding
import com.dezeta.guessit.domain.entity.Info
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.domain.entity.GuessType
import com.dezeta.guessit.loadImageBitmapFromInternalStorage
import kotlinx.coroutines.launch

class DuelFragment : Fragment() {
    private var _binding: FragmentDuelBinding? = null
    private val binding get() = _binding!!
    private var score: Int? = null
    private var level: Int? = null

    private val viewModel: ViewModelDuel by viewModels()

    lateinit var fadeOutAnimation: Animation
    lateinit var slideUpAnimation: Animation
    lateinit var serieTop: Guess
    lateinit var serieButton: Guess
    lateinit var infoTop: Info
    lateinit var infoButton: Info
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDuelBinding.inflate(inflater, container, false)
        binding.viewmodel = this.viewModel
        binding.lifecycleOwner = this
        fadeOutAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
        slideUpAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
        arguments.let {
            if (it != null) {
                level = it.getInt("level")
                score = it.getInt("score")
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.score.value = "0"
        setup()
        super.onViewCreated(view, savedInstanceState)
        binding.btnDualMore.setOnClickListener {
            if (infoButton.IMDB > infoTop.IMDB || infoButton.IMDB == infoTop.IMDB) {
                starAnimationButton(R.raw.success, true)
                with(viewModel.score) {
                    value = (value!!.toInt() + 1).toString()
                }
                viewModel.point += 3
            } else
                starAnimationButton(R.raw.error, false)
        }
        binding.btnDualLess.setOnClickListener {
            if (infoButton.IMDB < infoTop.IMDB || infoButton.IMDB == infoTop.IMDB) {
                starAnimationButton(R.raw.success, true)
                with(viewModel.score) {
                    value = (value!!.toInt() + 1).toString()
                }
                viewModel.point += 3
            } else
                starAnimationButton(R.raw.error, false)
        }
    }

    private fun loadImage(imageUrl: String, imageView: ImageView, animation: LottieAnimationView) {
        with(animation) {
            visibility = View.VISIBLE
            setAnimation(R.raw.load_image)
            playAnimation()
        }
        Glide.with(requireContext())
            .load(imageUrl)
            .timeout(30000)
            .override(720, 480)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    imageView.setImageDrawable(resource)
                    animation.visibility = View.GONE
                    animation.cancelAnimation()
                    return true
                }
            })
            .into(imageView)

    }

    private fun setup() {
        serieTop = viewModel.getSerie()
        serieButton = viewModel.getSerie()
        infoTop = viewModel.getInfoFromId(serieTop)
        infoButton = viewModel.getInfoFromId(serieButton)

        loadImage(
            viewModel.getImage0(serieTop)!!.img_url,
            binding.imgTop,
            binding.lottieLoadAnimationTop
        )

        loadImage(
            viewModel.getImage0(serieButton)!!.img_url,
            binding.imgButton,
            binding.lottieLoadAnimationButton
        )

        binding.tvDuelImdbTop.text = infoTop.IMDB.toString()
        binding.tvDuelImdb.text = infoButton.IMDB.toString()
        binding.tvDuelSerieButton.text = serieButton.name
        binding.tvDuelSerieTop.text = serieTop.name
    }

    private fun starAnimationButton(json: Int, win: Boolean) {
        binding.btnDualMore.startAnimation(fadeOutAnimation)
        binding.btnDualLess.startAnimation(fadeOutAnimation)
        fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                binding.btnDualMore.visibility = View.GONE
                binding.btnDualLess.visibility = View.GONE
                if (binding.cvDuelImdb.visibility == View.GONE) {
                    binding.cvDuelImdb.visibility = View.VISIBLE
                    binding.cvDuelImdb.startAnimation(slideUpAnimation)
                }

                starAnimationLottie(json, win)
            }
        })
    }

    private fun starAnimationLottie(json: Int, win: Boolean) {
        binding.lottieFullAnimation.visibility = View.VISIBLE
        with(binding.lottieFullAnimation) {
            setAnimation(json)
            playAnimation()
            addAnimatorUpdateListener {
                val progress = (it.animatedValue as Float * 100).toInt()
                if (progress == 100) {
                    this.startAnimation(fadeOutAnimation)
                    fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {}

                        override fun onAnimationRepeat(animation: Animation?) {}

                        override fun onAnimationEnd(animation: Animation?) {
                            binding.lottieFullAnimation.visibility = View.GONE
                            if (win) {
                                with(binding) {
                                    cvDuelImdb.visibility = View.GONE
                                    btnDualMore.visibility = View.VISIBLE
                                    btnDualLess.visibility = View.VISIBLE
                                }
                                if (score != null) {
                                    if (viewModel.score.value!!.toInt() == score) {
                                        viewModel.updateLevel(level!!)
                                        showLevelMessage()
                                        findNavController().popBackStack()
                                    }else{
                                        next()
                                    }
                                }else{
                                    next()
                                }
                            } else {
                                if (score == null) {
                                    viewModel.updatePoint()
                                    showCongratulatoryMessage()
                                    findNavController().popBackStack()
                                } else {
                                    findNavController().popBackStack()
                                }
                            }
                        }
                    })
                }
            }
        }

    }

    private fun showCongratulatoryMessage() {
        val mesage =
            "Has logrado una racha de : ${viewModel.score.value}, consiguiendo ${viewModel.point} puntos."

        val builder = AlertDialog.Builder(context)
        builder.setTitle("¡Felicidades!")
        builder.setMessage(mesage)
        builder.setPositiveButton("Aceptar") { dialog, _ ->

            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showLevelMessage() {
        val mesage = "Has superado el nivel ${level}."
        val builder = AlertDialog.Builder(context)
        builder.setTitle("¡Felicidades!")
        builder.setMessage(mesage)
        builder.setPositiveButton("Aceptar") { dialog, _ ->

            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun next() {
        serieTop = serieButton
        serieButton = viewModel.getSerie()
        infoTop = infoButton
        infoButton = viewModel.getInfoFromId(serieButton)
        loadImage(
            viewModel.getImage0(serieTop)!!.img_url,
            binding.imgTop,
            binding.lottieLoadAnimationTop
        )

        loadImage(
            viewModel.getImage0(serieButton)!!.img_url,
            binding.imgButton,
            binding.lottieLoadAnimationButton
        )

        binding.tvDuelImdbTop.text = infoTop.IMDB.toString()
        binding.tvDuelImdb.text = infoButton.IMDB.toString()
        binding.tvDuelSerieButton.text = serieButton.name
        binding.tvDuelSerieTop.text = serieTop.name
    }


}