package hu.bme.aut.android.reelrecall.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import hu.bme.aut.android.reelrecall.MainActivity
import hu.bme.aut.android.reelrecall.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.gson.reflect.TypeToken
import hu.bme.aut.android.reelrecall.data.MovieItem
import kotlinx.coroutines.async
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.net.URL
import java.util.UUID


class SettingsFragment : Fragment() {
    private val createDocument = registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->

        if (uri != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val database = (activity as MainActivity).getDatabase()
                val movies = database.movieItemDao().getAll()

                val gson = Gson()
                val moviesJson = gson.toJson(movies)

                withContext(Dispatchers.Main) {
                    val outputStream = requireContext().contentResolver.openOutputStream(uri)
                    val writer = OutputStreamWriter(outputStream)
                    writer.use {
                        it.write(moviesJson)
                    }
                }
            }
        }
    }
    private val openDocument = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        // handling uri
        if (uri != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val database = (activity as MainActivity).getDatabase()

                withContext(Dispatchers.Main) {
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    val reader = InputStreamReader(inputStream)
                    reader.use {
                        val gson = Gson()
                        val movieType = object : TypeToken<List<MovieItem>>() {}.type
                        val movies = gson.fromJson<List<MovieItem>>(it, movieType)

                        CoroutineScope(Dispatchers.IO).launch {
                            movies.forEach { movie ->
                                // check whether the movie is in the db
                                val existingMovie = database.movieItemDao().getById(movie.id_tmdb)

                                if (existingMovie == null) {
                                    // if the movie is not in the db insert it
                                    database.movieItemDao().insert(movie)

                                    // if the movie has a tmdb id download the poster
                                    if (movie.id_tmdb != null) {
                                        val posterUrl = "https://image.tmdb.org/t/p/w500" + movie.posterUrl
                                        val context = withContext(Dispatchers.Main) { requireContext() }
                                        val downloadJob = async { downloadImage(posterUrl, context.filesDir) }
                                        val posterUri = downloadJob.await()

                                        movie.coverUri = posterUri
                                        database.movieItemDao().update(movie)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnImport: Button = view.findViewById(R.id.btnImport)
        btnImport.setOnClickListener {
            openDocument.launch(arrayOf("application/json"))
        }

        val btnExport: Button = view.findViewById(R.id.btnExport)
        btnExport.setOnClickListener {
            createDocument.launch("movies.json")
        }
        val btnClearDatabase: Button = view.findViewById(R.id.btnClearDatabase)
        btnClearDatabase.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Movie")
                .setMessage("Are you sure you want to clear the database?")
                .setPositiveButton("Yes") { _, _ ->
                    clearDatabase()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }
    private fun clearDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val database = (activity as MainActivity).getDatabase()
            database.clearAllTables()

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Database cleared", Toast.LENGTH_SHORT).show()
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
                val uri = Uri.fromFile(posterFile)
                // send a broadcast when the image is downloaded
                val intent = Intent("hu.bme.aut.android.reelrecall.IMAGE_DOWNLOADED")
                requireActivity().sendBroadcast(intent)

                uri
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}