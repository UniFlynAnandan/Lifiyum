package com.lifiyum.main.util;

public class dummy {
}




/*
package com.lifiyum.main.basicactivitys

        import android.Manifest
        import android.annotation.SuppressLint
        import android.app.Activity
        import android.content.Context
        import android.content.Intent
        import android.net.Uri
        import android.os.Build
        import androidx.appcompat.app.AppCompatActivity
        import android.os.Bundle
        import android.util.Log
        import android.view.Menu
        import android.view.View
        import android.widget.Toast
        import androidx.activity.viewModels
        import androidx.appcompat.app.AlertDialog
        import androidx.lifecycle.MutableLiveData
        import androidx.lifecycle.Observer
        import com.github.dhaval2404.imagepicker.ImagePicker
        import com.google.android.gms.maps.CameraUpdateFactory
        import com.google.android.gms.maps.GoogleMap
        import com.google.android.gms.maps.model.*
        import com.google.android.material.snackbar.Snackbar
        import com.lifiyum.main.R


        import com.lifiyum.main.basicactivitys.Map_MVP.MapPresenter

        import com.lifiyum.main.basicactivitys.viewmodelss.MAINViewModel
        import com.lifiyum.main.databinding.ActivityProgressBinding
        import com.lifiyum.main.dataclass.Ui


        import com.lifiyum.main.db.Run
        import com.lifiyum.main.other.Constants
        import com.lifiyum.main.servicess.Polyline
        import com.lifiyum.main.servicess.TrackingServiceLifiyum
        import com.lifiyum.main.other.Constants.ACTION_PAUSE_SERVICE
        import com.lifiyum.main.other.Constants.ACTION_START_OR_RESUME_SERVICE
        import com.lifiyum.main.other.Constants.POLYLINE_WIDTH
        import com.lifiyum.main.other.TrackingUtility
        import com.uniflyn.uniflyn_runnerapp.utils.StepCounter
        import dagger.hilt.android.AndroidEntryPoint
        import pub.devrel.easypermissions.AppSettingsDialog
        import pub.devrel.easypermissions.EasyPermissions
        import timber.log.Timber


        import javax.inject.Inject
@AndroidEntryPoint
class ActivityProgress_Activity : AppCompatActivity(),EasyPermissions.PermissionCallbacks  {

private lateinit var map: GoogleMap
private lateinit var binding : ActivityProgressBinding
private val presenter = MapPresenter(this)
private lateinit var  context: Context;

        val ui = MutableLiveData(Ui.EMPTY)
@set:Inject
        var weight = 80f
private val viewModel: MAINViewModel by viewModels()
        lateinit var type_value : String
private var isTracking = false
private var pathPoints = mutableListOf<Polyline>()
//private var map: GoogleMap? = null
private var curTimeInMillis = 0L

private var menu: Menu? = null
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=this
        if(getSupportActionBar()!=null){
        getSupportActionBar()?.hide();
        }
        requestPermissions()

        val bundle: Bundle? = intent.extras
        type_value   = bundle?.getString("type").toString()
        val goaltype: String? = bundle?.getString("goaltype")
        //  presenter.onViewCreated()

        binding.mapView.onCreate(savedInstanceState)
        Log.d("value is"," >>>>"+type_value)

        subscribeToObservers()
        if (type_value != null) {
        init(type_value,goaltype)
        }
        init(type_value,goaltype)
        }

@SuppressLint("MissingPermission")
    fun init(type:String?, goaltype:String?)
            {
            toggleRun()
            //  stepCounter()
            binding.mapView.getMapAsync {
            map = it
            map.isMyLocationEnabled = true
            // map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, MAP_TYPE_HYBRID))

            if(map!=null) {
            initMAP()
            }else
            {
            initMapAgain()
            }
            }


            viewModel.runs.observe(this, Observer {

            Log.d("database",it.toString())
            if (it.isNotEmpty()){
            Log.d("database",viewModel.runs.toString())
            }
            else{
            Log.d("database","isnotdata")
            }
            })


            if(type.equals("run"))
            {
            binding.statusIcon.setImageResource(R.drawable.ic_run)
            binding.peaceTitleTxt.setText("Distance")
            binding.stepsCountTxt.setText("1.50 km")
            binding.speedTitleTxt.setText("Avg Pace")
            binding.speedCountTxt.setText("09:57/km")
            }
            else if(type.equals("walk")) {
            binding.statusIcon.setImageResource(R.drawable.ic_walk)
            binding.speedTitleTxt.setText("Distance")
            binding.speedCountTxt.setText("5.4 km")
            stepCounterforwlk(this)
            //binding.stepsCountTxt.text="$stepCount"

            }else if (type.equals("ride"))
            {
            binding.statusIcon.setImageResource(R.drawable.ic_cycling)
            binding.peaceTitleTxt.setText("Distance")
            binding.stepsCountTxt.setText("1.50 km")
            binding.speedTitleTxt.setText("Avg Speed")
            }
            if(goaltype.equals("distance"))
            {
            binding.txtSetgoal.setText("Distance Goal")
            }
            else if(goaltype.equals("time"))
            {
            binding.txtSetgoal.setText("Time Goal")
            }
            else if(goaltype.equals("calories"))
            {
            binding.txtSetgoal.setText("Calories Goal")
            }
            else if(goaltype.equals("empty"))
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

            binding.imgLockclose.setOnClickListener{
            binding.layoutBasiccontrol.visibility= View.VISIBLE
            binding.layoutFinish.visibility= View.GONE
            binding.layoutLock.visibility= View.GONE
            binding.layoutCompleted.visibility=View.GONE
            }

            binding.imgSetting.setOnClickListener({
            val i = Intent(this, Setting_Activity::class.java)
        startActivity(i)
        })

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

        Log.d("Pathpoints","size   "+ pathPoints.size   + "items>>  "+pathPoints.toString());
        if (pathPoints.size<2){(
        Snackbar.make(
        this.findViewById(R.id.rootView),
        "You don't move ",
        Snackbar.LENGTH_LONG
        ).show())

        // zoomToSeeWholeTrack()
        //endRunAndSaveToDb()
        }
        else{
        zoomToSeeWholeTrack()
        endRunAndSaveToDb()
        }
        //  finish()

        if(type.equals("walk"))
        {
        stopStepCounter(this)
        }
        }
        binding.imgPhotoPicker.setOnClickListener {
        ImagePicker.with(this)
        .crop()
        .compress(1024)
        .maxResultSize(1080, 1080)
        .start() }


        //  startTracking()

        // toggleRun()




        }



        fun initMAP()
        {
        addAllPolylines()

        }

@SuppressLint("MissingPermission")
    fun initMapAgain()
            {
            binding.mapView.getMapAsync {
            map = it
            map.isMyLocationEnabled = true
            // map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, MAP_TYPE_HYBRID))
            addAllPolylines()

            }
            }
            override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!

            // Use Uri object instead of File to avoid storage permissions
            binding.imgUser.setImageURI(uri)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
            }
private fun toggleRun() {

        Log.d("istracking",">>"+isTracking.toString())
        if(isTracking) {
        Timber.e("istracking");
        // menu?.getItem(0)?.isVisible = true
        sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
        Timber.e("notistracking");
        sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
        }

 */
/*   override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap


        //   map.setMapType(GoogleMap.MAP_TYPE_TERRAIN)
        presenter.ui.observe(this) { ui ->
            updateUi(ui)
        }
        *//*
*/
/*  val cameraPosition = CameraPosition.Builder()
            .zoom(13f)
            .build()*//*
*/
/*
        presenter.onMapLoaded()

        // map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        map.uiSettings.isZoomControlsEnabled = true
//    map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, MAP_TYPE_HYBRID))

    }

    private fun startTracking() {
     //  binding.txtPace.text = ""
      //  binding.txtDistance.text = ""
        binding.timeCountTxt.base = SystemClock.elapsedRealtime()
        binding.timeCountTxt.start()

        *//*
*/
/*if(map!=null) {
            map.clear()
        }*//*
*/
/*
        presenter.startTracking()
    }

    private fun stopTracking() {
        presenter.stopTracking()
       *//*
*/
/* binding.txtTime.stop()*//*
*/
/*
    }

    @SuppressLint("MissingPermission")
    private fun updateUi(ui: Ui) {
        if (ui.currentLocation != null && ui.currentLocation != map.cameraPosition.target) {
            map.isMyLocationEnabled = true
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(ui.currentLocation, 14f))
        }
      //  binding.txtDistance.text = ui.formattedDistance
       /// binding.txtPace.text = ui.formattedPace
        drawRoute(ui.userPath)
    }

    private fun drawRoute(locations: List<LatLng>) {
        val polylineOptions = PolylineOptions().color(R.color.purple_700)
        map.clear()
        val points = polylineOptions.points
        points.addAll(locations)
        map.addPolyline(polylineOptions)
//    map. addMarker(MarkerOptions().position(locations.get(0)).title("Start"))
        for(i in locations.indices){
            println(locations[i])
            Log.d("lot_lang","$$$$ "+locations[i].longitude + " >> >>>>"+locations[i].longitude);
        }
    }*//*

private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for(polyline in pathPoints) {
        for(pos in polyline) {
        bounds.include(pos)
        }
        }

        map?.moveCamera(
        CameraUpdateFactory.newLatLngBounds(
        bounds.build(),
        binding.mapView.width,
        binding.mapView.height,
        (  binding.mapView.height * 0.05f).toInt()
        )
        )
        }

private fun endRunAndSaveToDb() {
        map?.snapshot { bmp ->
        var distanceInMeters = 0
        for(polyline in pathPoints) {
        distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
        }
        val avgSpeed = Math.round((distanceInMeters / 1000f) / (curTimeInMillis / 1000f / 60 / 60) * 10) / 10f
        val dateTimestamp = java.util.Calendar.getInstance().timeInMillis
        val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
        val run = Run(bmp, dateTimestamp, avgSpeed, distanceInMeters, curTimeInMillis, caloriesBurned)

        viewModel.insertRun(run)
        Snackbar.make(
        this.findViewById(R.id.rootView),"Run saved successfully",
        Snackbar.LENGTH_LONG
        ).show()

        Timber.d("datasaved",run.toString());
        stopTrackingService()


        }
        }
private fun subscribeToObservers() {
        TrackingServiceLifiyum.isTracking.observe(this, Observer {

        updateTracking(it)
        })
        TrackingServiceLifiyum.pathPoints.observe(this, Observer {
        pathPoints = it
        /// Log.d("points",">> " );
        addLatestPolyline()
        moveCameraToUser()

        })

        TrackingServiceLifiyum.timeRunInMillis.observe(this, Observer {
        curTimeInMillis = it
        val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeInMillis, true)
        binding.cMeter.text = formattedTime
        updateSteps(pathPoints)
        })
        }
private fun moveCameraToUser() {
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
        map?.animateCamera(
        CameraUpdateFactory.newLatLngZoom(
        pathPoints.last().last(),
        Constants.MAP_ZOOM
        )
        )
        }
        }
private fun updateSteps(it: MutableList<MutableList<LatLng>>?) {
        var distanceInMeters = 0
        if (it != null) {
        for(polyline in it) {
        distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
        }
        }
        val distanceBySteps = (distanceInMeters*1350)/1000
        //  binding.stepsCountTxt.text="$distanceBySteps"
        }

private fun addAllPolylines() {

        for(polyline in pathPoints) {
        val polylineOptions = PolylineOptions()
        .color(getColor(R.color.mapdrawline))
        .width(Constants.POLYLINE_WIDTH)
        .addAll(polyline)
        map?.addPolyline(polylineOptions)
        map?. addMarker(MarkerOptions().position(polyline.get(0)).title("Start"))
        }
        }

private fun stopTrackingService() {
        // tvTimer.text="00:00:00:00"
        sendCommandToService(Constants.ACTION_STOP_SERVICE)
        finish() }
private fun addLatestPolyline() {
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
        val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]


        Log.d("ponits",pathPoints.get(0).toString())
        val lastLatLng = pathPoints.last().last()
        val polylineOptions = PolylineOptions()
        .color( R.color.mapdrawline)
        .width(POLYLINE_WIDTH)
        .add(preLastLatLng)
        .add(lastLatLng)
        map?.addPolyline(polylineOptions)
        }
        }
private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if(!isTracking&&curTimeInMillis>0L) {
        //  btnToggleRun.text = "Start"
        // btnFinishRun.visibility = View.VISIBLE
        } else if (isTracking){
        // btnToggleRun.text = "Stop"
        Timber.d("Trackingupdating","track ")
        //menu?.getItem(0)?.isVisible = true
        //  btnFinishRun.visibility = View.GONE
        }
        }

private fun sendCommandToService(action: String) =
        Intent(this, TrackingServiceLifiyum::class.java).also {
        it.action = action
        this.startService(it)
        }

        override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        }

        override fun onStart() {
        super.onStart()
        binding. mapView.onStart()
        }

        override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
        }

        override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        }
        override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
        }
        override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
        }

        fun stepCounterforwlk(activity:Activity){
        val stepCounter = StepCounter(activity as AppCompatActivity)
        Log.d("steps","started")
        stepCounter.setupStepCounter()
        stepCounter.liveSteps.observe(activity) { steps ->
        // val current = ui.value
        Log.d("steps", steps.toString())
        binding.stepsCountTxt.text="$steps"
        }
        }
        fun stopStepCounter(activity:Activity)
        {
        val stepCounter = StepCounter(activity as AppCompatActivity)
        stepCounter.unloadStepCounter()
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
        Manifest.permission.ACTIVITY_RECOGNITION
        )
        } else {
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
        } else {
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
        // super.onBackPressed()
        AlertDialog.Builder(this)
        .setTitle("Exit Alert")
        .setMessage("Do You Want To Exit ?")
        .setPositiveButton(android.R.string.ok) { dialog, whichButton ->

        if(type_value.equals("walk"))
        {
        stopStepCounter(this)
        stopTrackingService()
        }
        stopTrackingService()
        super.onBackPressed()
        }
        .setNegativeButton(android.R.string.cancel) { dialog, whichButton ->

        }
        .show()
        }
        }



*/

