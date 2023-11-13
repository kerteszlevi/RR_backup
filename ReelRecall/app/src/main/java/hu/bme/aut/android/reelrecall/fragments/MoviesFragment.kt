package hu.bme.aut.android.reelrecall.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.reelrecall.R
import hu.bme.aut.android.reelrecall.adapter.MovieAdapter
import hu.bme.aut.android.reelrecall.data.MovieItem
import hu.bme.aut.android.reelrecall.data.MovieListDatabase
import hu.bme.aut.android.reelrecall.databinding.FragmentMoviesBinding
import kotlin.concurrent.thread

class MoviesFragment : Fragment(), MovieAdapter.MovieItemClickListener,
    NewMovieItemDialogFragment.NewMovieItemDialogListener{
    private lateinit var bindingMovies: FragmentMoviesBinding
    private lateinit var adapter: MovieAdapter
    private lateinit var database: MovieListDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        bindingMovies  = FragmentMoviesBinding.inflate(inflater, container, false)
        bindingMovies.floatingActionButton.setOnClickListener(
            View.OnClickListener {
                NewMovieItemDialogFragment().show(
                    requireActivity().supportFragmentManager,
                    NewMovieItemDialogFragment.TAG
                )
            }
        )
        initRecyclerView()
        return bindingMovies.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = MovieListDatabase.getDatabase(requireContext())


    }

    private fun initRecyclerView(){
        adapter = MovieAdapter(this)
        bindingMovies.rvMovies.adapter = adapter
        bindingMovies.rvMovies.layoutManager = LinearLayoutManager(requireContext())
        loadItemsInBackground()
    }

    private fun loadItemsInBackground(){
        thread {
            val items = database.movieItemDao().getAll()
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
            activity?.runOnUiThread {
                adapter.addItem(newItem)
            }
        }
    }
}