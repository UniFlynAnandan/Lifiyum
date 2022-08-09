package com.lifiyum.main.basicactivitys

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.ui.AppBarConfiguration
import com.lifiyum.main.basicactivitys.viewmodelss.MAINViewModel
import com.lifiyum.main.databinding.ActivitySettingBinding
import com.lifiyum.main.db.Run
import com.lifiyum.main.other.Constants
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class Setting_Activity : AppCompatActivity() {


    @Inject
    lateinit var sharedPref: SharedPreferences
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySettingBinding
    private val viewModelc: MAINViewModel by viewModels()
            override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         binding = ActivitySettingBinding.inflate(layoutInflater)
                setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
         setContentView(binding.root)
         if(getSupportActionBar()!=null){
            getSupportActionBar()?.hide();
          }
            binding.imgBack.setOnClickListener({finish()})
            binding.linMyactivy.setOnClickListener {
                val i = Intent(this, MyActivitslList_Activity::class.java)

                startActivity(i)
            }
                binding.switchPause.setOnCheckedChangeListener({ _ , isChecked ->
                    val valu = if (isChecked) "1" else "0"
                    toWrite_KEY_AUTO_PAUSE(valu)

                })



            viewModelc.runs.observe(this , Observer {

                Timber.d(it.toString());
            Log.d("runningadata",it.toString())

            if(it.isNotEmpty())
            {
                Log.d("database","if  >>>   "+it.toString())
            }
              else
            {
                Log.d("databaseelse",it.toString())
            }
        })

         val checked_Value = sharedPref.getString(Constants.KEY_AUTO_PAUSE, "0")

                if(checked_Value.equals("1"))
                {
                   binding.switchPause.setChecked(true)
                }else
                {
                    binding.switchPause.setChecked(false)
                }

    }


    fun toWrite_KEY_AUTO_PAUSE(KEY_AUTO_PAUSE_value:String)
    {
        sharedPref.edit().putString(Constants.KEY_AUTO_PAUSE, KEY_AUTO_PAUSE_value).apply()
    }

    fun read_form_() : String
    {
        val result_String: String?
        result_String = sharedPref.getString(Constants.KEY_AUTO_PAUSE, "0").toString()
        return result_String
    }
}