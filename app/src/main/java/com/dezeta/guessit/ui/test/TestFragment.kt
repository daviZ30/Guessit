package com.dezeta.guessit.ui.test

import android.R.id.button3
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dezeta.guessit.R
import com.dezeta.guessit.databinding.FragmentTestBinding
import com.dezeta.guessit.domain.entity.AnswerTest
import com.dezeta.guessit.domain.entity.Guess
import com.github.nikartm.button.FitButton


class TestFragment : Fragment() {

    private var _binding: FragmentTestBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TestViewModel by viewModels()
    var NumImage = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTestBinding.inflate(inflater, container, false)
        arguments.let {
            if (it != null) {
                with(viewModel) {
                    tests.add(it.getSerializable("guess0") as Guess)
                    tests.add(it.getSerializable("guess1") as Guess)
                    tests.add(it.getSerializable("guess2") as Guess)
                    answers0 = getAnswers(viewModel.tests[0])
                    img0 = getImageUrl(viewModel.tests[0])!!
                    answers1 = getAnswers(viewModel.tests[1])
                    img1 = getImageUrl(viewModel.tests[1])!!
                    answers2 = getAnswers(viewModel.tests[2])
                    img2 = getImageUrl(viewModel.tests[2])!!
                    local = it.getBoolean("local")
                    if (local) {

                    } else {
                        for (i in 0..2) {
                            loadImage(i)
                        }
                        with(binding) {
                            tvTestTitle.text = tests[0].name
                            btnTest1.setText(answers0?.get(0)?.answer)
                            btnTest2.setText(answers0?.get(1)?.answer)
                            btnTest3.setText(answers0?.get(2)?.answer)
                            btnTest4.setText(answers0?.get(3)?.answer)
                        }
                    }

                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnTest1.setOnClickListener {
            var lista = when (NumImage) {
                0 -> {
                    viewModel.answers0
                }

                1 -> {
                    viewModel.answers1
                }

                2 -> {
                    viewModel.answers2
                }

                else -> null
            }
            starAnimation(0, lista!![0].correct)
        }
        binding.btnTest2.setOnClickListener {
            var lista = when (NumImage) {
                0 -> {
                    viewModel.answers0
                }

                1 -> {
                    viewModel.answers1
                }

                2 -> {
                    viewModel.answers2
                }

                else -> null
            }
            starAnimation(1, lista!![1].correct)
        }
        binding.btnTest3.setOnClickListener {
            var lista = when (NumImage) {
                0 -> {
                    viewModel.answers0
                }

                1 -> {
                    viewModel.answers1
                }

                2 -> {
                    viewModel.answers2
                }

                else -> null
            }
            starAnimation(2, lista!![2].correct)
        }
        binding.btnTest4.setOnClickListener {
            var lista = when (NumImage) {
                0 -> {
                    viewModel.answers0
                }

                1 -> {
                    viewModel.answers1
                }

                2 -> {
                    viewModel.answers2
                }

                else -> null
            }
            starAnimation(3, lista!![3].correct)
        }
    }

    private fun nextImage() {
        var img = ""
        val view = when (NumImage) {
            0 -> {
                binding.imgBackTest.visibility = View.VISIBLE
                binding.imgBackTest2.visibility = View.INVISIBLE
                binding.imgBackTest3.visibility = View.INVISIBLE
                img = viewModel.img0.img_url
                binding.imgBackTest
            }

            1 -> {
                binding.imgBackTest.visibility = View.INVISIBLE
                binding.imgBackTest2.visibility = View.VISIBLE
                binding.imgBackTest3.visibility = View.INVISIBLE
                img = viewModel.img1.img_url
                binding.imgBackTest2
            }

            2 -> {
                binding.imgBackTest.visibility = View.INVISIBLE
                binding.imgBackTest2.visibility = View.INVISIBLE
                binding.imgBackTest3.visibility = View.VISIBLE
                img = viewModel.img2.img_url
                binding.imgBackTest3
            }

            else -> null
        }

    }

    private fun loadImage(i: Int) {
        var img = ""

        val view = when (i) {
            0 -> {
                img = viewModel.img0.img_url
                binding.imgBackTest
            }

            1 -> {
                img = viewModel.img1.img_url
                binding.imgBackTest2
            }

            2 -> {
                img = viewModel.img2.img_url
                binding.imgBackTest3
            }

            else -> null
        }

        with(binding.lottieLoadAnimation) {
            visibility = View.VISIBLE
            setAnimation(R.raw.load_image)
            playAnimation()
        }
        if (i == 2) {
            Glide.with(requireContext())
                .load(img)
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
                        binding.lottieLoadAnimation.visibility = View.INVISIBLE
                        view!!.setImageDrawable(resource)
                        return true
                    }

                })
                .into(view!!)
        } else {
            Glide.with(requireContext())
                .load(img)
                .into(view!!)
        }
    }


    private fun starAnimation(i: Int, success: Boolean) {
        var btnMain: FitButton? = null
        var btn1: FitButton? = null
        var btn2: FitButton? = null
        var btn3: FitButton? = null
        when (i) {
            0 -> {
                btnMain = binding.btnTest1
                btn1 = binding.btnTest2
                btn2 = binding.btnTest3
                btn3 = binding.btnTest4
            }

            1 -> {
                btnMain = binding.btnTest2
                btn1 = binding.btnTest1
                btn2 = binding.btnTest3
                btn3 = binding.btnTest4
            }

            2 -> {
                btnMain = binding.btnTest3
                btn1 = binding.btnTest2
                btn2 = binding.btnTest1
                btn3 = binding.btnTest4
            }

            3 -> {
                btnMain = binding.btnTest4
                btn1 = binding.btnTest2
                btn2 = binding.btnTest3
                btn3 = binding.btnTest1
            }

        }
        var color = Color.GREEN
        if (!success)
            color = Color.RED

        val colorAnimator = ObjectAnimator.ofArgb(btnMain, "backgroundColor", color)
        val scaleXAnimator = ObjectAnimator.ofFloat(btnMain, "scaleX", 1.1f)
        val scaleYAnimator = ObjectAnimator.ofFloat(btnMain, "scaleY", 1.1f)
        val scaleAnimatorSet = AnimatorSet()
        scaleAnimatorSet.playTogether(scaleXAnimator, scaleYAnimator)
        scaleAnimatorSet.setDuration(600)

        val scaleDownAnimator1 = ObjectAnimator.ofFloat(btn1, "scaleX", 0.9f)
        val scaleDownAnimator2 = ObjectAnimator.ofFloat(btn2, "scaleX", 0.9f)
        val scaleDownAnimator3 = ObjectAnimator.ofFloat(btn3, "scaleX", 0.9f)
        val grayScaleAnimatorSet = AnimatorSet()
        grayScaleAnimatorSet.playTogether(
            scaleDownAnimator1, scaleDownAnimator2, scaleDownAnimator3
        )
        grayScaleAnimatorSet.setDuration(600)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(colorAnimator, scaleAnimatorSet, grayScaleAnimatorSet)
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                if (success) {
                    nextTest()
                } else {
                    findNavController().popBackStack()
                }
                stopAnimation(btnMain!!, btn1!!, btn2!!, btn3!!)
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })

        animatorSet.start()
    }

    private fun stopAnimation(
        btnMain: FitButton,
        btn1: FitButton,
        btn2: FitButton,
        btn3: FitButton
    ) {
        val colorPrimary = ContextCompat.getColor(requireContext(), R.color.BlueRange1_1)
        val colorAnimator = ObjectAnimator.ofArgb(btnMain, "backgroundColor", colorPrimary)

        val scaleXAnimator = ObjectAnimator.ofFloat(btnMain, "scaleX", 1.0f)
        val scaleYAnimator = ObjectAnimator.ofFloat(btnMain, "scaleY", 1.0f)
        val scaleAnimatorSet = AnimatorSet()
        scaleAnimatorSet.playTogether(scaleXAnimator, scaleYAnimator)
        scaleAnimatorSet.setDuration(300)

        val scaleDownAnimator1 = ObjectAnimator.ofFloat(btn1, "scaleX", 1.0f)
        val scaleDownAnimator2 = ObjectAnimator.ofFloat(btn2, "scaleX", 1.0f)
        val scaleDownAnimator3 = ObjectAnimator.ofFloat(btn3, "scaleX", 1.0f)

        val secondAnimation = AnimatorSet()
        secondAnimation.playTogether(
            scaleDownAnimator1, scaleDownAnimator2, scaleDownAnimator3
        )
        secondAnimation.setDuration(300)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(colorAnimator, scaleAnimatorSet, secondAnimation)
        animatorSet.start()

    }

    private fun nextTest() {
        NumImage++
        if (NumImage >= 3) {
            findNavController().popBackStack()
        } else {
            nextImage()
            var lista: List<AnswerTest>? = null
            when (NumImage) {
                1 -> {
                    lista = viewModel.answers1!!
                }

                2 -> {
                    lista = viewModel.answers2!!
                }
            }
            with(viewModel) {
                with(binding) {
                    tvTestTitle.text = tests[NumImage].name
                    btnTest1.setText(lista?.get(0)?.answer)
                    btnTest2.setText(lista?.get(1)?.answer)
                    btnTest3.setText(lista?.get(2)?.answer)
                    btnTest4.setText(lista?.get(3)?.answer)
                }
            }
        }

    }
}