package hu.bme.aut.android.reelrecall.data

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movieitem")
data class MovieItem(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true)@SerializedName("local_id") var id: Long? = null,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "description") @SerializedName("overview") var description: String,
    @ColumnInfo(name = "playtime") var playtime: Int,
    @ColumnInfo(name = "rating") var rating: Int,
    @ColumnInfo(name = "rating_tmdb") @SerializedName("vote_average")var rating_tmdb: Float? = null,
    @ColumnInfo(name = "seen") var seen: Boolean,
    @Transient @ColumnInfo(name = "coverUri") var coverUri: Uri? = null,
    @ColumnInfo(name = "id_tmdb") @SerializedName("id")var id_tmdb: Long? = null,
    @ColumnInfo(name = "posterUrl") @SerializedName("poster_path") var posterUrl: String? = null,
    @ColumnInfo(name = "watchlist") var watchlist: Boolean = false
)