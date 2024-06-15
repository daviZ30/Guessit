package com.example.signup.ui.preferences

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.dezeta.guessit.R
import com.dezeta.guessit.utils.Locator
import java.util.Locale

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting, rootKey)
        initPreferences()
        //preferenceManager.preferenceDataStore = Locator.settingsPreferencesRepository
    }

    private fun initPreferences() {
        val theme =
            preferenceManager.findPreference<Preference>(getString(R.string.theme)) as SwitchPreference?
        val github =
            preferenceManager.findPreference<Preference>(getString(R.string.pref_gitHub)) as Preference
        val linkedIn =
            preferenceManager.findPreference<Preference>(getString(R.string.pref_LinkedIn)) as Preference

        val themeActive = Locator.PreferencesRepository.getTheme()
        if (themeActive != "none") {
            theme?.isChecked = themeActive.toBoolean()
        } else {
            val isDarkThemeOn =
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            theme?.isChecked = isDarkThemeOn
        }

        theme?.setOnPreferenceChangeListener { preference, newValue ->
            if (newValue == true) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Locator.PreferencesRepository.saveTheme(true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Locator.PreferencesRepository.saveTheme(false)
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

        val option =
            preferenceManager.findPreference<Preference>(getString(R.string.pref_language)) as ListPreference?

        val language = Locator.PreferencesRepository.getLanguage()
        if (language != "none") {
            option?.value = language
        }

        option?.setOnPreferenceChangeListener { preference, newValue ->
            if (preference is ListPreference) {
                val index = preference.findIndexOfValue(newValue.toString())
                val entryvalue = preference.entryValues.get(index)
                Locator.PreferencesRepository.saveLanguage(entryvalue.toString())
                val locale = Locale(entryvalue.toString())
                Locale.setDefault(locale)

                val configuration = Configuration(resources.configuration)
                configuration.setLocale(locale)

                resources.updateConfiguration(configuration, resources.displayMetrics)

                recreate(requireActivity())
            }
            true
        }

    }


}