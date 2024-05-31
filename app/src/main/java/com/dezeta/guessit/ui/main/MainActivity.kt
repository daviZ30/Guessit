package com.dezeta.guessit.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.dezeta.guessit.R
import com.dezeta.guessit.databinding.ActivityMainBinding
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.ui.login.LoginActivity
import com.dezeta.guessit.utils.CloudStorageManager
import com.dezeta.guessit.utils.Locator
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView


class MainActivity : AppCompatActivity() {


    private lateinit var manager: CloudStorageManager
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: NavigationView

    private lateinit var preferences: SharedPreferences.Editor
    private val viewModel: ViewModelMain by viewModels()
    //var file = createImageFile(this)
    var editProfile = false

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
        val isDarkThemeOn =
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        if (isDarkThemeOn)
            window.statusBarColor = Color.BLACK
        else
            window.statusBarColor = Color.WHITE

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
    fun updateHeader(){
        viewModel.loadUser(Locator.email)
    }

    private fun setupHeader() {
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val headerView: View = navigationView.getHeaderView(0)

        val btnEditProfile = headerView.findViewById<Button>(R.id.navBtnDeleteProfile)
        val imgEditProfile = headerView.findViewById<ImageView>(R.id.navImgEditProfile)
        val tvDrawerName = headerView.findViewById<TextView>(R.id.navTvEmail)
        val tvEmail = headerView.findViewById<TextView>(R.id.navTvEmail)
        val tvDrawerPoint = headerView.findViewById<TextView>(R.id.navTvPoint)

        viewModel.getUserProfileImageByEmail(manager)
        tvEmail.text = viewModel.user.value!!.email
        tvDrawerPoint.text = viewModel.user.value!!.point.toString()
        imgEditProfile.setOnClickListener {
            pickPhotoFromGallery()
        }

        btnEditProfile.setOnClickListener {

        }
    }

    private fun setup() {
        val bundle = intent.extras
        var email = bundle?.getString("email") as String
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
            viewModel.saveImageProfile(manager,data!!)

        }
    }
    private fun pickPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startGallery.launch(intent)


    }
}






