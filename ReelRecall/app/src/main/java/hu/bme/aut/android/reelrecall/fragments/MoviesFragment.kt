package hu.bme.aut.android.reelrecall.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.reelrecall.MainActivity
import hu.bme.aut.android.reelrecall.adapter.MovieAdapter
import hu.bme.aut.android.reelrecall.adapter.MovieSearchAdapter
import hu.bme.aut.android.reelrecall.data.MovieItem
import hu.bme.aut.android.reelrecall.data.MovieListDatabase
import hu.bme.aut.android.reelrecall.databinding.FragmentMoviesBinding
import kotlin.concurrent.thread

class MoviesFragment : Fragment(), MovieAdapter.MovieItemClickListener,
    NewMovieItemDialogFragment.NewMovieItemDialogListener, EditMovieDialogFragment.EditMovieListener{
    private lateinit var bindingMovies: FragmentMoviesBinding
    private lateinit var adapter: MovieAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var database: MovieListDatabase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mainActivity = activity as MainActivity
        database = mainActivity.getDatabase()

        bindingMovies  = FragmentMoviesBinding.inflate(inflater, container, false)
        bindingMovies.floatingActionButton.setOnClickListener{
            val searchMovieDialogFragment = SearchMovieDialogFragment()
            searchMovieDialogFragment.onMovieAddedListener = object : SearchMovieDialogFragment.OnMovieAddedListener {
                override fun onMovieAdded(movieItem: MovieItem) {
                    thread {
                        val items = database.movieItemDao().getAll()
                        activity?.runOnUiThread {
                            adapter.addItem(movieItem)
                            adapter.update(items)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
            searchMovieDialogFragment.show(childFragmentManager, SearchMovieDialogFragment.TAG)
        }

        initRecyclerView()
        return bindingMovies.root
    }

    private fun initRecyclerView(){
        adapter = MovieAdapter(this, mainActivity.calculateItemHeight(), childFragmentManager)
        bindingMovies.rvMovies.adapter = adapter
        bindingMovies.rvMovies.layoutManager = LinearLayoutManager(mainActivity)
        loadItemsInBackground()
    }

    private fun loadItemsInBackground(){
        thread {
            val items = database.movieItemDao().getAll().filter { !it.watchlist }
            activity?.runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onItemChanged(item: MovieItem) {
        thread {
            database.movieItemDao().update(item)
            Log.d("MoviesFragment", "Item updated: $item")
        }
    }

    override fun onMovieItemCreated(newItem: MovieItem) {
        thread{
            val insertId = database.movieItemDao().insert(newItem)
            newItem.id = insertId
            val items = database.movieItemDao().getAll()
            activity?.runOnUiThread {
                adapter.addItem(newItem)
                adapter.update(items)
            }
        }
    }

    override fun onMovieUpdated(updatedMovie: MovieItem) {
        thread {
            database.movieItemDao().update(updatedMovie)
            val items = database.movieItemDao().getAll().filter { !it.watchlist }
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

    private val imageDownloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            loadItemsInBackground()
        }
    }
    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter("hu.bme.aut.android.reelrecall.IMAGE_DOWNLOADED")
        requireActivity().registerReceiver(imageDownloadReceiver, intentFilter)
    }
    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(imageDownloadReceiver)
    }
}