package com.notes.itunes.Data.models


data class Songs( // Model Class for iTunes Api

    val resultCount: Int,
    val results: List<Tracks>

    ) {
    data class Tracks(
        val wrapperType: String,
        val kind: String,

        val collectionId: Int,
        val trackId: Int,

        val artistName: String,
        val trackName: String,

        val artistViewUrl: String,
        val trackViewUrl: String, // for view track in iTunes
        val previewUrl: String, // for listen

        val artworkUrl100: String,
        val trackTimeMillis: Long
    )
}

