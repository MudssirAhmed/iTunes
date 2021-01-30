package com.notes.itunes.UI.viewModels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.notes.itunes.Data.models.Entity_tracks
import com.notes.itunes.Data.models.Songs
import com.notes.itunes.Data.repositories.ITunesData
import com.notes.itunes.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.lang.Exception


@InternalCoroutinesApi
class mainActivityViewModel(context: Context, q: String) : ViewModel() { // ViewModel

    var status = MutableLiveData<String>() // Api Status 0 or 1
    var tracks : MutableLiveData<Songs>? = null // Api List
    var databaseTracks : MutableList<Entity_tracks> = mutableListOf() // Database List

    lateinit var getTracksData: LiveData<List<Entity_tracks>> // Get Tracks Data from Api
    lateinit var getQueryTracksData: LiveData<List<Entity_tracks>> // Get Tracks Data form Database

    private val repo: ITunesData // Instance for Repo

    init {
        repo = ITunesData()
        viewModelScope.launch {
            getTracksData = repo.getTracksFromDatabase(context)
            getQueryTracksData = repo.getQueryList(context, q)
        }
    }

    fun init(text: String): LiveData<Songs>?{ // Get Tracks Data from Database

        lateinit var  miTunesRepo: ITunesData
        miTunesRepo = repo.getInstance()
        tracks = miTunesRepo.getTracks(text)
        status = miTunesRepo.Status

        return tracks

    }

    fun addTracksInDatabase(list: List<Songs.Tracks>, context: Context){ // Adding Tracks form Api to Database
        viewModelScope.launch(Dispatchers.IO) {

            for ( track in list){ // making Entity object

                val wrapperType: String = track.wrapperType
                val kind: String = track.kind

                val collectionId: Int = track.collectionId
                val trackId: Int = track.trackId

                val artistName: String = track.artistName
                val trackName: String = track.trackName

                val artistViewUrl: String = track.artistViewUrl
                val trackViewUrl: String = track.trackViewUrl
                var previewUrl: String = track.previewUrl


                val artworkUrl100: Bitmap = getBitmap(track.artworkUrl100, context)
                val trackTimeMillis: Long = track.trackTimeMillis

                try {
                    val t = Entity_tracks( wrapperType, kind, collectionId, artistName, trackName, artistViewUrl, trackViewUrl, previewUrl,
                        artworkUrl100, trackTimeMillis)

                    databaseTracks.add(t)
                }
                catch (e: Exception){
                    Log.i("Exception", "Error: $e.message")
                }

            }

            repo.setTrackInDatabase(databaseTracks, context)
        }
    }

    private suspend fun getBitmap(uri: String, context: Context) : Bitmap{  // This will convert image from uri to Bitmap i use coil liberary for it
        val loadeing = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(uri)
            .build()

        try { // When There is no image placeHolder is save
            val result = (loadeing.execute(request) as SuccessResult).drawable
            return (result as BitmapDrawable).bitmap

        }
        catch (e: Exception){
            return BitmapFactory.decodeResource(context.getResources(),  R.drawable.music_place_holder);
        }

    }

    fun getDataFromDataBase(context: Context){ // Geting List of Tracks from Database
        viewModelScope.launch(Dispatchers.IO) {
            getTracksData = repo.getTracksFromDatabase(context)
        }
    }

    fun getQueryList(context: Context, q: String){ // Getting QueryList from Database
        viewModelScope.launch(Dispatchers.IO) {
            getQueryTracksData = repo.getQueryList(context, q)
        }
    }
}