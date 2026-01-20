package hr.algebra.moviedb.adapter

import android.content.ContentUris
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hr.algebra.moviedb.MOVIE_PROVIDER_CONTENT_URI
import hr.algebra.moviedb.MovieDetailActivity
import hr.algebra.moviedb.R
import hr.algebra.moviedb.model.Item
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.File

class ItemAdapter(
    private val context: Context,
    private var items: MutableList<Item>,
    private var showImages: Boolean = true
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = items[position]
        holder.bind(item, showImages)

        holder.itemView.setOnClickListener {
            // Open movie detail with ViewPager
            val intent = MovieDetailActivity.newIntent(context, items.toList(), position)
            context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            deleteItem(position)
            true
        }
    }
    
    /**
     * Updates the adapter data and display settings.
     * Call this when settings change or data needs to be refreshed.
     */
    fun updateData(newItems: MutableList<Item>, newShowImages: Boolean) {
        items = newItems
        showImages = newShowImages
        notifyDataSetChanged()
    }

    private fun deleteItem(position: Int) {
        val item = items[position]
        context.contentResolver.delete(
            ContentUris.withAppendedId(MOVIE_PROVIDER_CONTENT_URI, item._id!!),
            null,
            null
        )
        // Delete cached poster file
        if (item.posterPath.isNotEmpty()) {
            File(item.posterPath).delete()
        }
        items.removeAt(position)
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.count()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvItem)
        private val tvRating = itemView.findViewById<TextView>(R.id.tvRating)
        private val ivPoster = itemView.findViewById<ImageView>(R.id.ivItem)

        fun bind(item: Item, showImages: Boolean) {
            tvTitle.text = item.title
            tvRating?.text = "★ ${String.format("%.1f", item.rating)}"
            
            // Show or hide image based on settings
            if (showImages) {
                ivPoster.visibility = View.VISIBLE
                if (item.posterPath.isNotEmpty()) {
                    Picasso.get()
                        .load(File(item.posterPath))
                        .error(R.drawable.movie_placeholder)
                        .transform(RoundedCornersTransformation(50, 5))
                        .into(ivPoster)
                } else {
                    ivPoster.setImageResource(R.drawable.movie_placeholder)
                }
            } else {
                ivPoster.visibility = View.GONE
            }
        }
    }
}