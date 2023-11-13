package hu.bme.aut.android.reelrecall.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MovieItemDao {
    @Query("SELECT * FROM movieitem")
    fun getAll(): List<MovieItem>

    @Insert
    fun insert(movieItem: MovieItem): Long

    @Update
    fun update(movieItem: MovieItem)

    @Delete
    fun deleteItem(movieItem: MovieItem)
}