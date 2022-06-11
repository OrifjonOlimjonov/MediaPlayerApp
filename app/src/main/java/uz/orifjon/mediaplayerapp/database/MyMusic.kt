package uz.orifjon.mediaplayerapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class MyMusic(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    @ColumnInfo(name = "path")
    val aPath:String,
    @ColumnInfo(name = "name")
    val aName :String,
    @ColumnInfo(name = "album")
    val aAlbum:String,
    @ColumnInfo(name = "artist")
    val aArtist:String,
    val duration:Long
):Serializable