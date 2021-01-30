package com.notes.itunes.Data.Api

import com.notes.itunes.Data.models.Songs
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface JsonConvertor {

    // Get request to iTunes it get term= artistName/trackName and return list of Songs
    @GET("search")
    fun getTracks(
        @Query("term") term :String
    ) : Call<Songs>
}