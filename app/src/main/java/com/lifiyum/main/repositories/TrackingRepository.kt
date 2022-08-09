package com.lifiyum.main.repositories

import android.app.Activity
import android.location.Location
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.lifiyum.main.db.UserInfo
import com.lifiyum.main.other.Constants
import com.lifiyum.main.other.TargetType
import com.uniflyn.uniflyn_runnerapp.utils.LocationProvider
import com.uniflyn.uniflyn_runnerapp.utils.StepCounter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.security.AccessController.getContext
import javax.inject.Inject
import kotlin.math.round

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class TrackingRepository @Inject constructor(
    private val userInfo: UserInfo
) {
    var isFirstRun = true
        private set
    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L
    var isCancelled = false
    private var lastCounted = 0

   fun initStartingValues() {
        pathPoints.value = mutableListOf()
        timeRunInMillis.postValue(0L)
        timeRunInSeconds.postValue(0L)
        lapTime = 0L
        timeRun = 0L
        timeStarted = 0L
        lastSecondTimestamp = 0L
    }

    private fun startTracking() {
        addEmptyPolyline()
        isTracking.value = true
        isCancelled = false
        targetType.value = userInfo.targetType
    }
    fun startRun(firstRun: Boolean = false) {
        startTracking()
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        isFirstRun = firstRun

        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                val progressValue = when (userInfo.targetType) {
                    TargetType.TIME -> timeRunInSeconds.value!! / 60f
                    TargetType.DISTANCE -> distanceInMeters.value!!.toFloat()
                    TargetType.CALORIES -> caloriesBurned.value!!.toFloat()
                    else -> 0f
                }
                val percentage = progressValue / userInfo.targetType.value * 100f
                isTargetReached.postValue(percentage >= 100)
                progress.postValue(percentage.toInt())
                userActcitvityType.postValue(userInfo.activityType)
                lapTime = System.currentTimeMillis() - timeStarted
                timeRunInMillis.postValue(timeRun + lapTime)

                if (timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    val lastPointIndex = pathPoints.value!!.last().lastIndex
                    if (lastPointIndex > lastCounted) {
                        var distance = 0f
                        for (i in lastCounted until lastPointIndex) {
                            val pos1 = pathPoints.value!!.last()[i]
                            val pos2 = pathPoints.value!!.last()[i + 1]

                            val result = FloatArray(1)
                            Location.distanceBetween(
                                pos1.latitude,
                                pos1.longitude,
                                pos2.latitude,
                                pos2.longitude,
                                result
                            )
                            distance += result[0]
                        }
                        lastCounted = lastPointIndex
                        val newDistance = distanceInMeters.value!! + distance.toInt()
                        Log.d("distancemeter",">>"+newDistance)
                        val newDistancetokm=newDistance/1000.0
                        Log.d("distancemeter",">> KM"+newDistancetokm)
                        distanceInMeters.postValue(newDistance.toLong())

                        if(userInfo.activityType.equals("run")) {

                            val avgSpeed = round(
                                 (timeRunInMillis.value!! / 1000f / 60 / 60) * 10)  /(newDistance / 1000f) / 10f

                            AvgSpeed.postValue(avgSpeed)


                        }
                        else
                        {
                            val avgSpeed = round(
                                (newDistance / 1000f) / (timeRunInMillis.value!! / 1000f / 60 / 60) * 10
                            ) / 10f

                            AvgSpeed.postValue(avgSpeed)
                        }

                        val metavalue :Float =calorie_Calculation(userInfo.activityType,1.5f)


                        caloriesBurned.postValue(((newDistance / 1000f) * userInfo.weight).toInt())
                    }
                     timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(Constants.TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    fun pauseRun() {
        isTracking.value = false
        isTimerEnabled = false
        lastCounted = 0
                   }
    fun cancelRun(){
        isCancelled = true
        isFirstRun = true
        timeRunInMillis.value = 0L // reset value for correct fragment observers income values
        distanceInMeters.value = 0L
        caloriesBurned.value = 0
        progress.value = 0
        isTargetReached.value = false
        pauseRun()
        initStartingValues()
    }

    fun addPoint(latLng: LatLng) {
        pathPoints.value?.last()?.add(latLng)
        pathPoints.postValue(pathPoints.value)
    }
    fun addSteps(currentSteps: Int)
    {
        liveSteps.postValue(currentSteps)
    }

    fun status_Actvity(status:Int)
    {
        trackingStatus.postValue(status)

    }

    fun add_Alltitude(altitute:Double)
    {
        trackingaltitude.postValue(altitute)

    }

    private fun addEmptyPolyline() {
        pathPoints.value?.add(mutableListOf())

    }

    private fun calorie_Calculation(type:String,newDistance :Float): Float {
       var metValue : Float =0f
      Log.d("metavalue",newDistance.toString())
        if(type.equals("walk"))
        {
            Log.d("metavalueif",newDistance.toString())
            if (newDistance.equals(1.0f.. 1.4f))
            {
                metValue = 2.0f

            }
            if (newDistance.equals(1.5f..2.7f))
            {
                metValue = 2.8f

            }
            if (newDistance.equals(2.8 to 3.2))
            {
                metValue = 3.5f

            }
            if (newDistance.equals(3.3 to 3.9))
            {
                metValue = 4.3F

            }
            if (newDistance.equals(4.0 to 5.0))
            {
                metValue = 5.0F

            }
            if (newDistance.equals(5.0 to 9.0))
            {
                metValue = 6.0F

            }
        }
        else if(type.equals("run"))
        {

        }else if(type.equals("ride"))
        {

        }

        return metValue
    }
    companion object {
        val isTracking = MutableLiveData(false)
        val pathPoints = MutableLiveData<Polylines>()
        val trackingaltitude = MutableLiveData<Double>()
        //val pathPonitAll=MutableLiveData<List<Polylines>>()
        val timeRunInMillis = MutableLiveData(0L)
        val timeRunInSeconds = MutableLiveData(0L)
        val distanceInMeters = MutableLiveData(0L)
        val caloriesBurned = MutableLiveData(0)
        val AvgSpeed = MutableLiveData<Float>()
        val liveSteps = MutableLiveData<Int>()
        val trackingStatus=MutableLiveData<Int>()
        val progress = MutableLiveData(0)
        var targetType = MutableLiveData(TargetType.NONE)
        var isTargetReached = MutableLiveData(false)
        var userActcitvityType=MutableLiveData<String>()
    }
}
