package com.notes.itunes.Data.models

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tracks")
data class Entity_tracks( // Entity Class for Room Database

    val wrapperType: String,
    val kind: String,

    val collectionId: Int,

    @ColumnInfo(name = "artistName") val artistName: String,
    @ColumnInfo(name = "trackName") val trackName: String,

    val artistViewUrl: String,
    val trackViewUrl: String, // for view track in iTunes
    val previewUrl: String, // for listen

    val artworkUrl100: Bitmap,
    val trackTimeMillis: Long
){
    @PrimaryKey(autoGenerate = true) // This is primary key
    var trackId: Int= 0 //because iTunes Api doesn't give us a unique track id  that's why i autoGenerate it i know this make duplicates
}