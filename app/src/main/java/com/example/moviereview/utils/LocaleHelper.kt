package com.example.moviereview.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale
import androidx.core.content.edit

object LocaleHelper {

    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"

    // Called on Activity start to apply the saved language
    fun onAttach(context: Context): Context {
        val deviceLanguage = Locale.getDefault().language

        // Try to get saved language. If none exists, use deviceLanguage
        val lang = getPersistedData(context, deviceLanguage)
        return setLocale(context, lang)
    }

    // Save new language and return updated context
    fun setLocale(context: Context, language: String): Context {
        persist(context, language)
        return updateResources(context, language)
    }

    private fun getPersistedData(context: Context, defaultLanguage: String): String {
        val preferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage) ?: defaultLanguage
    }

    private fun persist(context: Context, language: String) {
        val preferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        preferences.edit { putString(SELECTED_LANGUAGE, language) }
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }
}