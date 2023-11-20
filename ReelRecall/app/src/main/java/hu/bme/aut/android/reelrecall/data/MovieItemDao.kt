package hu.bme.aut.android.reelrecall.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MovieItemDao {
    @Query("SELECT * FROM movieitem")
    fun getAll(): List<MovieItem>

    @Query("SELECT * FROM movieitem WHERE id_tmdb = :id")
    fun getById(id: Long?): MovieItem?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(movieItem: MovieItem): Long

    @Update
    fun update(movieItem: MovieItem)

    @Delete
    fun deleteItem(movieItem: MovieItem)
}