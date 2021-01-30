package com.notes.itunes.Data.Database

import android.content.Context
import androidx.room.*
import com.notes.itunes.Data.models.Entity_tracks
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(entities = [Entity_tracks::class], version = 1, exportSchema = false)
@TypeConverters(ImageBitmapString::class)

// Database for Room Database This will five Instance for Room Database
abstract class Database_tracks: RoomDatabase() {
    abstract  fun tracksDao() : Dao_tracks

    companion object{
        @Volatile
        private var Instance: Database_tracks? = null

        @InternalCoroutinesApi
        fun getDataBaseInstance(context: Context): Database_tracks{

            val tempInstance = Instance
            if (tempInstance != null){
                return tempInstance
            }

            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Database_tracks::class.java,
                    "Tracks"
                ).build()

                Instance = instance
                return instance
            }

        }
    }


}