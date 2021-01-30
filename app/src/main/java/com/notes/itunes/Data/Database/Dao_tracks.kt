package com.notes.itunes.Data.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.notes.itunes.Data.models.Entity_tracks

@Dao
interface Dao_tracks {

    // Dao for Room Database

    @Insert // Insert list in Database
    suspend fun saveTracks(tracks: List<Entity_tracks>)

    @Query("SELECT * FROM Tracks") // Get full List from Database
    fun getTracksFromDatabase():LiveData<List<Entity_tracks>>

    @Query("SELECT * FROM Tracks WHERE artistName LIKE :q") // Get Query list from Database
    fun getQueryList(q: String):LiveData<List<Entity_tracks>>

}