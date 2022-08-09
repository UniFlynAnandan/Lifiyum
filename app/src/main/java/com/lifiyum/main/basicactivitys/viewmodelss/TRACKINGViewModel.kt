package com.lifiyum.main.basicactivitys.viewmodelss

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifiyum.main.db.Run
import com.lifiyum.main.db.UserInfo
import com.lifiyum.main.other.Constants
import com.lifiyum.main.other.SortType
import com.lifiyum.main.other.Sortable
import com.lifiyum.main.other.TrackingUtility
import com.lifiyum.main.repositories.MainRepository
import com.lifiyum.main.repositories.TrackingRepository
import com.lifiyum.main.servicess.TrackingService
import com.uniflyn.uniflyn_runnerapp.utils.StepCounter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class TRACKINGViewModel
@Inject constructor(
    private val mainRepository: MainRepository,
    val userInfo: UserInfo

) : ViewModel(), Sortable {

    val isTracking = TrackingRepository.isTracking
    val pathPoints = TrackingRepository.pathPoints
    /*val pathPointAll=TrackingRepository.pathPonitAll*/
    val currentTimeInMillis = TrackingRepository.timeRunInMillis
    val distanceInMeters = TrackingRepository.distanceInMeters
    val caloriesBurned = TrackingRepository.caloriesBurned
    val liveSteps=TrackingRepository.liveSteps
    val trackingStatus=TrackingRepository.trackingStatus
    val avgSpeed= TrackingRepository.AvgSpeed

    val targetType = TrackingRepository.targetType
    val progress = TrackingRepository.progress
    val trackingaltitude= TrackingRepository.trackingaltitude

    override val runsSortedByDate = mainRepository.getAllRunsSortedByDate()
    override val runsSortedByTime = mainRepository.getAllRunsSortedByTimeInMillis()
    override val runsSortedByAvgSpeed = mainRepository.getAllRunsSortedByAvgSpeed()
    //override val runsSortedByDistance = MediatorLiveData<List<Run>>()

     override val runsSortedByDistance = mainRepository.getAllRunsSortedByDistance()
    override val runsSortedByCaloriesBurned = mainRepository.getAllRunsSortedByCaloriesBurned()
    override val runs = MediatorLiveData<List<Run>>()
    override var sortType: SortType = SortType.DATE

    init {
        fillSources()
    }

    fun sendCommandToService(context: Context) {
        val action =
            if (isTracking.value!!) Constants.ACTION_PAUSE_SERVICE
            else Constants.ACTION_START_OR_RESUME_SERVICE
        Intent(context, TrackingService::class.java).also {
            it.action = action
            context.startService(it)
        }
    }

    fun setCancelCommand(context: Context) {
        Intent(context, TrackingService::class.java).also {
            it.action = Constants.ACTION_STOP_SERVICE
            context.startService(it)
        }
    }









    fun processRun(context: Context, bitmap: Bitmap,obj:String,activity_type :String, speedpaceData:String) {
        val dateTimestamp = Calendar.getInstance().timeInMillis
        val distance = distanceInMeters.value!!
        val avgSpeed = round(
            (distance / 1000f) / (currentTimeInMillis.value!! / 1000f / 60 / 60) * 10
        ) / 10f
        val run = Run(
            bitmap,
            dateTimestamp,
            avgSpeed,
            distance ,
            currentTimeInMillis.value!!,
            caloriesBurned.value!!,
            obj,
            speedpaceData,
            activity_type,
            liveSteps.value!!
        )
        viewModelScope.launch {
            setCancelCommand(context)
            mainRepository.insertRun(run)
        }
    }
    fun deleteRun(run: Run) {
        viewModelScope.launch {
            mainRepository.deleteRun(run)
        }
    }
    fun restoreDeletedRun(run: Run) {
        viewModelScope.launch {
            mainRepository.insertRun(run)
        }
    }

   /* fun getSortbydistance(type:String): MediatorLiveData<List<Run>> {
       runsSortedByDistance .addSource(mainRepository.getAllRunsSortedByDistance(type)) { result ->
           result?.let {
               runsSortedByDistance.value = it
           }
       }
        return runsSortedByDistance

    }*/

}


