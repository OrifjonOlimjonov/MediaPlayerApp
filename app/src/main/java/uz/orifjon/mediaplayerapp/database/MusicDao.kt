package uz.orifjon.mediaplayerapp.database

import androidx.room.*

@Dao
interface MusicDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addMusic(music: MyMusic)

    @Update
    fun editMusic(music: MyMusic)

    @Delete
    fun deleteMusic(music: MyMusic)

    @Query("SELECT * FROM mymusic")
    fun listMusics():List<MyMusic>



}