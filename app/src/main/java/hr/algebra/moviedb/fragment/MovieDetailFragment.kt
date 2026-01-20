package hr.algebra.moviedb.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import hr.algebra.moviedb.R
import hr.algebra.moviedb.model.Item
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.File

private const val ARG_ITEM = "item"

class MovieDetailFragment : Fragment() {
    
    private var item: Item? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            @Suppress("DEPRECATION")
            item = it.getSerializable(ARG_ITEM) as? Item
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        item?.let { movie ->
            view.findViewById<TextView>(R.id.tvMovieTitle).text = movie.title
            view.findViewById<TextView>(R.id.tvMovieRating).text = "★ ${String.format("%.1f", movie.rating)}"
            view.findViewById<TextView>(R.id.tvMovieReleaseDate).text = movie.releaseDate
            view.findViewById<TextView>(R.id.tvMovieOverview).text = movie.overview
            
            val ivPoster = view.findViewById<ImageView>(R.id.ivMoviePoster)
            if (movie.posterPath.isNotEmpty()) {
                Picasso.get()
                    .load(File(movie.posterPath))
                    .error(R.drawable.movie_placeholder)
                    .transform(RoundedCornersTransformation(20, 0))
                    .into(ivPoster)
            } else {
                ivPoster.setImageResource(R.drawable.movie_placeholder)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(item: Item) = MovieDetailFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_ITEM, item)
            }
        }
    }
}
