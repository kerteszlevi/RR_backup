package hu.bme.aut.android.reelrecall.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(entities = [MovieItem::class], version = 1)
abstract class MovieListDatabase : RoomDatabase (){
    abstract fun movieItemDao(): MovieItemDao

    companion object{
        fun getDatabase(applicationContext: Context): MovieListDatabase {
            return Room.databaseBuilder(
                applicationContext,
                MovieListDatabase::class.java,
                "movie-list"
            ).build();
        }
    }
}