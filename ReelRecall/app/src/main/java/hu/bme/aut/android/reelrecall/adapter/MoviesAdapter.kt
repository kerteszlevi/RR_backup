package hu.bme.aut.android.reelrecall.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.reelrecall.data.MovieItem
import hu.bme.aut.android.reelrecall.databinding.ItemMovieListBinding

class MovieAdapter(private val listener: MovieItemClickListener) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>(){

    private val items = mutableListOf<MovieItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MovieViewHolder(
        ItemMovieListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int){
        var movieItem = items[position]

        holder.binding.tvTitle.text = movieItem.title
        holder.binding.tvRating.text = movieItem.rating.toString()
    }

    @DrawableRes()
    private fun getImageResource(id: Int): Int{
        //TODO: implement
        return 0
    }

    override fun getItemCount(): Int = items.size

    fun addItem(item: MovieItem){
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun update(items: List<MovieItem>){
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }
    fun removeItem(item: MovieItem){
        items.remove(item)
        notifyDataSetChanged()
    }
    interface MovieItemClickListener{
        fun onItemChanged(item: MovieItem)
    }

    inner class MovieViewHolder(val binding: ItemMovieListBinding) : RecyclerView.ViewHolder(binding.root)
}