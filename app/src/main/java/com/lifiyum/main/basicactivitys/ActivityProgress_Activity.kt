package com.lifiyum.main.basicactivitys

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.lifiyum.main.R
import com.lifiyum.main.adapters.ImageAdapter




import com.lifiyum.main.basicactivitys.viewmodelss.TRACKINGViewModel
import com.lifiyum.main.databinding.ActivityProgressBinding


import com.lifiyum.main.other.Constants


import com.lifiyum.main.other.Constants.MAP_ZOOM
import com.lifiyum.main.other.Constants.POLYLINE_COLOR
import com.lifiyum.main.other.Constants.POLYLINE_WIDTH
import com.lifiyum.main.other.MapLifecycleObserver
import com.lifiyum.main.other.TargetType
import com.lifiyum.main.other.TrackingUtility
import com.lifiyum.main.repositories.TrackingRepository.Companion.pathPoints
import com.lifiyum.main.repositories.TrackingRepository.Companion.trackingStatus
import com.uniflyn.uniflyn_runnerapp.utils.StepCounter
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.lang.Math.round
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*


import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ActivityProgress_Activity : AppCompatActivity(),EasyPermissions.PermissionCallbacks  {

 //  private lateinit var map: GoogleMap
    private lateinit var binding : ActivityProgressBinding

    private lateinit var  context: Context;
    private val trackingViewModel: TRACKINGViewModel by viewModels()

    private var map: GoogleMap? = null
    private var mapView: MapView? = null
    private lateinit var mapLifecycleObserver: MapLifecycleObserver

    private var image_List : ArrayList<Uri?> = ArrayList()

    @set:Inject
    var weight = 80f
    private var distance = 0f
    private var avgSpeed = 0
    lateinit var type_value : String
    lateinit var goaltype : String
    lateinit var targetValue: String
    private var isTracking = false
    private var curTimeInSeconds = 0L
    private var curTimeInMillis = 0L
    private var tts: TextToSpeech? = null
    private var menu: Menu? = null
    private var mImg_Adapter: ImageAdapter? = null
    var  isStartPoint :Boolean = true
    var startPoint : LatLng? = null
    private var elevationList : ArrayList<Double> = ArrayList()

    private var avgSpeedlist : ArrayList<Double> = ArrayList()
    lateinit var activity_Type:String
   //val elevationList :ArrayList<Double> = TODO()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(getSupportActionBar()!=null){
            getSupportActionBar()?.hide();
        }
        binding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context=this

        mapView = binding.mapView
        requestPermissions()

        mapLifecycleObserver = MapLifecycleObserver(mapView, lifecycle)
        val bundle: Bundle? = intent.extras
        type_value   = bundle?.getString("type").toString()
        goaltype = bundle?.getString("goaltype").toString()
        targetValue=bundle?.getString("targetvalue").toString()
        Log.d("value is"," >>>>"+type_value)
        mapView?.let { mapView ->
            mapView.onCreate(savedInstanceState)
            mapView.getMapAsync {
                map = it
                addAllPolylines()
                /* if (args.isFinishActionFired) {
                     finishRun()
                 }*/
            }
        }
       // subscribeToObservers()
        if (type_value != null) {
            init(type_value,goaltype,targetValue)
        }
        subscribeToObservers()
        init(type_value,goaltype,targetValue)
    }

    @SuppressLint("MissingPermission")
    fun init(type:String?, goaltype:String?, targetValue:String?)
    {
        toggleRun()
        setup_Img_Adapter()

        if(type.equals("run"))
        {
            binding.statusIcon.setImageResource(R.drawable.ic_run)
            binding.peaceTitleTxt.setText("Distance")
         //+   binding.stepsCountTxt.setText("1.50 km")
            binding.speedTitleTxt.setText("Avg Pace")
          //  binding.speedCountTxt.setText("09:57/km")
        }
        else if(type.equals("walk")) {
            binding.statusIcon.setImageResource(R.drawable.ic_walk)
            binding.speedTitleTxt.setText("Distance")
           // binding.speedCountTxt.setText("5.4 km")
        }else if (type.equals("ride"))
        {
            binding.statusIcon.setImageResource(R.drawable.ic_cycling)
            binding.peaceTitleTxt.setText("Distance")
           // binding.stepsCountTxt.setText("1.50 km")
            binding.speedTitleTxt.setText("Avg Speed")
        }
        if(goaltype.equals("DISTANCE") && !targetValue.equals("0"))
        {
            binding.txtSetgoal.visibility=View.VISIBLE
            binding.txtSetgoal.setText("Distance Goal")
            binding.progressSetp.visibility=View.VISIBLE
        }
        else if(goaltype.equals("TIME")&&!targetValue.equals("0"))
        {
            binding.txtSetgoal.visibility=View.VISIBLE
            binding.txtSetgoal.setText("Time Goal")
            binding.progressSetp.visibility=View.VISIBLE
        }
        else if(goaltype.equals("CALORIES")&& !targetValue.equals("0"))
        {
            binding.txtSetgoal.visibility=View.VISIBLE
            binding.txtSetgoal.setText("Calories Goal")
            binding.progressSetp.visibility=View.VISIBLE
        }
        else if(goaltype.equals("NONE"))
        {
            binding.txtSetgoal.setText(" ")
            binding.progressSetp.visibility=View.GONE
        }
        binding.layoutBasiccontrol.visibility= View.VISIBLE
        binding.layoutFinish.visibility= View.GONE
        binding.layoutLock.visibility= View.GONE
        binding.layoutCompleted.visibility=View.GONE

            binding.imgLock.setOnClickListener{
            binding.layoutBasiccontrol.visibility= View.GONE
            binding.layoutFinish.visibility= View.GONE
            binding.layoutLock.visibility= View.VISIBLE
            binding.layoutCompleted.visibility=View.GONE
        }

        binding.imgLockclose.setOnLongClickListener{
            binding.layoutBasiccontrol.visibility= View.VISIBLE
            binding.layoutFinish.visibility= View.GONE
            binding.layoutLock.visibility= View.GONE
            binding.layoutCompleted.visibility=View.GONE
            return@setOnLongClickListener true
        }

        binding.imgSetting.setOnClickListener {
            val i = Intent(this, Setting_Activity::class.java)
            startActivity(i)
        }

        binding.imgPause.setOnClickListener{
            binding.layoutBasiccontrol.visibility= View.GONE
            binding.layoutFinish.visibility= View.VISIBLE
            binding.layoutLock.visibility= View.GONE
            binding.layoutCompleted.visibility=View.GONE
            toggleRun()
        }
        binding.imgResume.setOnClickListener{

            toggleRun()
            binding.layoutBasiccontrol.visibility= View.VISIBLE
            binding.layoutFinish.visibility= View.GONE
            binding.layoutLock.visibility= View.GONE
            binding.layoutCompleted.visibility=View.GONE

        }
        binding.imgFinish.setOnClickListener{
            binding.layoutBasiccontrol.visibility= View.GONE
            binding.layoutFinish.visibility= View.GONE
            binding.layoutLock.visibility= View.GONE
            binding.viewTime.visibility= View.VISIBLE
            binding.layoutTime.visibility=View.VISIBLE
            binding.layoutCompleted.visibility=View.VISIBLE
        }
        binding.buttonSave.setOnClickListener{

            finishRun()
                   }
        binding.imgPhotoPicker.setOnClickListener {
            ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start() }
             }

    override fun onStart() {
        super.onStart()
        if (trackingViewModel.isTracking.value!! || trackingViewModel.currentTimeInMillis.value!! > 0L) {
                   }
    }

    private fun finishRun() {
        var elevationListJsonString :String="";
        var speedpaceJsonString:String=""

        if(elevationList.isNotEmpty()){

            for (valueD: Double in elevationList) {
            Log.d("elevationlist", ">> " + valueD)
        }

            val gs= Gson()
            elevationListJsonString   = gs.toJson(elevationList)
          }


        if(avgSpeedlist.isNotEmpty()){

            for (valueD: Double in avgSpeedlist) {
                Log.d("avgSpeedlist", ">> " + valueD)
            }
             val gs=Gson()
            speedpaceJsonString=gs.toJson(avgSpeedlist)

             }
        Log.d("elevationlist", "json " + elevationListJsonString)

        Log.d("clicked","click")
        zoomToSeeWholeTrack()
        map!!.snapshot {
                  if (it != null) {
                      trackingViewModel.processRun(this, it,elevationListJsonString,type_value,speedpaceJsonString)
                  }
            Snackbar.make(
                this.findViewById(R.id.rootView),
                getString(R.string.run_saved_successfully),
                Snackbar.LENGTH_LONG
            ).show()

            finish()
        }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!

            // Use Uri object instead of File to avoid storage permissions
          //  binding.imgUser.setImageURI(uri)
            add_Imagestolist(uri)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
    private fun toggleRun() {

        if (trackingViewModel.currentTimeInMillis.value!! == 0L) {
            val success = setRunTarget(goaltype)
            if (success) {
                trackingViewModel.sendCommandToService(this)
               // motionLayout.transitionToEnd()
            }
        } else {
            trackingViewModel.sendCommandToService(this)
        }
    }
       private fun setRunTarget(type:String): Boolean {
        val typeString = type
           Log.d("targetvalue",">> " +typeString)
           val targetValuetemp=targetValue.toFloat()
           var insertTargtvalue :Float=0f
           val targetType = TargetType.valueOf(typeString.toUpperCase(Locale.getDefault()))

           when (typeString)
           {
               "DISTANCE" ->  insertTargtvalue = (targetValuetemp.toFloat()*1000).toFloat()

               "CALORIES"  -> insertTargtvalue = targetValuetemp.toFloat()

               "TIME"  -> insertTargtvalue = targetValuetemp.toFloat()

                         }

           Log.d("targetvalue",">> " +insertTargtvalue)
        if (insertTargtvalue == 0f && targetType != TargetType.NONE) {

            return false
        } else {
            if (targetType != TargetType.NONE) targetType.value = insertTargtvalue
        }
        trackingViewModel.userInfo.applyChanges(targetType = targetType)
        return true
    }


    private fun subscribeToObservers() {
        trackingViewModel.isTracking.observe(this, Observer {

           /* menu?.getItem(0)?.isVisible = it ||
                    trackingViewModel.currentTimeInMillis.value!! > 0L*/

        })

        trackingViewModel.trackingStatus.observe(this, Observer {
            Log.d("tracStatus",">>"+it.toString());

            if(it.equals(0))
            {
                binding.layoutBasiccontrol.visibility= View.VISIBLE
                binding.layoutFinish.visibility= View.GONE
            }else if(it.equals(1))
            {
                binding.layoutBasiccontrol.visibility= View.GONE
                binding.layoutFinish.visibility= View.VISIBLE
            }

        })


        trackingViewModel.pathPoints.observeForever {
            Log.d("updatapoint",it.toString())
            addLatestPolyline()
            moveCameraToUser()

        }


        trackingViewModel.currentTimeInMillis.observeForever{curTimeInSeconds=it.toLong()
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(it, true)
            binding.cMeter.text  = formattedTime
        }

        trackingViewModel.caloriesBurned.observeForever{
            val formattedcal =it
            binding.calCountTxt.text  = "$formattedcal cal"
        }


        trackingViewModel.avgSpeed.observeForever{
            val avgSpeed = it

          //  avgSpeed = round(((distance/curTimeInSeconds)*(3600/1000))*10)/10 // Show speed in km/h
            avgSpeedlist.add(avgSpeed.toDouble())

            Log.d("AvgSpeed","_speed>>"+avgSpeed)

            if(type_value.equals("walk")) {


            }
            else if(type_value.equals("run"))
            {
                //binding. speedCountTxt.text="$avgSpeed km/h"
                binding. speedCountTxt.text="${TrackingUtility.getFormattedElevation(avgSpeed.toDouble())} km"

            }
            else if(type_value.equals("ride"))
            {
                //  binding.speedCountTxt.text = "$avgSpeed km/h"
                binding. speedCountTxt.text="${TrackingUtility.getFormattedElevation(avgSpeed.toDouble())} km/h"
            }


        }


        trackingViewModel.distanceInMeters.observeForever{
            val km = it
            avgSpeedlist= ArrayList();
            distance=km.toFloat()

            val formatDistance=TrackingUtility.getFormattedDistance(km)

            avgSpeed = round(((distance/curTimeInSeconds)*(3600/1000))*10)/10 // Show speed in km/h
            avgSpeedlist.add(avgSpeed.toDouble())
            Log.d("AvgSpeed","_dis\">>"+distance)
            Log.d("AvgSpeed","_speed>>"+avgSpeed)


            if(type_value.equals("walk")) {

                binding.speedCountTxt.text = "$formatDistance km"
            }
            else if(type_value.equals("run"))
            {
                binding.stepsCountTxt.text = "$formatDistance km"
                //binding. speedCountTxt.text="$avgSpeed km/h"
                binding. speedCountTxt.text="2.0 km/h"
            }
            else if(type_value.equals("ride"))
            {
                binding.stepsCountTxt.text=  "$formatDistance km"
                //  binding.speedCountTxt.text = "$avgSpeed km/h"
                binding. speedCountTxt.text="7.0 km/h"
            }
        }



        trackingViewModel.liveSteps.observeForever {
            val km = it
                Log.d("viewmodel",">>"+km)

                        if(type_value.equals("walk")) {
                            binding.stepsCountTxt.text = "$km"
                        }
        }
        trackingViewModel.progress.observeForever {
            val km = it
            Log.d("Progress",">>"+km)
            binding.progressSetp.currentProgress=km
        }


       trackingViewModel.trackingaltitude.observeForever {
            val altitude = TrackingUtility.getFormattedElevationinsert(it)
           elevationList.add(altitude)

           Log.d("Altitude"," altitude>>"+altitude)

        }
    }



    private fun addLatestPolyline() {
        if (pathPoints.value!!.isNotEmpty() && pathPoints.value!!.last().size > 1) {

            Log.d("drawupdate",pathPoints.toString())
        /*    if(isStartPoint) {
                 startPoint = pathPoints.value!!.last()[pathPoints.value!!.last().size - 2]
            }*/

           val preLastLatLng = pathPoints.value!!.last()[pathPoints.value!!.last().size - 2]

            val lastLatLng = pathPoints.value!!.last().last()

           // map?. addMarker(MarkerOptions().position(startPoint).title("Start"))  .geodesic(true) .jointType(JointType.BEVEL)
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)

            isStartPoint =false
        }
    }
    private fun moveCameraToUser() {
        if (pathPoints.value!!.isNotEmpty() && pathPoints.value!!.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(pathPoints.value!!.last().last(), MAP_ZOOM)
            )
        }
    }
    private fun addAllPolylines() {
        pathPoints.value?.forEach {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(it)
            map?.addPolyline(polylineOptions)
        }
    }
    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints.value!!) {
            for (pos in polyline) {
                bounds.include(pos)
            }
        }

        val latLngBounds = try {
            bounds.build()
        } catch (e: IllegalStateException) {
            Timber.e(e, "Cannot find any path points, associated with this activity R/c/W")
            return
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                latLngBounds,
                mapView!!.width,
                mapView!!.height,
                (mapView!!.height * 0.05f).toInt()))
         }


         override fun onResume()
         {
         super.onResume()
             mapView?.onStop()
         }
         override fun onStop() {
        super.onStop()
        mapView?.onStop()
         }
         override fun onPause() {
        super.onPause()
        mapView?.onPause()
         }
    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
    override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        mapView?.onSaveInstanceState(outState)
      //  binding.mapView.onSaveInstanceState(outState)
    }
    private fun requestPermissions() {
        if (TrackingUtility.hasLocationPermissions(this)) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                Constants.REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACTIVITY_RECOGNITION)
        }

        else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                Constants.REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACTIVITY_RECOGNITION
            )
        }
    }
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
        else {
            requestPermissions()
        }
    }
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
      }
    override fun onBackPressed() {
             AlertDialog.Builder(this)
            .setTitle("Exit Alert")
            .setMessage("Do You Want To Exit ?")
            .setPositiveButton(android.R.string.ok) { dialog, whichButton ->

                trackingViewModel.setCancelCommand(this)
                /*if(type_value.equals("walk"))
                {


                    finishRun()
                }
                else
                {
                    finishRun()
                }*/
                super.onBackPressed()
                }.setNegativeButton(android.R.string.cancel) { dialog, whichButton ->
                }
            .show()
    }
    fun setup_Img_Adapter()
    {
        binding.imgRecyclerView?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        mImg_Adapter = ImageAdapter(image_List, this)
        binding.imgRecyclerView?.adapter = mImg_Adapter
    }

     fun add_Imagestolist(uri:Uri )
    {
        Log.d("images",uri.encodedPath+">>>>")
        image_List.add(uri)
        mImg_Adapter = ImageAdapter(image_List, this)
        binding.imgRecyclerView?.adapter = mImg_Adapter
        Timber.d("addedimage"+image_List)
    }
}




