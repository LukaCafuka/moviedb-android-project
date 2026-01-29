package hr.algebra.moviedb.framework

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.preference.PreferenceManager
import java.util.Locale

/**
 * Helper object for managing application locale/language.
 * Allows switching between English and Croatian.
 */
object LocaleHelper {
    
    private const val LANGUAGE_KEY = "language"
    private const val DEFAULT_LANGUAGE = "en"
    
    /**
     * Gets the currently saved language preference.
     */
    fun getLanguage(context: Context): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }
    
    /**
     * Sets the app language and saves it to preferences.
     */
    fun setLanguage(context: Context, languageCode: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putString(LANGUAGE_KEY, languageCode).apply()
    }
    
    /**
     * Applies the saved language to the given context.
     * Should be called in attachBaseContext of activities.
     */
    fun applyLanguage(context: Context): Context {
        val languageCode = getLanguage(context)
        return updateResources(context, languageCode)
    }
    
    /**
     * Updates the resources configuration with the specified locale.
     */
    fun updateResources(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        
        return context.createConfigurationContext(config)
    }
    
    /**
     * Gets the locale for the specified language code.
     */
    fun getLocale(languageCode: String): Locale {
        return Locale(languageCode)
    }
}
