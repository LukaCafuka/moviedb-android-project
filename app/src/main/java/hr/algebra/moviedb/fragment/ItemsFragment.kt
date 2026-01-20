package hr.algebra.moviedb.fragment

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import hr.algebra.moviedb.adapter.ItemAdapter
import hr.algebra.moviedb.databinding.FragmentItemsBinding
import hr.algebra.moviedb.framework.fetchItems
import hr.algebra.moviedb.model.Item

private const val KEY_SCROLL_STATE = "scroll_state"

class ItemsFragment : Fragment() {

    private var _binding: FragmentItemsBinding? = null
    private val binding get() = _binding!!
    
    private var items: MutableList<Item> = mutableListOf()
    private var scrollState: Parcelable? = null
    private var adapter: ItemAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemsBinding.inflate(inflater, container, false)
        
        // Restore scroll state
        savedInstanceState?.let {
            scrollState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(KEY_SCROLL_STATE, Parcelable::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.getParcelable(KEY_SCROLL_STATE)
            }
        }
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        // Load data immediately when view is created
        refreshData()
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh data and apply settings when returning to fragment
        refreshData()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        // Clear adapter reference when view is destroyed
        adapter = null
        _binding = null
    }
    
    private fun setupRecyclerView() {
        binding.rvItems.layoutManager = LinearLayoutManager(requireContext())
    }
    
    private fun refreshData() {
        // Fetch items from database
        items = requireContext().fetchItems()
        
        // Apply sorting based on preferences
        sortItems()
        
        // Get show_images preference
        val showImages = getShowImagesSetting()
        
        // Always create a new adapter to ensure it's attached to the current RecyclerView
        adapter = ItemAdapter(requireContext(), items, showImages)
        binding.rvItems.adapter = adapter
        
        // Restore scroll position after data refresh
        scrollState?.let { state ->
            binding.rvItems.layoutManager?.onRestoreInstanceState(state)
            scrollState = null // Clear after restoring
        }
    }
    
    private fun sortItems() {
        val sortOrder = getSortOrderSetting()
        
        when (sortOrder) {
            "title_asc" -> items.sortBy { it.title.lowercase() }
            "title_desc" -> items.sortByDescending { it.title.lowercase() }
            "date_asc" -> items.sortBy { it.releaseDate }
            "date_desc" -> items.sortByDescending { it.releaseDate }
            "rating_asc" -> items.sortBy { it.rating }
            "rating_desc" -> items.sortByDescending { it.rating }
            else -> items.sortBy { it.title.lowercase() } // Default to title A-Z
        }
    }
    
    private fun getSortOrderSetting(): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        return prefs.getString("sort_order", "title_asc") ?: "title_asc"
    }
    
    private fun getShowImagesSetting(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        return prefs.getBoolean("show_images", true)
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save scroll position (only if binding is still valid)
        _binding?.rvItems?.layoutManager?.onSaveInstanceState()?.let { state ->
            outState.putParcelable(KEY_SCROLL_STATE, state)
        }
    }
}