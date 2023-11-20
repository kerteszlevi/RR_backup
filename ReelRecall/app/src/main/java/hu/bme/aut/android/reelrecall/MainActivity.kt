package hu.bme.aut.android.reelrecall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import hu.bme.aut.android.reelrecall.data.MovieListDatabase
import hu.bme.aut.android.reelrecall.databinding.ActivityMainBinding
import hu.bme.aut.android.reelrecall.fragments.MoviesFragment
import hu.bme.aut.android.reelrecall.fragments.SeriesFragment
import hu.bme.aut.android.reelrecall.fragments.SettingsFragment
import hu.bme.aut.android.reelrecall.fragments.WatchlistFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: MovieListDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        openFragment(MoviesFragment())
        binding.bottomNavigationView.background = null
        database = MovieListDatabase.getDatabase(this)

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
    fun getDatabase(): MovieListDatabase{
        return database
    }
    fun calculateItemHeight(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        return screenHeight / 6
    }
}