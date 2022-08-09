package com.lifiyum.main.basicactivitys

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.lifiyum.main.R
import com.lifiyum.main.basicactivitys.viewmodelss.MAINViewModel
import com.lifiyum.main.databinding.ActivitySetGoalBinding
import com.lifiyum.main.other.Constants
import com.lifiyum.main.other.SortType
import com.lifiyum.main.other.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


@AndroidEntryPoint
class SetGoal_Activity : AppCompatActivity() ,EasyPermissions.PermissionCallbacks {

    private lateinit var binding: ActivitySetGoalBinding
     var goaltype :String? = "NONE"
     var targetValue:String?="0"
     var activity_TYPE:String=""

    lateinit var bestbyKillometer:List<Float>
    lateinit var bestbyTime:List<Float>
    lateinit var bestbyCalorie:List<Float>
    private val trackingViewModel: MAINViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if(getSupportActionBar()!=null){
            getSupportActionBar()?.hide();
        }
        val bundle: Bundle? = intent.extras
        val type_value: String? = bundle?.getString("type")
        activity_TYPE=type_value.toString()
        Log.d("value is"," >>>>"+type_value)
        if (type_value != null) {
            init(type_value)
        }
        requestPermissions()
        binding.imgBack.setOnClickListener({ onBackPressed()})

        setBestPerfomace()



        }
    fun init(typr:String)
    {
        activity_TYPE=typr
        binding.edtCalories.isEnabled=false
        binding.edtDistance.isEnabled = false
        binding.edtTime.isEnabled = false
        val  title_icon = binding.statusIcon
        val  titletext=binding.txtIcontitle

        if(typr.equals("run"))
        {
            titletext.setText("Run")
            title_icon.setImageResource(R.drawable.ic_run)
        }else if(typr.equals("walk"))
        {
            titletext.setText("Walk")
            title_icon.setImageResource(R.drawable.ic_walk)
        }else if (typr.equals("ride"))
        {
            titletext.setText("Ride")
            title_icon.setImageResource(R.drawable.ic_cycling)
        }

        binding.buttonSave.setOnClickListener(View.OnClickListener {

              if(goaltype.equals("DISTANCE"))
              {
                  targetValue = binding.edtDistance.text.toString()

                  StartActivitwithGoals(targetValue!!,typr)
              }
            else if(goaltype.equals("TIME"))
              {
                  targetValue = binding.edtTime.text.toString()
                  StartActivitwithGoals(targetValue!!,typr)
              }
            else if (goaltype.equals("CALORIES"))
              {
                  targetValue = binding.edtCalories.text.toString()
                  StartActivitwithGoals(targetValue!!,typr)
              }
            else
              {
                  StartActivitwithGoals("0",typr)
              }
                        /*val i = Intent(this, Countdown_Activity::class.java)
            i.putExtra("type",typr)
            i.putExtra("goaltype",goaltype)
            i.putExtra("targetvalue",targetValue)
            startActivity(i)*/})

        binding.textSkip.setOnClickListener(View.OnClickListener {val i = Intent(this, Countdown_Activity::class.java)
            i.putExtra("type",typr)
            i.putExtra("goaltype","NONE")
            i.putExtra("targetvalue",targetValue)
                    startActivity(i) })
/*        binding.edtDistance.addTextChangedListener(textWatcherDstance)
        binding.edtTime.addTextChangedListener(textWatcherTime)
        binding.edtCalories.addTextChangedListener(textWatcherCaloriee)*/

      //  binding.rbDistance.setOnCheckedChangeListener { compoundButton, b ->  }


        binding.linDistance.setOnClickListener {


            Log.d("checked", "ditance")
            binding.edtCalories.getText().clear()
            binding.edtTime.getText().clear()
            binding.edtDistance.setText("0")
            binding.edtDistance.isEnabled = true
            binding.edtCalories.isEnabled = false
            binding.edtTime.isEnabled = false
            goaltype = "DISTANCE"
            binding.viewDistance.visibility=View.INVISIBLE
            binding.viewTime.visibility=View.VISIBLE
            binding.viewCalories.visibility=View.VISIBLE
        }

        binding.viewDistance.setOnClickListener {
            Log.d("checked", "ditance")
            binding.edtCalories.getText().clear()
            binding.edtTime.getText().clear()
            binding.edtDistance.setText("0")
            binding.edtDistance.isEnabled = true
            binding.edtCalories.isEnabled = false
            binding.edtTime.isEnabled = false
            goaltype = "DISTANCE"
            binding.viewDistance.visibility=View.INVISIBLE
            binding.viewTime.visibility=View.VISIBLE
            binding.viewCalories.visibility=View.VISIBLE
        }

        binding.linTime.setOnClickListener {


            Log.d("checked", "time")
            binding.edtDistance.getText().clear()
            binding.edtCalories.getText().clear()
            binding.edtTime .setText("0")
            binding.edtTime.isEnabled = true
            binding.edtDistance.isEnabled = false
            binding.edtCalories.isEnabled = false
            goaltype = "TIME"
            binding.viewDistance.visibility=View.VISIBLE
            binding.viewTime.visibility=View.INVISIBLE
            binding.viewCalories.visibility=View.VISIBLE
        }


        binding.viewTime.setOnClickListener {

            Log.d("checked", "time")
            binding.edtDistance.getText().clear()
            binding.edtCalories.getText().clear()
            binding.edtTime .setText("0")
            binding.edtTime.isEnabled = true
            binding.edtDistance.isEnabled = false
            binding.edtCalories.isEnabled = false
            goaltype = "TIME"
            binding.viewDistance.visibility=View.VISIBLE
            binding.viewTime.visibility=View.INVISIBLE
            binding.viewCalories.visibility=View.VISIBLE
        }


        binding.linCalories.setOnClickListener {


                binding.edtDistance  .getText().clear()
                binding.edtTime  .getText().clear()

                Log.d("checked","calore")
                 binding.edtCalories .setText("0")
                binding.edtCalories.isEnabled=true
                binding.edtDistance.isEnabled = false
                binding.edtTime.isEnabled = false
                binding.checkboxTime.isChecked=false;
                binding.checkboxDistance.isChecked=false
                goaltype="CALORIES"
            binding.viewDistance.visibility=View.VISIBLE
            binding.viewTime.visibility=View.VISIBLE
            binding.viewCalories.visibility=View.INVISIBLE
            }

         binding.viewCalories.setOnClickListener {
            binding.edtDistance  .getText().clear()
            binding.edtTime  .getText().clear()

            Log.d("checked","calore")
            binding.edtCalories .setText("0")
            binding.edtCalories.isEnabled=true
            binding.edtDistance.isEnabled = false
            binding.edtTime.isEnabled = false
            binding.checkboxTime.isChecked=false;
            binding.checkboxDistance.isChecked=false
            goaltype="CALORIES"
            binding.viewDistance.visibility=View.VISIBLE
            binding.viewTime.visibility=View.VISIBLE
            binding.viewCalories.visibility=View.INVISIBLE
        }

        }

    fun setBestPerfomace()
    {
        trackingViewModel.getDataFromActivity(activity_TYPE).observe(this, Observer {
            it?.reversed()?.let { runs ->
                var sortType = SortType.DISTANCE
                bestbyKillometer =     runs.indices.map { i ->
                    when (sortType) {
                        SortType.DATE -> runs[i].timestamp.toFloat()
                        SortType.RUNNING_TIME -> runs[i].timeInMillis.toFloat()
                        SortType.DISTANCE -> runs[i].distanceInMeters.toFloat()
                        SortType.AVG_SPEED -> runs[i].avgSpeedInKMH
                        SortType.CALORIES_BURNED -> runs[i].caloriesBurned.toFloat()
                    }
                }

                if(bestbyKillometer.isNotEmpty()) {
                    Log.d("best", "KM " + getDistance(findMax(bestbyKillometer)))

                    binding.distancePersonalTxt.text =
                        "( Personal Best : " + getDistance(findMax(bestbyKillometer)) + " Km )"
                }

            sortType = SortType.RUNNING_TIME
                bestbyTime =     runs.indices.map { i ->
                    when (sortType) {
                        SortType.DATE -> runs[i].timestamp.toFloat()
                        SortType.RUNNING_TIME -> runs[i].timeInMillis.toFloat()
                        SortType.DISTANCE -> runs[i].distanceInMeters.toFloat()
                        SortType.AVG_SPEED -> runs[i].avgSpeedInKMH
                        SortType.CALORIES_BURNED -> runs[i].caloriesBurned.toFloat()
                    }
                }
                if(bestbyTime.isNotEmpty()) {
                    Log.d("best", "TIME " + findMax(bestbyTime)?.let { it1 ->
                        TrackingUtility.getFormattedStopWatchTime(
                            it1.toLong()
                        ).toString()
                    } + "Min )")
                    val formatedminutes = findMax(bestbyTime)?.let { it1 -> TimeUnit.MILLISECONDS.toMinutes( it1.toLong()) }






                   binding.distanceTimeTxt.text =
                        "( Personal Best : " +formatedminutes+  " Min )"

                }
                sortType = SortType.CALORIES_BURNED
                bestbyCalorie  = runs.indices.map { i ->
                when (sortType) {
                    SortType.DATE -> runs[i].timestamp.toFloat()
                    SortType.RUNNING_TIME -> runs[i].timeInMillis.toFloat()
                    SortType.DISTANCE -> runs[i].distanceInMeters.toFloat()
                    SortType.AVG_SPEED -> runs[i].avgSpeedInKMH
                    SortType.CALORIES_BURNED -> runs[i].caloriesBurned.toFloat()
                }
            }

                if(bestbyCalorie.isNotEmpty()) {
                    Log.d("best", "calorie " + findMax(bestbyCalorie).toString())


                    val rounded = java.lang.String.format("%.0f", findMax(bestbyCalorie) )

                    binding.caloriePersonalTxt.text =
                        "( Personal Best : " + rounded + " Cal )"

                }
            }

        })

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
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
    fun StartActivitwithGoals( tragetTypevalue :String,  type:String)
    { val i = Intent(this, Countdown_Activity::class.java)
        i.putExtra("type",type)
        i.putExtra("goaltype",goaltype)
        i.putExtra("targetvalue",tragetTypevalue)
        startActivity(i)
        finish()
    }
    fun getDistance(datas: Float?): String {
            val km = datas!! / 1000f
            return ((km * 10).roundToInt() / 10f).toString()

    }
    fun findMax(list: List<Float>): Float? {
        return  list.sortedWith(compareBy { it }).last()
    }

    }







