package  com.lifiyum.main.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream


class Converters {
    @TypeConverter
    fun toBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    @TypeConverter
    fun fromBitmap(bmp: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }


//    @TypeConverter
//    fun restoreArrayList(listOfdDouble: Double): ArrayList<Double?>? {
//        return Gson().fromJson(listOfdDouble.toString(), object : TypeToken<ArrayList<Double?>?>() {}.type)
//    }
//
//    @TypeConverter
//    fun saveArrayList(listOfdDouble: Double): String? {
//        return Gson().toJson(listOfdDouble.toString())
//    }
}