package com.notes.itunes.Data.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.notes.itunes.Data.models.Songs
import com.notes.itunes.Data.Api.JsonConvertor
import com.notes.itunes.Data.Database.Database_tracks
import com.notes.itunes.Data.models.Entity_tracks
import kotlinx.coroutines.InternalCoroutinesApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ITunesData() {  // Repository

    val Tracks = MutableLiveData<Songs>() // Tracks
    val Status = MutableLiveData<String>() // Status of Api 1= success and 0 for failed

    private  lateinit var Instance: ITunesData // Instance for our Repo


    fun  getInstance():ITunesData{  // Give Instance for repo
        if(!::Instance.isInitialized){
            Instance = ITunesData()
        }
        return Instance
    }

    // This will get list of Tracks from iTunes and set on Tracks and set status also
    fun getTracks(text: String) : MutableLiveData<Songs>{

        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val jsonConverter : JsonConvertor
        jsonConverter = retrofit.create(JsonConvertor::class.java)

        val call = jsonConverter.getTracks(text)

        call.enqueue(object : Callback<Songs>{
            override fun onResponse(call: Call<Songs>, response: Response<Songs>) {
                if (!response.isSuccessful){
                    Log.i("Track", "Error:    " + response.code())
                    Status.value = "0"
                    return
                }

                val apiData = response.body()

                if (apiData != null) {
                    Tracks.value = apiData
                    Status.value = "1"
                }

            }
            override fun onFailure(call: Call<Songs>, t: Throwable) {
                Log.i("Track", "Fail:    " + t.message)
                Status.value = "0"
            }
        })

        return Tracks;

    }

    @InternalCoroutinesApi
    suspend fun setTrackInDatabase(tracks: List<Entity_tracks>, context: Context){ // This will save Tracks Data in Database
        val tracksDao = Database_tracks.getDataBaseInstance(context).tracksDao()
        tracksDao.saveTracks(tracks)
    }

    @InternalCoroutinesApi
    suspend fun getTracksFromDatabase(context: Context):LiveData<List<Entity_tracks>>{ // This will Get full List from Database
        val trackDao = Database_tracks.getDataBaseInstance(context).tracksDao()
        return trackDao.getTracksFromDatabase()
    }

    @InternalCoroutinesApi
    suspend fun getQueryList(context: Context, q: String): LiveData<List<Entity_tracks>>{ // This will Get QueryList from Database
        val trackDao = Database_tracks.getDataBaseInstance(context).tracksDao()
        return trackDao.getQueryList(q)
    }
}


