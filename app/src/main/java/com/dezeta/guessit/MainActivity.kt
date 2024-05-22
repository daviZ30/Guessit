package com.dezeta.guessit

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.GestureDetector
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dezeta.guessit.databinding.ActivityMainBinding
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.ui.ViewModelMain
import com.dezeta.guessit.ui.login.LoginActivity
import com.github.nikartm.button.FitButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: NavigationView
    private lateinit var preferences: SharedPreferences.Editor
    private val viewModel: ViewModelMain by viewModels()
    private var x1 = 0.0f
    private var x2 = 0.0f
    private var y1 = 0.0f
    private var y2 = 0.0f
    private lateinit var gestureDetector: GestureDetector

    companion object {
        private var MIN_DISTANCE = 150
        private var TAG = "Swipe Position"
    }


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

        setup()
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        //Guardado de datos
        preferences.putString("email", viewModel.user.value!!.email)
        preferences.putString("provider", viewModel.user.value!!.provider.toString())
        preferences.putInt("point", viewModel.user.value!!.point)
        preferences.putString("name", viewModel.user.value!!.name)
        preferences.apply()


    }

    private fun setup() {
        val bundle = intent.extras
        viewModel.user.value = bundle?.getSerializable("user") as User?

        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val headerView: View = navigationView.getHeaderView(0)
        //headerView.findViewById(R.id.tvDrawerName).text = "Your Text Here"

        val btnSignOut = headerView.findViewById<FitButton>(R.id.navBtnSignOut)
        val tvDrawerName = headerView.findViewById<TextView>(R.id.navTvName)
        val tvEmail = headerView.findViewById<TextView>(R.id.navTvEmail)
        val tvDrawerPoint = headerView.findViewById<TextView>(R.id.navTvPoint)

        tvDrawerName.text = viewModel.user.value!!.name
        tvEmail.text = viewModel.user.value!!.email
        tvDrawerPoint.text = viewModel.user.value!!.point.toString()

        btnSignOut.setOnClickListener {
            preferences.clear()
            preferences.apply()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
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
}