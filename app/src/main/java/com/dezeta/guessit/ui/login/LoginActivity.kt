package com.dezeta.guessit.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.dezeta.guessit.ui.main.MainActivity
import com.dezeta.guessit.R
import com.dezeta.guessit.databinding.ActivityLoginBinding
import com.dezeta.guessit.domain.Repository.Resource
import com.dezeta.guessit.domain.entity.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var fadeInAnimation: Animation
    private lateinit var fadeOutAnimation: Animation
    private lateinit var fadeOutAnimationLottie: Animation
    private val viewModel: ViewModelLogin by viewModels()
    private var register = false
    private var endload = false

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    if (account != null) {
                        starLoadAnimation()
                        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                        viewModel.signInGoogle(credential, account.email)
                    }
                } catch (e: ApiException) {
                    showAlert("Error", "No se ha podido iniciar sesión con google")
                }
            }
        }

    inner class TextWatcher(private var t: TextInputLayout) : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable) {
            t.isErrorEnabled = false
        }
    }
    private fun getDrawableUri(drawableId: Int): Uri? {
        return try {
            val resourceId = resources.getResourceEntryName(drawableId)

            Uri.parse("android.resource://${packageName}/drawable/$resourceId")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
        session()
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
                        }
                    } else {
                        showHome(it.data as String)
                    }

                }
            }

        }
        viewModel.getState().observe(this) { state ->
            when (state) {
                is LoginState.EmailNotVerifiedError -> {
                    showAlert(
                        "Error",
                        "Por favor, revisa tu correo electrónico y verifica tu dirección haciendo clic en el enlace que te hemos enviado."
                    )
                }
                is LoginState.GoogleSignInError ->{
                    showAlert(
                        "Atención",
                        "Ya dispone de una cuenta de google asociada a este correo, seleccione continuar con google"
                    )
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
                    starLoadAnimation()

                    if (register) {
                        viewModel.signup(getDrawableUri(R.drawable.user_profile)!!)
                    } else {
                        viewModel.signin()
                    }
                }
            }
        }


    }

    private fun starLoadAnimation() {
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
                            if (!endload) {
                                playAnimation()
                            }
                            visibility = View.INVISIBLE
                        }
                    })
                }
            }
        }
    }

    private fun session() {
        val preferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = preferences.getString("email", null)

        if (email != null) {
            binding.LayoutLogin.visibility = View.INVISIBLE
            starLoadAnimation()
            showHome(email)
        }
    }

    override fun onStart() {
        super.onStart()
        binding.LayoutLogin.visibility = View.VISIBLE

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

    private fun showHome(email: String) {
        endload = true
        val MenuIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(MenuIntent)


    }

    private fun setup() {
        window.statusBarColor = resources.getColor(R.color.BlueRange1);
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
                with(binding) {
                    tilConfirmPassword.visibility = View.VISIBLE
                    tilConfirmPassword.startAnimation(fadeInAnimation)

                    btnChanged.text = "Login"
                }

            } else {
                register = false
                with(binding) {
                    tilConfirmPassword.startAnimation(fadeOutAnimation)
                    btnChanged.text = "Registrar"
                }

            }
        }
        binding.btnLoginGoogle.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()

            startForResult.launch(googleClient.signInIntent)
        }
    }

}