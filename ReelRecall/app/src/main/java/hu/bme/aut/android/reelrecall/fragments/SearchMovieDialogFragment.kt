package hu.bme.aut.android.reelrecall.fragments

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.reelrecall.R
import hu.bme.aut.android.reelrecall.adapter.MovieSearchAdapter
import hu.bme.aut.android.reelrecall.data.MovieResponse
import hu.bme.aut.android.reelrecall.databinding.DialogSearchMovieBinding
import com.google.gson.Gson
import hu.bme.aut.android.reelrecall.MainActivity
import hu.bme.aut.android.reelrecall.data.MovieItem
import hu.bme.aut.android.reelrecall.fragments.EditMovieDialogFragment.Companion.TAG
import hu.bme.aut.android.reelrecall.timing.MutableReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.util.UUID
import kotlin.concurrent.thread

class SearchMovieDialogFragment : DialogFragment() {
    private lateinit var binding: DialogSearchMovieBinding
    private lateinit var adapter: MovieSearchAdapter
    var onMovieAddedListener: OnMovieAddedListener? = null
    interface OnMovieAddedListener {
        fun onMovieAdded(movieItem: MovieItem)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogSearchMovieBinding.inflate(LayoutInflater.from(context))

        setupRecyclerView()
        setupSearchBar()
        setupManualAddButton()

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.search_movie)
            .setView(binding.root)
            .create()
    }

    private fun setupRecyclerView() {
        adapter = MovieSearchAdapter()
        adapter.onItemClickListener = object : MovieSearchAdapter.OnItemClickListener {
            override fun onItemClicked(movieItem: MovieItem) {
                addMovieToDatabase(movieItem)
                dismiss()
            }
        }
        binding.rvSearchResults.layoutManager = LinearLayoutManager(context)
        binding.rvSearchResults.adapter = adapter
    }
    private fun addMovieToDatabase(movieItem: MovieItem) {

        CoroutineScope(Dispatchers.IO).launch {
            val database = (activity as MainActivity).getDatabase()
            val posterUrl = "https://image.tmdb.org/t/p/w500" + movieItem.posterUrl
            Log.d(TAG, "Poster URL: $posterUrl")

            // gettin context from main thread
            val context = withContext(Dispatchers.Main) { requireContext() }

            // passing contetxt to downloadimage
            val downloadJob = async { downloadImage(posterUrl, context.filesDir) }
            val posterUri = downloadJob.await()

            movieItem.coverUri = posterUri
            Log.d(TAG, "Poster URI: ${movieItem.coverUri}")

            database.movieItemDao().insert(movieItem)

            withContext(Dispatchers.Main) {
                onMovieAddedListener?.onMovieAdded(movieItem)
            }
        }
    }

    private suspend fun downloadImage(url: String, directory: File): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = URL(url).openStream()
                val posterFile = File(directory, UUID.randomUUID().toString() + ".jpg")
                val outputStream = FileOutputStream(posterFile)

                inputStream.copyTo(outputStream)

                Log.d("SearchMovieDialogFragment", "File created at: ${posterFile.absolutePath}")
                Uri.fromFile(posterFile)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun setupSearchBar() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = MutableReference<Runnable?>(null)

        binding.etSearchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // előző runnable törlése
                runnable.value?.let { handler.removeCallbacks(it) }

                // új runnable létrehozása
                runnable.value = Runnable {
                    //keresés végrehajtása
                    searchMovies(s.toString())
                }

                val currentRunnable = runnable.value

                // új runnable kiírása 500ms-es delay-el
                currentRunnable?.let { handler.postDelayed(it, 500) }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupManualAddButton() {
        binding.btnAddManually.setOnClickListener {
            dismiss()
            NewMovieItemDialogFragment().show(parentFragmentManager, NewMovieItemDialogFragment.TAG)
        }
    }

    private fun searchMovies(query: String) {
        val url = "https://api.themoviedb.org/3/search/movie?api_key=416c5cd6f0e70f66aaf1e23bdec8c364&query=$query"

        val request = Request.Builder()
            .url(url)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    //sikertelen kérés esetén toast error kiírása
                    Toast.makeText(activity, "Network error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val gson = Gson()
                val movieResponse = gson.fromJson(body, MovieResponse::class.java)

                activity?.runOnUiThread {
                    adapter.update(movieResponse.results)
                }
            }
        })
    }

    companion object {
        const val TAG = "SearchMovieDialogFragment"
    }
}