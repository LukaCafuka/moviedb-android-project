package hr.algebra.moviedb.fragment

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import hr.algebra.moviedb.DATA_IMPORTED
import hr.algebra.moviedb.MOVIE_PROVIDER_CONTENT_URI
import hr.algebra.moviedb.R
import hr.algebra.moviedb.api.MovieWorker
import hr.algebra.moviedb.framework.AlarmHelper
import hr.algebra.moviedb.framework.isOnline
import hr.algebra.moviedb.framework.setBooleanPreference

class SettingsFragment : PreferenceFragmentCompat(), 
    SharedPreferences.OnSharedPreferenceChangeListener {
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        
        // Ensure ListPreferences have valid default values set
        initializeListPreferenceDefaults()
        
        // Set up ListPreference summaries to show selected values
        setupListPreferenceSummary("refresh_interval", getString(R.string.refresh_interval_summary))
        setupListPreferenceSummary("sort_order", getString(R.string.sort_order_summary))
        
        // Handle clear cache preference click
        findPreference<Preference>("clear_cache")?.setOnPreferenceClickListener {
            clearCache()
            true
        }
        
        // Handle reset data preference click
        findPreference<Preference>("reset_data")?.setOnPreferenceClickListener {
            showResetDataDialog()
            true
        }
    }
    
    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }
    
    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }
    
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        // Update alarm when auto_refresh or refresh_interval changes
        if (key == "auto_refresh" || key == "refresh_interval") {
            AlarmHelper.updateAlarmFromSettings(requireContext())
        }
    }
    
    override fun onDisplayPreferenceDialog(preference: Preference) {
        // Ensure ListPreference values are ALWAYS set before opening dialog
        if (preference is ListPreference) {
            ensureListPreferenceValue(preference)
        }
        super.onDisplayPreferenceDialog(preference)
    }
    
    private fun ensureListPreferenceValue(preference: ListPreference) {
        val key = preference.key ?: return
        val sharedPreferences = preferenceManager.sharedPreferences ?: return
        
        // ALWAYS ensure value is set - don't just check if null
        val currentValue = preference.value
        val defaultValue = when (key) {
            "refresh_interval" -> {
                sharedPreferences.getString("refresh_interval", "3600") ?: "3600"
            }
            "sort_order" -> {
                sharedPreferences.getString("sort_order", "title_asc") ?: "title_asc"
            }
            else -> return
        }
        
        // Check if current value is valid
        var isValueValid = false
        if (currentValue != null && currentValue.isNotEmpty()) {
            try {
                isValueValid = preference.findIndexOfValue(currentValue) >= 0
            } catch (e: Exception) {
                // If findIndexOfValue crashes, value is invalid
                isValueValid = false
            }
        }
        
        // If value is null, empty, or invalid, set the default
        if (!isValueValid) {
            // Set value synchronously using commit() to ensure it's written before dialog opens
            preference.value = defaultValue
            sharedPreferences.edit().putString(key, defaultValue).commit()
        } else {
            // Even if value exists, ensure it's synced with SharedPreferences
            sharedPreferences.edit().putString(key, currentValue).commit()
        }
    }
    
    private fun initializeListPreferenceDefaults() {
        val sharedPreferences = preferenceManager.sharedPreferences ?: return
        
        // Ensure refresh_interval has a value in SharedPreferences
        if (!sharedPreferences.contains("refresh_interval")) {
            sharedPreferences.edit().putString("refresh_interval", "3600").apply()
        }
        
        // Ensure sort_order has a value in SharedPreferences
        if (!sharedPreferences.contains("sort_order")) {
            sharedPreferences.edit().putString("sort_order", "title_asc").apply()
        }
        
        // Set the values on the preferences to ensure they're synced
        findPreference<ListPreference>("refresh_interval")?.value = 
            sharedPreferences.getString("refresh_interval", "3600")
        findPreference<ListPreference>("sort_order")?.value = 
            sharedPreferences.getString("sort_order", "title_asc")
        
    }
    
    private fun setupListPreferenceSummary(key: String, defaultSummary: String) {
        val listPreference = findPreference<ListPreference>(key)
        // Use ListPreference's built-in summary handling which automatically shows the selected entry
        // Set summary to "%s" to display the entry, or use a summary provider that safely handles nulls
        listPreference?.summaryProvider = Preference.SummaryProvider<ListPreference> { preference ->
            try {
                // Use getEntry() which is safer than findIndexOfValue
                val entry = preference.entry
                if (entry != null) {
                    entry.toString()
                } else {
                    // Fallback: try to get value and find entry manually with null checks
                    val value = preference.value
                    if (value != null && value.isNotEmpty() && 
                        preference.entries != null && preference.entryValues != null) {
                        val entryValues = preference.entryValues
                        for (i in entryValues.indices) {
                            val entryValue = entryValues[i]
                            if (entryValue != null && entryValue.toString() == value && 
                                i < preference.entries.size) {
                                return@SummaryProvider preference.entries[i]?.toString() ?: defaultSummary
                            }
                        }
                    }
                    defaultSummary
                }
            } catch (e: Exception) {
                defaultSummary
            }
        }
    }
    
    private fun clearCache() {
        try {
            val cacheDir = requireContext().getExternalFilesDir(null)
            var deletedCount = 0
            
            cacheDir?.listFiles()?.forEach { file ->
                if (file.isFile && file.delete()) {
                    deletedCount++
                }
            }
            
            Toast.makeText(
                requireContext(),
                "Cache cleared: $deletedCount files deleted",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error clearing cache: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    private fun showResetDataDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Reset Data")
            setMessage("Are you sure you want to delete all movie data? This action cannot be undone.")
            setIcon(android.R.drawable.ic_dialog_alert)
            setCancelable(true)
            setNegativeButton(getString(R.string.cancel), null)
            setPositiveButton("Reset") { _, _ ->
                resetData()
            }
            show()
        }
    }
    
    private fun resetData() {
        try {
            // Delete all items from database
            val deletedRows = requireContext().contentResolver.delete(
                MOVIE_PROVIDER_CONTENT_URI,
                null,
                null
            )
            
            // Clear cache
            clearCache()
            
            // Clear DATA_IMPORTED flag
            requireContext().setBooleanPreference(DATA_IMPORTED, false)
            
            // Immediately fetch new movies if online
            if (requireContext().isOnline()) {
                WorkManager.getInstance(requireContext()).apply {
                    enqueueUniqueWork(
                        "reset_fetch_movies",
                        ExistingWorkPolicy.REPLACE,
                        OneTimeWorkRequest.from(MovieWorker::class.java)
                    )
                }
                Toast.makeText(
                    requireContext(),
                    "Data reset: $deletedRows items deleted. Fetching new movies...",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Data reset: $deletedRows items deleted. Connect to internet and restart app to fetch movies.",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error resetting data: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
