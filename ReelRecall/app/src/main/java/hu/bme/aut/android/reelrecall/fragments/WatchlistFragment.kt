package hu.bme.aut.android.reelrecall.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.reelrecall.MainActivity
import hu.bme.aut.android.reelrecall.R
import hu.bme.aut.android.reelrecall.adapter.MovieAdapter
import hu.bme.aut.android.reelrecall.data.MovieItem
import hu.bme.aut.android.reelrecall.data.MovieListDatabase
import hu.bme.aut.android.reelrecall.databinding.FragmentWatchlistBinding
import kotlin.concurrent.thread


class WatchlistFragment : Fragment(), MovieAdapter.MovieItemClickListener, EditMovieDialogFragment.EditMovieListener {
    private lateinit var binding: FragmentWatchlistBinding
    private lateinit var adapter: MovieAdapter
    private lateinit var database: MovieListDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        database = (activity as MainActivity).getDatabase()

        setupRecyclerView()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter(this, (activity as MainActivity).calculateItemHeight(), childFragmentManager)
        binding.rvWatchlistMovies.adapter = adapter
        binding.rvWatchlistMovies.layoutManager = LinearLayoutManager(activity)
        loadItemsInBackground()
    }

    private fun loadItemsInBackground() {
        thread {
            val items = database.movieItemDao().getAll().filter { it.watchlist }
            activity?.runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onItemChanged(item: MovieItem) {
        thread {
            item.watchlist = true
            database.movieItemDao().update(item)
            val items = database.movieItemDao().getAll().filter { it.watchlist }
            activity?.runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onMovieUpdated(updatedMovie: MovieItem) {
        thread {
            database.movieItemDao().update(updatedMovie)
            val items = database.movieItemDao().getAll().filter { it.watchlist }
            activity?.runOnUiThread {
                if (updatedMovie.watchlist) {
                    adapter.removeItem(updatedMovie)
                } else {
                    adapter.updateItem(updatedMovie)
                }
                adapter.update(items)
            }
        }
    }

    override fun onMovieDeleted(movie: MovieItem) {
        thread {
            database.movieItemDao().deleteItem(movie)
            val items = database.movieItemDao().getAll()
            activity?.runOnUiThread {
                adapter.removeItem(movie)
                adapter.update(items)
            }
        }
    }
}