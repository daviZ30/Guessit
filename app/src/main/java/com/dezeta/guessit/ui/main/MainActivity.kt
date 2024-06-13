package com.dezeta.guessit.ui.main

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.dezeta.guessit.R
import com.dezeta.guessit.databinding.ActivityMainBinding
import com.dezeta.guessit.ui.login.LoginActivity
import com.dezeta.guessit.utils.CloudStorageManager
import com.dezeta.guessit.utils.Locator
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {
    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var manager: CloudStorageManager
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: NavigationView
    private var isDarkThemeOn by Delegates.notNull<Boolean>()
    lateinit var fadeOutAnimation: Animation

    private lateinit var preferences: SharedPreferences.Editor
    private val viewModel: ViewModelMain by viewModels()

    override fun onBackPressed() {
        val currentFragment = navController.currentDestination?.id
        if (currentFragment == R.id.MenuFragment) {
            finishAffinity()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDarkThemeOn =
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        setTemes()
        fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        preferences =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        navView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        setup()
    }

    private fun setTemes() {
        if (isDarkThemeOn) {
            window.statusBarColor = Color.BLACK
        } else
            window.statusBarColor = Color.WHITE
    }

    fun updateHeader() {
        if (isOnline())
            viewModel.loadUser(Locator.email)

    }

    fun setOflineHeader() {
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val headerView: View = navigationView.getHeaderView(0)
        val btnDeleteProfile = headerView.findViewById<Button>(R.id.navBtnDeleteProfile)
        val imgEditProfile = headerView.findViewById<ImageView>(R.id.navImgEditProfile)
        val tvDrawerName = headerView.findViewById<TextView>(R.id.navTvName)
        val tvEmail = headerView.findViewById<TextView>(R.id.navTvEmail)
        val navImg = headerView.findViewById<CircleImageView>(R.id.navImgProfile)
        val navLinerLayout = headerView.findViewById<LinearLayout>(R.id.navLinearLayout)
        val navImgDolar = headerView.findViewById<ImageView>(R.id.navImgDolar)

        navImg.setImageResource(R.drawable.user_profile)
        tvEmail.text = ContextCompat.getString(this, R.string.nav_email)
        tvDrawerName.text = ContextCompat.getString(this, R.string.nav_name_user)

        btnDeleteProfile.isEnabled = false

        imgEditProfile.visibility = View.INVISIBLE
        navImgDolar.visibility = View.INVISIBLE
        navLinerLayout.visibility = View.INVISIBLE


    }

    private fun setupHeader() {
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val headerView: View = navigationView.getHeaderView(0)
        val btnDeleteProfile = headerView.findViewById<Button>(R.id.navBtnDeleteProfile)
        val imgEditProfile = headerView.findViewById<ImageView>(R.id.navImgEditProfile)
        lottieAnimationView =
            headerView.findViewById<LottieAnimationView>(R.id.navLottieLoadAnimation)
        val tvDrawerName = headerView.findViewById<TextView>(R.id.navTvName)
        val tvEmail = headerView.findViewById<TextView>(R.id.navTvEmail)
        val tvDrawerPoint = headerView.findViewById<TextView>(R.id.navTvPoint)
        val tvPrePoint = headerView.findViewById<TextView>(R.id.navTvPrePoint)
        val tvPostPoint = headerView.findViewById<TextView>(R.id.navTvPostPoint)
        val navLinerLayout = headerView.findViewById<LinearLayout>(R.id.navLinearLayout)
        val navImgDolar = headerView.findViewById<ImageView>(R.id.navImgDolar)


        if (isDarkThemeOn) {
            tvPrePoint.setTextColor(Color.WHITE)
            tvPostPoint.setTextColor(Color.WHITE)
        }
        if (isOnline()) {
            imgEditProfile.visibility = View.VISIBLE
            navImgDolar.visibility = View.VISIBLE
            navLinerLayout.visibility = View.VISIBLE
            viewModel.getUserProfileImageByEmail(manager)
            tvEmail.text = viewModel.user.value!!.email
            tvDrawerName.text = viewModel.user.value!!.name
            tvDrawerPoint.text = viewModel.user.value!!.point.toString()
            imgEditProfile.setOnClickListener {
                pickPhotoFromGallery()
            }

            btnDeleteProfile.setOnClickListener {
                showConfirmationDialog()
            }
        }

    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Desea eliminar la cuenta?")
        builder.setPositiveButton("Si") { _, _ ->
            with(lottieAnimationView) {
                visibility = View.VISIBLE
                setAnimation(R.raw.load_image)
                playAnimation()
            }
            viewModel.deleteUser()
        }

        builder.setNegativeButton("No") { _, _ ->
        }
        builder.show()


    }

    private fun setup() {
        val bundle = intent.extras
        val email = bundle?.getString("email") as String
        Locator.email = email
        viewModel.loadUser(email)
        viewModel.loadDatabase()
        manager = CloudStorageManager()

        //Guardado de datos
        preferences.putString("email", email)
        preferences.apply()

        viewModel.getState().observe(this) {
            when (it) {
                is MainState.UserSuccess -> {
                    setupHeader()
                }

                MainState.SignOut -> {
                    preferences.clear()
                    preferences.apply()
                    lottieAnimationView.cancelAnimation()
                    lottieAnimationView.visibility = View.INVISIBLE
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }

                is MainState.RefreshUser -> {
                    refreshHeader()
                }

                is MainState.RefreshUrl -> {
                    refreshUri(it.url)
                }
            }
        }

    }

    private fun refreshUri(url: String) {
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val headerView: View = navigationView.getHeaderView(0)

        val navImg = headerView.findViewById<CircleImageView>(R.id.navImgProfile)

        Glide.with(this)
            .load(url)
            .into(navImg)
    }

    private fun refreshHeader() {
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val headerView: View = navigationView.getHeaderView(0)

        val tvEmail = headerView.findViewById<TextView>(R.id.navTvEmail)
        val tvDrawerPoint = headerView.findViewById<TextView>(R.id.navTvPoint)

        tvEmail.text = viewModel.user.value!!.email
        tvDrawerPoint.text = viewModel.user.value!!.point.toString()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        if (isDarkThemeOn) {
            tintMenuItemIcon(menu, R.id.action_signOut, Color.WHITE)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_signOut -> {
                preferences.clear()
                preferences.apply()
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun tintMenuItemIcon(menu: Menu, idItem: Int, color: Int) {
        var drawable = menu.findItem(idItem).icon
        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(drawable, color)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private val startGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data?.data
            val imageName = viewModel.user.value!!.email + "_img1"
            viewModel.saveImageProfile(manager, data!!)

        }
    }

    private fun pickPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startGallery.launch(intent)


    }

    fun isOnline(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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






