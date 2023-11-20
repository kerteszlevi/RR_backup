
package hu.bme.aut.android.reelrecall.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hu.bme.aut.android.reelrecall.data.MovieItem
import hu.bme.aut.android.reelrecall.databinding.ItemSearchResultBinding

class MovieSearchAdapter : RecyclerView.Adapter<MovieSearchAdapter.MovieViewHolder>() {
    interface OnItemClickListener {
        fun onItemClicked(movieItem: MovieItem)
    }

    var onItemClickListener: OnItemClickListener? = null

    private val items = mutableListOf<MovieItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvMovieTitle.text = item.title
        holder.binding.tvRating.text = item.rating_tmdb.toString()

        // load the movie poster using glide
        Glide.with(holder.binding.ivMoviePoster.context)
            .load("https://image.tmdb.org/t/p/w500"+item.posterUrl)
            .into(holder.binding.ivMoviePoster)
    }

    override fun getItemCount(): Int = items.size

    fun update(items: List<MovieItem>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(val binding: ItemSearchResultBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedMovieItem = items[position]
                    onItemClickListener?.onItemClicked(clickedMovieItem)
                }
            }
        }
    }
}