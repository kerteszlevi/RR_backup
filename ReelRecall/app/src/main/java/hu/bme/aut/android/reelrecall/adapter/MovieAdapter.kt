package hu.bme.aut.android.reelrecall.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hu.bme.aut.android.reelrecall.R
import hu.bme.aut.android.reelrecall.data.MovieItem
import hu.bme.aut.android.reelrecall.databinding.ItemMovieListBinding
import hu.bme.aut.android.reelrecall.fragments.EditMovieDialogFragment

class MovieAdapter(private val listener: MovieItemClickListener, private val itemHeight: Int, private val fragmentManager: FragmentManager) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>(){

    private val items = mutableListOf<MovieItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MovieViewHolder(
        ItemMovieListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int){
        val movieItem = items[position]

        //loading image from uri with glide
        movieItem.coverUri?.let { uri ->
            Glide.with(holder.binding.ivPoster.context)
                .load(uri)
                .placeholder(R.drawable.asset_missing_icon)
                .into(holder.binding.ivPoster)
        } ?: holder.binding.ivPoster.setImageResource(R.drawable.poster_placeholder)

        holder.binding.tvTitle.text = movieItem.title
        holder.binding.tvRating.text = if (movieItem.rating == 0) {
            holder.binding.tvRating.setTextColor(Color.RED)
            "Your Rating: Not Rated"
        } else {
            holder.binding.tvRating.setTextColor(Color.BLACK)
            "Your Rating: " + movieItem.rating.toString()
        }
        holder.binding.tvTmdbRating.text = if (movieItem.rating_tmdb == null) { "TMDB Rating: N/A"} else "TMDB Rating: " + movieItem.rating_tmdb.toString()

        // Set the list item height
        val layoutParams = holder.binding.rlPoster.layoutParams
        layoutParams.height = itemHeight

        //holder.binding.ivPoster.layoutParams = layoutParams
        holder.binding.rlPoster.layoutParams = layoutParams

        //editing
        holder.binding.ibEditMovie.setOnClickListener{
            EditMovieDialogFragment(movieItem).show(
                fragmentManager,
                EditMovieDialogFragment.TAG
            )
        }
        //checkmark
        holder.binding.ivSeenMark.visibility = if(movieItem.seen) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }

        //tmdb rating visibility
        holder.binding.tvTmdbRating.visibility = if(movieItem.rating_tmdb == null) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    override fun getItemCount(): Int = items.size

    fun addItem(item: MovieItem){
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    //sorting as well
    fun update(items: List<MovieItem>){
        this.items.clear()
        val sortedItems = items.sortedWith(compareByDescending<MovieItem> { it.rating }.thenBy { it.title })
        this.items.addAll(sortedItems)
        notifyDataSetChanged()
    }
    fun removeItem(item: MovieItem){
        items.remove(item)
        notifyItemRemoved(items.indexOf(item))
    }
    fun updateItem(item: MovieItem){
        val index = items.indexOfFirst { it.id == item.id }
        items[index] = item
        notifyItemChanged(index)
    }
    interface MovieItemClickListener{
        fun onItemChanged(item: MovieItem)
    }

    inner class MovieViewHolder(val binding: ItemMovieListBinding) : RecyclerView.ViewHolder(binding.root)
}