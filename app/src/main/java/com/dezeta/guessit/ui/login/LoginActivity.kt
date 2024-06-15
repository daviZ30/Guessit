package com.dezeta.guessit.ui.login

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.dezeta.guessit.ui.main.MainActivity
import com.dezeta.guessit.R
import com.dezeta.guessit.databinding.ActivityLoginBinding
import com.dezeta.guessit.databinding.DialogLoginNameBinding
import com.dezeta.guessit.domain.Repository.Resource
import com.dezeta.guessit.utils.Locator
import com.dezeta.guessit.utils.MyWorkerFirebase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var bindingDialog: DialogLoginNameBinding

    private lateinit var fadeInAnimation: Animation
    private lateinit var fadeOutAnimation: Animation
    private lateinit var fadeOutAnimationLottie: Animation
    private lateinit var dialog: Dialog
    private var isDarkThemeOn by Delegates.notNull<Boolean>()
    private lateinit var preferences: SharedPreferences.Editor

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
                        viewModel.signInGoogle(
                            this,
                            credential,
                            account.email,
                            account.displayName ?: "Username"
                        )
                    }
                } catch (e: ApiException) {
                    showAlert("Error", ContextCompat.getString(this, R.string.SignErrorGoogle))
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

    fun getLanguegeSystem(): String {
        val configuracion = resources.configuration
        val idioma = configuracion.locales[0]
        return idioma.language
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val theme = viewModel.getDarkTheme()
        val language = viewModel.getLanguege()
        if (language != "none") {

            if (language == "es") {
                val locale = Locale(language)
                Locale.setDefault(locale)

                val configuration = Configuration(resources.configuration)
                configuration.setLocale(locale)

                resources.updateConfiguration(configuration, resources.displayMetrics)
            } else {
                val locale = Locale(language)
                Locale.setDefault(locale)

                val configuration = Configuration(resources.configuration)
                configuration.setLocale(locale)

                resources.updateConfiguration(configuration, resources.displayMetrics)
            }
        } else {
            Locator.PreferencesRepository.saveLanguage(getLanguegeSystem())
        }
        if (theme != "none") {
            if (theme.toBoolean()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        isDarkThemeOn =
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        binding = ActivityLoginBinding.inflate(layoutInflater)
        bindingDialog = DialogLoginNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
        session()
        setTheme()
        preferences =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        dialog = Dialog(this)
        dialog.setContentView(bindingDialog.root)
        dialog.setCancelable(true)

        binding.viewmodel = this.viewModel
        binding.lifecycleOwner = this

        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        fadeOutAnimationLottie = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.tilConfirmPassword.visibility = View.GONE
                binding.tilLoginName.visibility = View.GONE
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
                        val workManager = WorkManager.getInstance(applicationContext)

                        val request = OneTimeWorkRequestBuilder<MyWorkerFirebase>()
                            .setInputData(
                                workDataOf(
                                    "email" to it.data as String
                                )
                            )
                            .setInitialDelay(1, TimeUnit.DAYS)
                            .build()

                        workManager.enqueue(request)

                        showAlert(
                            ContextCompat.getString(this, R.string.attention),
                            ContextCompat.getString(this, R.string.reviseMail)
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
                is LoginState.nameEmtyError -> {
                    with(binding.tilLoginName) {
                        error = ContextCompat.getString(context, R.string.LoginName)
                        requestFocus()
                    }
                }

                is LoginState.nameEqualsError -> {
                    with(binding.tilLoginName) {
                        error = ContextCompat.getString(context, R.string.LoginExistName)
                        requestFocus()
                    }
                }

                is LoginState.GoogleSuccess -> {
                    showHome(state.email)
                }

                is LoginState.GoogleNameExists -> {
                    endload = true
                    dialog.show()
                }

                is LoginState.EmailNotVerifiedError -> {
                    showAlert(
                        ContextCompat.getString(this,R.string.attention),
                        ContextCompat.getString(this, R.string.EmailNotVerified)
                    )
                }

                is LoginState.GoogleSignInError -> {
                    showAlert(
                        ContextCompat.getString(this, R.string.attention),
                        ContextCompat.getString(this, R.string.googleAccountMessage)
                    )
                }

                is LoginState.GoogleNameEmpty -> {
                    with(bindingDialog.tilDialogLogin) {
                        error = ContextCompat.getString(context, R.string.introduceName)
                        requestFocus()
                    }
                }

                is LoginState.GoogleNameEquals -> {
                    with(bindingDialog.tilDialogLogin) {
                        error = ContextCompat.getString(context, R.string.LoginExistName)
                        requestFocus()
                    }
                }

                is LoginState.passwordEmtyError -> {
                    with(binding.tilLoginPassword) {
                        error = ContextCompat.getString(context, R.string.introducePassword)
                        requestFocus()
                    }
                }

                is LoginState.emailEmtyError -> {
                    with(binding.tilLoginMail) {
                        error = ContextCompat.getString(context, R.string.introduceMail)
                        requestFocus()
                    }
                }

                is LoginState.emailFormatError -> {
                    with(binding.tilLoginMail) {
                        error = ContextCompat.getString(context, R.string.MailFormatError)
                        requestFocus()
                    }
                }

                is LoginState.passwordFormatError -> {
                    with(binding.tilLoginPassword) {
                        error =
                            ContextCompat.getString(context, R.string.PasseordFormatError)
                        requestFocus()
                    }
                }

                is LoginState.NotEqualsPasswordError -> {
                    with(binding.tilConfirmPassword) {
                        error =
                            ContextCompat.getString(context, R.string.EqualsPassword)
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

    private fun setTheme() {
        if (isDarkThemeOn) {
            binding.btnLoginGoogle.setTextColor(Color.WHITE)
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
        val offlineMode = preferences.getBoolean(Locator.OFFLINE_MODE, false)

        if (email != null && !offlineMode) {
            binding.LayoutLogin.visibility = View.INVISIBLE
            starLoadAnimation()
            showHome(email)
        }
        if (offlineMode) {
            showHome(Locator.OFFLINE_MODE)
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
            setPositiveButton(ContextCompat.getString(context, R.string.accept), null)
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
        bindingDialog.viewmodel = this.viewModel
        bindingDialog.lifecycleOwner = this
        window.statusBarColor = resources.getColor(R.color.BlueRange1);
        binding.tieLoginMail.addTextChangedListener(
            TextWatcher(
                binding.tilLoginMail
            )
        )
        bindingDialog.tieDialogLogin.addTextChangedListener(
            TextWatcher(
                bindingDialog.tilDialogLogin
            )
        )
        binding.tieLoginName.addTextChangedListener(
            TextWatcher(
                binding.tilLoginName
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
            if (register) {
                viewModel.validateSignUp()
            } else {
                viewModel.validateSignIn()
            }

        }
        bindingDialog.btnloginOk.setOnClickListener {
            viewModel.validateDialogName()
        }
        binding.btnOfflineMode.setOnClickListener {
            preferences.putBoolean(Locator.OFFLINE_MODE, true)
            preferences.apply()
            showHome(Locator.OFFLINE_MODE)
        }

        binding.btnChanged.setOnClickListener {
            if (!register) {
                register = true
                with(binding) {
                    tilConfirmPassword.visibility = View.VISIBLE
                    tilConfirmPassword.startAnimation(fadeInAnimation)
                    tilLoginName.visibility = View.VISIBLE
                    tilLoginName.startAnimation(fadeInAnimation)
                    btnChanged.text = ContextCompat.getString(applicationContext, R.string.SignIn)
                }

            } else {
                register = false
                with(binding) {
                    tilConfirmPassword.startAnimation(fadeOutAnimation)
                    tilLoginName.startAnimation(fadeOutAnimation)
                    btnChanged.text = ContextCompat.getString(applicationContext, R.string.SignUp)
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
        binding.cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Show password
                binding.tieLoginPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.tieConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                // Hide password
                binding.tieLoginPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.tieConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
    }

}