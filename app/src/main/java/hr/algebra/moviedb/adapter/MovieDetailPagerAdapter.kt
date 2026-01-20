package hr.algebra.moviedb.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import hr.algebra.moviedb.fragment.MovieDetailFragment
import hr.algebra.moviedb.model.Item

class MovieDetailPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val items: List<Item>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment {
        return MovieDetailFragment.newInstance(items[position])
    }
}
