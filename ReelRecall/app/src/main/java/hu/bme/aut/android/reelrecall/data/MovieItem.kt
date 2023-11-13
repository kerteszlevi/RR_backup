package hu.bme.aut.android.reelrecall.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
@Entity(tableName = "movieitem")
data class MovieItem(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "playtime") var playtime: Int,
    @ColumnInfo(name = "rating") var rating: Int,
    @ColumnInfo(name = "seen") var seen: Boolean,
)