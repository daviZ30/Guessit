package com.dezeta.guessit.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.dezeta.guessit.MainActivity
import com.dezeta.guessit.R
import com.dezeta.guessit.databinding.ActivityLoginBinding
import com.dezeta.guessit.domain.Repository.Resource
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.showSnackbar
import com.dezeta.guessit.usecase.LoginState
import com.dezeta.guessit.usecase.ViewModelLogin
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityLoginBinding

    private lateinit var fadeInAnimation: Animation
    private lateinit var fadeOutAnimation: Animation
    private lateinit var fadeOutAnimationLottie: Animation
    private val viewModel: ViewModelLogin by viewModels()
    private var register = false
    private var endload = false

    inner class TextWatcher(private var t: TextInputLayout) : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable) {
            t.isErrorEnabled = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
        binding.viewmodel = this.viewModel
        binding.lifecycleOwner = this

        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        fadeOutAnimationLottie = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {
                binding.tilConfirmPassword.visibility = View.GONE
            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.tilConfirmPassword.visibility = View.GONE
            }
        })
        viewModel.getResult().observe(this) {
            when (it) {
                is Resource.Error -> {
                    endload = true
                    showAlert("Error", it.exception.message.toString())
                    println("NO IR A CASA")
                }

                is Resource.Success<*> -> {
                    endload = true
                    if (register) {
                        showAlert(
                            "Atención",
                            "Por favor, revisa tu correo electrónico y verifica tu dirección haciendo clic en el enlace que te hemos enviado."
                        )
                        with(binding) {
                            tieLoginMail.setText("")
                            tieLoginPassword.setText("")
                            tieConfirmPassword.setText("")
                            btnChanged.callOnClick()
                            //register = false
                          //  tilConfirmPassword.visibility = View.GONE
                            //btnChanged.text = "Registrar"
                        }

                    } else {
                        showHome(it.data.toString(), ProviderType.BASIC)
                    }

                }
            }

        }
        viewModel.getState().observe(this) { state ->
            when (state) {
                is LoginState.EmailNotVerifiedError -> {
                    showAlert("Error","Por favor, revisa tu correo electrónico y verifica tu dirección haciendo clic en el enlace que te hemos enviado.")
                }
                is LoginState.passwordEmtyError -> {
                    with(binding.tilLoginPassword) {
                        error = "Introduce la contraseña"
                        requestFocus()
                    }
                }

                is LoginState.emailEmtyError -> {
                    with(binding.tilLoginMail) {
                        error = "Introduce el correo electrónico"
                        requestFocus()
                    }
                }

                is LoginState.emailFormatError -> {
                    with(binding.tilLoginMail) {
                        error = "El formato del correo es invalido"
                        requestFocus()
                    }
                }

                is LoginState.passwordFormatError -> {
                    with(binding.tilLoginPassword) {
                        error =
                            "La contraseña debe contener letras, número y 8 caracteres como mínimo"
                        requestFocus()
                    }
                }

                is LoginState.NotEqualsPasswordError -> {
                    with(binding.tilConfirmPassword) {
                        error =
                            "Las contraseñas no son iguales"
                        requestFocus()
                    }
                }

                is LoginState.Success -> {
                    with(binding.lottieLoadAnimation) {
                        visibility = View.VISIBLE
                        setAnimation(R.raw.load)
                        playAnimation()
                        addAnimatorUpdateListener {
                            val progress = (it.animatedValue as Float * 100).toInt()
                            if (progress == 100) {
                                this.startAnimation(fadeOutAnimationLottie)
                                fadeOutAnimationLottie.setAnimationListener(object :
                                    Animation.AnimationListener {
                                    override fun onAnimationStart(animation: Animation?) {}
                                    override fun onAnimationRepeat(animation: Animation?) {}

                                    override fun onAnimationEnd(animation: Animation?) {
                                        // findNavController().popBackStack()
                                        if (!endload) {
                                            playAnimation()
                                        }
                                        visibility = View.INVISIBLE
                                    }
                                })
                            }
                        }
                    }
                    if (register) {
                        viewModel.signup()
                    } else {
                        viewModel.signin()
                    }
                }
            }
        }


    }

    private fun showAlert(title: String, str: String) {
        val builder = AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(str)
            setPositiveButton("Aceptar", null)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, privider: ProviderType) {
        val MenuIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", privider.name)
        }
        startActivity(MenuIntent)


    }

    private fun setup() {
        binding.tieLoginMail.addTextChangedListener(
            TextWatcher(
                binding.tilLoginMail
            )
        )
        binding.tieLoginPassword.addTextChangedListener(
            TextWatcher(
                binding.tilLoginPassword
            )
        )
        binding.tieConfirmPassword.addTextChangedListener(
            TextWatcher(
                binding.tilConfirmPassword
            )
        )
        binding.btnContinue.setOnClickListener {
            //startActivity(MenuIntent)
            if (register) {
                viewModel.validateSignUp()
            } else {
                viewModel.validateSignIn()
            }

        }

        binding.btnChanged.setOnClickListener {
            if (!register) {
                register = true
                binding.tilConfirmPassword.visibility = View.VISIBLE
                binding.tilConfirmPassword.startAnimation(fadeInAnimation)
                binding.btnChanged.text = "Login"
            } else {
                register = false
                binding.tilConfirmPassword.startAnimation(fadeOutAnimation)
                binding.btnChanged.text = "Registrar"
            }
        }
    }

}