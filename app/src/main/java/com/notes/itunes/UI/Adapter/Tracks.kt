package com.notes.itunes.UI.Adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.notes.itunes.Data.models.Entity_tracks
import com.notes.itunes.Data.models.Songs
import com.notes.itunes.R


// Custom Adapter for RecyclerView
class Tracks(activity: Activity) : RecyclerView.Adapter<Tracks.MyViewHolder>() {

    lateinit var list : List<Songs.Tracks> // This is for iTunes Api
    lateinit var databaseList: List<Entity_tracks> // This is for Database
    val activity: Activity = activity


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(activity)

        val view: View = inflater.inflate(R.layout.track_card, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if(this.list.isEmpty()){ // When list is empty means connection is off or user open app 1st time and set from Database
            val track = databaseList[position]

            val trackName: String = track.trackName
            val trackTimeMillis: Long = track.trackTimeMillis

            val minutes = trackTimeMillis / 1000 / 60
            val seconds = trackTimeMillis / 1000 % 60
            val duration =  "$minutes:$seconds"

            holder.tv_trackName.text = trackName
            holder.tv_trackDuration.text = duration
            holder.iv_song.setImageBitmap(track.artworkUrl100)
        }
        else if (this.databaseList.isEmpty()){ // When databaseList is empty means connection is on and set from Api
            val track = list[position]

            val trackName: String = track.trackName
            val artworkUrl100: String = track.artworkUrl100
            val trackTimeMillis: Long = track.trackTimeMillis

            val minutes = trackTimeMillis / 1000 / 60
            val seconds = trackTimeMillis / 1000 % 60
            val duration =  "$minutes:$seconds"

            holder.tv_trackName.text = trackName
            holder.tv_trackDuration.text = duration

            Glide.with(activity)
                .load(artworkUrl100)
                .placeholder(R.drawable.music_place_holder)
                .into(holder.iv_song)
        }
    }

    override fun getItemCount(): Int {

        return if(list.isEmpty()) {
            databaseList.size
        } else {
            list.size
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iv_song = itemView.findViewById<ImageView>(R.id.iv_song)
        val tv_trackName = itemView.findViewById<TextView>(R.id.tv_trackName)
        val tv_trackDuration = itemView.findViewById<TextView>(R.id.tv_trackDuration)
    }

    fun setTracks(list: List<Songs.Tracks>){ // set Api List
        this.list = list
        this.databaseList = emptyList()
        notifyDataSetChanged()
    }

    fun setTracksFromDatabase(databaseList: List<Entity_tracks>){ // set Database List
        this.databaseList = databaseList
        this.list = emptyList()
        notifyDataSetChanged()
    }

}