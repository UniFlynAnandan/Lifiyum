package com.lifiyum.main.basicactivitys

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.lifiyum.main.R
import com.lifiyum.main.other.Constants.KEY_ACTIVITY_TYPE
import com.lifiyum.main.other.Constants.KEY_NAME
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectActivity_Activitykt : AppCompatActivity() {


    @Inject
    lateinit var sharedPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_activitykt)
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if(getSupportActionBar()!=null){
            getSupportActionBar()?.hide();
        }

        init()
        }

       fun init()
       {
           toWriteActvity_Status("actvity")
        val  run_imageview = findViewById(R.id.image_run) as ImageView

           run_imageview.setOnClickListener {
             val i = Intent(this@SelectActivity_Activitykt, SetGoal_Activity::class.java)
             i.putExtra("type","run")
               toWriteActvity_Status("run")
             startActivity(i)
        }
        val  walk_imageview = findViewById(R.id.image_walk) as ImageView
        walk_imageview.setOnClickListener {

            val i = Intent(this@SelectActivity_Activitykt, SetGoal_Activity::class.java)
            i.putExtra("type","walk")
            toWriteActvity_Status("walk")
            startActivity(i)
        }

        val  ride_imageview = findViewById(R.id.image_ride) as ImageView
        ride_imageview.setOnClickListener {

            val i = Intent(this@SelectActivity_Activitykt, SetGoal_Activity::class.java)
            i.putExtra("type","ride")
            toWriteActvity_Status("ride")
            startActivity(i)
        }
    }

    fun toWriteActvity_Status(actvity_type:String)
    {
        sharedPref.edit().putString(KEY_ACTIVITY_TYPE, actvity_type).apply()
    }
}