package com.notes.itunes.UI.View

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.notes.itunes.Data.models.Entity_tracks
import com.notes.itunes.Data.models.Songs
import com.notes.itunes.R
import com.notes.itunes.UI.Adapter.Tracks
import com.notes.itunes.UI.viewModels.mainActivityViewModel
import kotlinx.coroutines.InternalCoroutinesApi


class MainActivity : AppCompatActivity() {

    @InternalCoroutinesApi
    private lateinit var mViewModel: mainActivityViewModel  // Main ViewModel
    private lateinit var rvTracks: RecyclerView  // RecyclerView
    private lateinit var trackAdapter : Tracks // Custom Adapter
    private lateinit var progressBar: ProgressBar // progress Bar

    private lateinit var searchView: SearchView

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Init Components
        searchView = findViewById(R.id.search)
        rvTracks = findViewById(R.id.rv_tracks)
        progressBar = findViewById(R.id.progressBar)


        // Init QueryTextListener On Search View
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(qString: String): Boolean {
                getQueryList(qString) // This will filter data from Room Database
                return true
            }

            override fun onQueryTextSubmit(qString: String): Boolean {
                progressBar.visibility = VISIBLE // Show progress
                searchView.clearFocus()

                if(isNetworkAvailable()){ // When Network Connection is Open it getTracks from iTunes Api
                    searchResults(qString)
                }
                else{ // else search in Database if there is
                    getQueryList(qString)
                }

                return true
            }
        })

        mViewModel = mainActivityViewModel(this, "")

        getDataFromDatabase()  // Getting Data From Database if there is

    }

    @InternalCoroutinesApi
    private fun getDataFromDatabase(){  // This will Get Tracks From Database and pass to setDataOnRecyclerViewFromDatabase() to showing Data

        mViewModel.getDataFromDataBase(this)

        // Observer For Tracks in Database
        mViewModel.getTracksData.observe(this, Observer {
            setDataOnRecyclerViewFromDatabase(it)
        })
    }

    @InternalCoroutinesApi
    private fun setDataOnRecyclerViewFromDatabase(list: List<Entity_tracks>){ // This will set data  on recyclerView and pass the list to Adapter
        trackAdapter = Tracks(this)
        trackAdapter.setTracksFromDatabase(list)
        rvTracks.adapter = trackAdapter
        rvTracks.layoutManager = GridLayoutManager(this, 3);
        progressBar.visibility = INVISIBLE
    }

    @InternalCoroutinesApi
    fun searchResults(text: String){  // This will search in iTunes Api

        mViewModel.init(text)!!.observe(this, Observer { it ->
            val result = it.results
            mViewModel.status.observe(this, Observer {

                if (it == "1")
                    setDataOnRecyclerViewFromApi(result) // pass track list to setDataOnRecyclerViewFromApi()
                else {
                    progressBar.visibility = INVISIBLE // if connection loss or error occurred hide progressbar
                }

            })
        })

    }

    @InternalCoroutinesApi
    private fun setDataOnRecyclerViewFromApi(list: List<Songs.Tracks>){ // This will set data  on recyclerView and pass the list to Adapter
        trackAdapter = Tracks(this)
        trackAdapter.setTracks(list)
        rvTracks.adapter = trackAdapter
        rvTracks.layoutManager = GridLayoutManager(this, 3);
        progressBar.visibility = INVISIBLE
        saveTracksInDataBase(list) // When successfully list retrieve from Api then save it on Room Database
    }

    @InternalCoroutinesApi
    private fun saveTracksInDataBase(tracks: List<Songs.Tracks>){ // save Data
        mViewModel.addTracksInDatabase(tracks, this)
    }

    @InternalCoroutinesApi
    fun getQueryList(q: String){ // Getting Query list from database
        mViewModel.getDataFromDataBase(this)
        mViewModel.getTracksData.observe(this, Observer {
            filterList(it, q)
        })

    }

    @InternalCoroutinesApi
    private fun filterList(list: List<Entity_tracks>, q: String){ // Filter List
        val filterList: MutableList<Entity_tracks> = mutableListOf()

        for(track in list){
            val trackName = track.trackName
            val artistName = track.artistName


            if (trackName.toLowerCase().contains(q.toLowerCase()) || artistName.toLowerCase().contains(
                    q.toLowerCase()
                )){
                Log.i("Query", "filterlist: $trackName  $artistName")
                filterList.add(track)
            }
        }

        Log.i("Query", "filterlist Size: " + filterList.size)

        setDataOnRecyclerViewFromDatabase(filterList)
    }

    private fun isNetworkAvailable(): Boolean { // Check Network Connection
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

}