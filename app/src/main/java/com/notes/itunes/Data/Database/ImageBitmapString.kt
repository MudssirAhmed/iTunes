package com.notes.itunes.Data.Database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import java.lang.Exception

// Convector TypeChange for storing Images
class ImageBitmapString {

    @TypeConverter
    public fun fromBitmap(bitmap: Bitmap): ByteArray{
        val outputStream = ByteArrayOutputStream ()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    public fun toBitmap(byteArray: ByteArray): Bitmap{
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}