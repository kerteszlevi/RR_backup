package hu.bme.aut.android.reelrecall.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.reelrecall.R
import hu.bme.aut.android.reelrecall.data.MovieItem
import hu.bme.aut.android.reelrecall.databinding.DialogEditMovieBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class EditMovieDialogFragment(private val movie: MovieItem) : DialogFragment() {
    interface EditMovieListener {
        fun onMovieUpdated(updatedMovie: MovieItem)
        fun onMovieDeleted(deletedMovie: MovieItem)
    }

    private lateinit var listener: EditMovieListener
    private lateinit var binding: DialogEditMovieBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? EditMovieListener
            ?: throw RuntimeException("Fragment must implement the EditMovieListener interface!")
    }

    override fun onStart() {
        super.onStart()
        binding.sbRating.progress = movie.rating - 1 // Initialize the SeekBar with the current rating
    }

    /*private val getContent = registerForActivityResult(GetContent()) { uri: Uri? ->
        //the returned uri gets set as a poster on the fragment
        uri?.let {
            binding.ibAddCover.setImageURI(it)
            movie.coverUri = it
        }
    }*/

    private val getContent = registerForActivityResult(GetContent()) { uri: Uri? ->
        // The returned URI gets set as a poster on the fragment
        uri?.let {
            CoroutineScope(Dispatchers.IO).launch {
                val newUri = saveImageToInternalStorage(it)
                withContext(Dispatchers.Main) {
                    binding.ibAddCover.setImageURI(newUri)
                    movie.coverUri = newUri
                }
            }
        }
    }

    private suspend fun saveImageToInternalStorage(uri: Uri): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val posterFile = File(requireContext().filesDir, UUID.randomUUID().toString() + ".jpg")
                val outputStream = FileOutputStream(posterFile)

                inputStream?.copyTo(outputStream)

                Log.d("EditMovieDialogFragment", "File created at: ${posterFile.absolutePath}")

                Uri.fromFile(posterFile)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogEditMovieBinding.inflate(LayoutInflater.from(context))

        binding.etMovieTitle.setText(movie.title)
        binding.etMovieDescription.setText(movie.description)
        binding.etPlaytime.setText(movie.playtime.toString())
        //binding.sbRating.progress = movie.rating - 1
        binding.cbSeen.isChecked = movie.seen

        binding.btnDeleteMovie.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Movie")
                .setMessage("Are you sure you want to delete this movie?")
                .setPositiveButton("Yes") { _, _ ->
                    listener.onMovieDeleted(movie)
                    dismiss()
                }
                .setNegativeButton("No", null)
                .show()
        }
        binding.btnSaveChanges.setOnClickListener {
            if (isValid()) {
                listener.onMovieUpdated(getMovieItem())
                dismiss()
            }
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.ibAddCover.setOnClickListener{
            getContent.launch("image/*")
        }

        binding.sbRating.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //movie.rating = progress + 1
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // --
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // --
                movie.rating = binding.sbRating.progress + 1
            }
        })

        binding.cbWatchlist.isChecked = movie.watchlist
        binding.cbWatchlist.setOnCheckedChangeListener { _, isChecked ->
            movie.watchlist = isChecked
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.edit_movie_item)
            .setView(binding.root)
            /*.setPositiveButton(R.string.button_ok) { _, _ ->
                if (isValid()) {
                    listener.onMovieUpdated(getMovieItem())
                }
            }
            .setNegativeButton(R.string.button_cancel, null) */
            .create()
    }

    private fun isValid() = binding.etMovieTitle.text.isNotEmpty()

    private fun getMovieItem() = MovieItem(
        id = movie.id,
        title = binding.etMovieTitle.text.toString(),
        description = binding.etMovieDescription.text.toString(),
        playtime = binding.etPlaytime.text.toString().toIntOrNull() ?: 0,
        rating = movie.rating,
        rating_tmdb = movie.rating_tmdb,
        seen = binding.cbSeen.isChecked,
        coverUri = movie.coverUri,
        watchlist = binding.cbWatchlist.isChecked
    )

    companion object {
        const val TAG = "EditMovieDialogFragment"
    }
}