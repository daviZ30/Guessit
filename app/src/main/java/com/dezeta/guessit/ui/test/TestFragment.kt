package com.dezeta.guessit.ui.test

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dezeta.guessit.R
import com.dezeta.guessit.databinding.FragmentTestBinding
import com.dezeta.guessit.domain.entity.Guess

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
                       loadImage(img0.img_url)
                        with(binding){
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

    private fun loadImage(img:String) {
        val view = when(NumImage) {
            0 -> binding.imgBackTest
            1 -> binding.imgBackTest2
            2 -> binding.imgBackTest3
            else -> null
        }
        with(binding.lottieLoadAnimation) {
            visibility = View.VISIBLE
            setAnimation(R.raw.load_image)
            playAnimation()
        }
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}