package hr.algebra.moviedb

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import hr.algebra.moviedb.adapter.MovieDetailPagerAdapter
import hr.algebra.moviedb.model.Item
import java.io.Serializable

private const val EXTRA_ITEMS = "hr.algebra.moviedb.ITEMS"
private const val EXTRA_POSITION = "hr.algebra.moviedb.POSITION"
private const val KEY_CURRENT_POSITION = "current_position"

class MovieDetailActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var items: List<Item>
    private var currentPosition: Int = 0

    companion object {
        fun newIntent(context: Context, items: List<Item>, position: Int): Intent {
            return Intent(context, MovieDetailActivity::class.java).apply {
                putExtra(EXTRA_ITEMS, ArrayList(items) as Serializable)
                putExtra(EXTRA_POSITION, position)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        @Suppress("DEPRECATION", "UNCHECKED_CAST")
        items = intent.getSerializableExtra(EXTRA_ITEMS) as? ArrayList<Item> ?: emptyList()
        
        // Restore position from saved state, or use intent extra
        currentPosition = savedInstanceState?.getInt(KEY_CURRENT_POSITION) 
            ?: intent.getIntExtra(EXTRA_POSITION, 0)

        setupViewPager()
    }

    private fun setupViewPager() {
        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = MovieDetailPagerAdapter(this, items)
        viewPager.setCurrentItem(currentPosition, false)

        // Update action bar title when page changes
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
                supportActionBar?.title = items.getOrNull(position)?.title ?: getString(R.string.app_name)
            }
        })

        // Set initial title
        supportActionBar?.title = items.getOrNull(currentPosition)?.title ?: getString(R.string.app_name)
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save current ViewPager position
        outState.putInt(KEY_CURRENT_POSITION, currentPosition)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
