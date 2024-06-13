package com.example.signup.ui.preferences

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.dezeta.guessit.R
import com.dezeta.guessit.domain.Repository.Resource
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.utils.Locator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting, rootKey)
        initPreferences()
        //preferenceManager.preferenceDataStore = Locator.settingsPreferencesRepository
    }

    private fun initPreferences() {
        val option =
            preferenceManager.findPreference<Preference>(getString(R.string.theme)) as SwitchPreference?
        val github =
            preferenceManager.findPreference<Preference>(getString(R.string.pref_gitHub)) as Preference
        val linkedIn =
            preferenceManager.findPreference<Preference>(getString(R.string.pref_LinkedIn)) as Preference

        val isDarkThemeOn =
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        option?.isChecked = isDarkThemeOn

        option?.setOnPreferenceChangeListener { preference, newValue ->
            if (newValue == true) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Locator.PreferencesRepository.saveTheme("true")
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Locator.PreferencesRepository.saveTheme("false")
            }
            true
        }
        val openURL = Intent(Intent.ACTION_VIEW)

        github.setOnPreferenceClickListener {
            openURL.data = Uri.parse("https://github.com/daviZ30")
            startActivity(openURL)
            true
        }

        linkedIn.setOnPreferenceClickListener {
            openURL.data = Uri.parse("https://www.linkedin.com/in/david-zambrana-8042912a9/")
            startActivity(openURL)
            true
        }

    }

}