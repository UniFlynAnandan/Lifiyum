package com.lifiyum.main.servicess

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import com.lifiyum.main.R
import com.lifiyum.main.basicactivitys.ActivityProgress_Activity
import com.lifiyum.main.other.Constants.ACTION_FINISH_RUN
import com.lifiyum.main.other.Constants.ACTION_PAUSE_SERVICE
import com.lifiyum.main.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.lifiyum.main.other.Constants.ACTION_STOP_SERVICE
import com.lifiyum.main.other.Constants.FASTEST_LOCATION_INTERVAL
import com.lifiyum.main.other.Constants.LOCATION_UPDATE_INTERVAL
import com.lifiyum.main.other.Constants.NOTIFICATION_CHANNEL_ID
import com.lifiyum.main.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.lifiyum.main.other.Constants.NOTIFICATION_CHANNEL_TARGET_ID
import com.lifiyum.main.other.Constants.NOTIFICATION_CHANNEL_TARGET_NAME
import com.lifiyum.main.other.Constants.NOTIFICATION_ID
import com.lifiyum.main.other.Constants.NOTIFICATION_TARGET_ID
import com.lifiyum.main.other.Constants.REQUEST_CODE_ACTION_FINISH_RUN
import com.lifiyum.main.other.TargetType
import com.lifiyum.main.other.TrackingUtility
import com.lifiyum.main.repositories.TrackingRepository
import com.lifiyum.main.repositories.TrackingRepository.Companion.isTracking
import dagger.hilt.android.AndroidEntryPoint

import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class TrackingService : LifecycleService() , SensorEventListener,TextToSpeech.OnInitListener  {

    @Inject
    lateinit var trackingRepository: TrackingRepository

    private var tts: TextToSpeech? = null
    private var isAlreadyNotifiedAboutTargetReached = false
    /*private val client by lazy { LocationServices.getFusedLocationProviderClient(this) }
*/
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            if (isTracking.value!!) {
                p0?.locations?.forEach(::addPathPoint)
            } } }

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    @Named("baseNotificationBuilder")
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    @Inject
    @Named("targetReachedNotificationBuilder")
    lateinit var targetReachedNotificationBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()
        trackingRepository.initStartingValues()

        isTracking.observe(this, Observer {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        })

        TrackingRepository.isTargetReached.observe(this, Observer {
            if (it && !isAlreadyNotifiedAboutTargetReached) notifyTargetReached()
        })

        tts = TextToSpeech(this, this)
    }

    private fun addPathPoint(location: Location?) {
        location?.let {


            trackingRepository.addPoint(LatLng(it.latitude, it.longitude))
            trackingRepository.add_Alltitude(it.altitude)
        }
    }
        // Handled by EasyPermissions
    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtility.hasLocationPermissions(this)) {
                val request = LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                    smallestDisplacement=0.25F
                }

                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )

            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    Timber.d("Started or resumed service, first run: ${trackingRepository.isFirstRun}")
                    if (trackingRepository.isFirstRun) {
                        startForegroundService()
                        setupStepCounter()
                        trackingRepository.startRun(true)
                        trackingRepository.status_Actvity(0)
                    } else {
                        Timber.d("Resuming service...")
                        trackingRepository.startRun()

                        trackingRepository.status_Actvity(0)
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Paused service")
                    trackingRepository.pauseRun()
                    trackingRepository.status_Actvity(1)
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped service")
                    unloadStepCounter()
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("RestrictedApi")
    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationActionText =
            if (isTracking) getString(R.string.pause)
            else getString(R.string.resume)

        val pendingIntent = PendingIntent.getService(
            this,
            if (isTracking) 1 else 2,
            Intent(this, TrackingService::class.java).apply {
                action = if (isTracking) ACTION_PAUSE_SERVICE else ACTION_START_OR_RESUME_SERVICE
            },
            FLAG_UPDATE_CURRENT
        )
        baseNotificationBuilder.apply {
            mActions.clear()
            addAction(R.drawable.ic_pause_black, notificationActionText, pendingIntent)
        }
        if (!trackingRepository.isCancelled) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, baseNotificationBuilder.build())
        }
    }
    private fun startForegroundService() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        TrackingRepository.timeRunInSeconds.observe(this, Observer {
            if (!trackingRepository.isCancelled) {

                val time = TrackingUtility.getFormattedStopWatchTime(it * 1000L)
                var distance : Long = 0L
                distance= TrackingRepository.distanceInMeters.value!!
                val info = "${TrackingUtility.getFormattedDistance(distance)} km | " +
                        "${TrackingRepository.caloriesBurned.value} cal" +
                        if (TrackingRepository.targetType.value!! != TargetType.NONE) {
                            " | ${TrackingRepository.progress.value!!} % - ${getString(R.string.target).toLowerCase(
                                Locale.getDefault()
                            )}"
                        } else ""

                baseNotificationBuilder
                    .setContentTitle("${TrackingRepository.userActcitvityType.value?.toUpperCase()}  "+time)
                    .setContentText(info)
                notificationManager.notify(NOTIFICATION_ID, baseNotificationBuilder.build())
            }
        })


        TrackingRepository.progress.observe(this, Observer {
            val km = it
            Log.d("Progress",">>"+km)

            // binding.stepsCountTxt.text  = "$km"

            when (km) {
                24 ->speakOut("Twentyfive Percentage Completed  ")

                49->speakOut("Fifty Percentage Completed ")

                74->speakOut("Seventyfive  Percentage Completed ")

                99->speakOut("You Completed the Goal ,")

            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTargetReachedNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_TARGET_ID,
            NOTIFICATION_CHANNEL_TARGET_NAME,
            IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    @SuppressLint("RestrictedApi")
    private fun notifyTargetReached() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createTargetReachedNotificationChannel(notificationManager)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            REQUEST_CODE_ACTION_FINISH_RUN,
            Intent(this, ActivityProgress_Activity::class.java).apply {
                action = ACTION_FINISH_RUN
            },
            FLAG_UPDATE_CURRENT
        )

        targetReachedNotificationBuilder.addAction( R.drawable.ic_pause_black,getString(R.string.finish),pendingIntent )

        if (!trackingRepository.isCancelled) {
            notificationManager.cancelAll()
            notificationManager.notify( NOTIFICATION_TARGET_ID,
                targetReachedNotificationBuilder .setContentTitle("${TrackingRepository.userActcitvityType.value?.toUpperCase()} ").build()
            )
            isAlreadyNotifiedAboutTargetReached = true
        }
    }

    private fun killService() {
        trackingRepository.cancelRun()
        stopForeground(true)
        stopSelf()
        isAlreadyNotifiedAboutTargetReached = false
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    private val sensorManager by lazy {
        getSystemService(SENSOR_SERVICE) as SensorManager
    }

    private val stepCounterSensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    }
    private var initialSteps = -1
    fun setupStepCounter() {
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }
    override fun onSensorChanged(event: SensorEvent) {
        event.values.firstOrNull()?.toInt()?.let { newSteps ->
            if (initialSteps == -1) {
                initialSteps = newSteps
            }
            val currentSteps = newSteps - initialSteps
            trackingRepository.addSteps(currentSteps)
        }
    }

    fun unloadStepCounter() {
        if (stepCounterSensor != null) {
            sensorManager.unregisterListener(this)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit


    private fun speakOut(text: String) {

        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language not supported!")
            } else {

            }
        }
    }
}
