package hu.bme.aut.android.reelrecall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import hu.bme.aut.android.reelrecall.adapter.MovieAdapter
import hu.bme.aut.android.reelrecall.data.MovieItem
import hu.bme.aut.android.reelrecall.data.MovieListDatabase
import hu.bme.aut.android.reelrecall.databinding.ActivityMainBinding
import hu.bme.aut.android.reelrecall.databinding.FragmentMoviesBinding
import hu.bme.aut.android.reelrecall.fragments.MoviesFragment
import hu.bme.aut.android.reelrecall.fragments.NewMovieItemDialogFragment
import hu.bme.aut.android.reelrecall.fragments.SeriesFragment
import hu.bme.aut.android.reelrecall.fragments.SettingsFragment
import hu.bme.aut.android.reelrecall.fragments.WatchlistFragment
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), NewMovieItemDialogFragment.NewMovieItemDialogListener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        openFragment(SeriesFragment())
        binding.bottomNavigationView.background = null

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.movies -> openFragment(MoviesFragment())
                R.id.series -> openFragment(SeriesFragment())
                R.id.watchlist -> openFragment(WatchlistFragment())
                R.id.settings -> openFragment(SettingsFragment())
            }
            true
        }

    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
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