package  com.lifiyum.main.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "running_table")
data class Run(var img: Bitmap? = null,
               var timestamp: Long = 0L,
               var avgSpeedInKMH: Float = 0f,
               var distanceInMeters: Long = 0L,
               var timeInMillis: Long = 0L,
               var caloriesBurned: Int = 0,
               var elevation: String=" ",
               var speedandpaceData: String=" ",
               var activityTYPE: String=" ",
               var walkingSteps: Int = 0){
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
