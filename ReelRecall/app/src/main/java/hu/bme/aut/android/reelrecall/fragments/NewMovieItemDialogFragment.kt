package hu.bme.aut.android.reelrecall.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.reelrecall.R
import hu.bme.aut.android.reelrecall.data.MovieItem
import hu.bme.aut.android.reelrecall.databinding.DialogNewMovieItemBinding

class NewMovieItemDialogFragment : DialogFragment(){
    interface NewMovieItemDialogListener {
        fun onMovieItemCreated(newItem: MovieItem)
    }

    private lateinit var listener: NewMovieItemDialogListener

    private lateinit var binding: DialogNewMovieItemBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewMovieItemDialogListener
            ?: throw RuntimeException("Activity must implement the NewShoppingItemDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNewMovieItemBinding.inflate(LayoutInflater.from(context))

        return AlertDialog.Builder(requireContext())
            .setTitle("R.string.mew_movie_item")
            .setView(binding.root)
            .setPositiveButton("R.string.button_ok") { dialogInterface, i ->
                if (isValid()) {
                    listener.onMovieItemCreated(getMovieItem())
                }
            }
            .setNegativeButton("R.string.button_cancel", null)
            .create()
    }

    private fun isValid() = binding.etTitle.text.isNotEmpty()

    private fun getMovieItem() = MovieItem(
        title = binding.etTitle.text.toString(),
        rating = binding.etRating.text.toString().toIntOrNull() ?: 0,
        seen = binding.cbSeen.isChecked,
        description = binding.etDescription.text.toString(),
        playtime = binding.etPlaytime.text.toString().toIntOrNull() ?: 0
    )

    companion object {
        const val TAG = "NewMovieItemDialogFragment"
    }
}