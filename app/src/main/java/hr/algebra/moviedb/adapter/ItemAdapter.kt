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
import hr.algebra.moviedb.R
import hr.algebra.moviedb.model.Item
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.File

class ItemAdapter(
    private val context: Context,
    private val items: MutableList<Item>
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>(){

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
        holder.bind(item)

        holder.itemView.setOnClickListener {
            // TODO
        }

        holder.itemView.setOnLongClickListener {
            deleteItem(position)
            true
        }

    }

    // "content://hr.algebra.moviedb.provider/items
    //"content://hr.algebra.moviedb.provider/items/22

    private fun deleteItem(position: Int) {
        val item = items[position]
        context.contentResolver.delete(
            ContentUris.withAppendedId(MOVIE_PROVIDER_CONTENT_URI, item._id!!),
            null,
            null
        )
        File(item.picturePath).delete()
        items.removeAt(position)
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.count()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val tvItem = itemView.findViewById<TextView>(R.id.tvItem)
        private val ivItem = itemView.findViewById<ImageView>(R.id.ivItem)

        fun bind(item: Item){
            tvItem.text = item.title
            Picasso.get()
                .load(File(item.picturePath))
                .error(R.drawable.movie_placeholder)
                .transform(RoundedCornersTransformation(50, 5))
                .into(ivItem)
        }

    }
}