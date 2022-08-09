package com.lifiyum.main.basicactivitys

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.lifiyum.main.databinding.ActivityCountdownBinding


class Countdown_Activity : AppCompatActivity() {

       private lateinit var binding: ActivityCountdownBinding
  //  private val presenter = MapPresenter(this)
       override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountdownBinding.inflate(layoutInflater)
        setContentView(binding.root)
      setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if(getSupportActionBar()!=null){
            getSupportActionBar()?.hide();
        }
      //presenter.onViewCreated()
        val bundle: Bundle? = intent.extras
        val type_value: String? = bundle?.getString("type")
        val goal_type: String? = bundle?.getString("goaltype")
        val targetvalue:String?=bundle?.getString("targetvalue")
           Log.d("value is"," >>>>"+type_value+">>>"+targetvalue)
        coundownTimer(type_value,goal_type,targetvalue)
    }
    fun coundownTimer(type:String?,goal_type:String?,targetvalue:String?)
    {

        object : CountDownTimer(2600, 700) {
            override fun onTick(millisUntilFinished: Long) {
                val temp =millisUntilFinished / 700
                if(temp in 0..3) {
                   binding.txtCount.setText("" + temp)
                    binding.txtCount.setTextSize(220f)
                    when (temp.toInt())
                    { 0 ->
                       {
                         binding.txtCount.setTextSize(220f)
                         binding.txtCount.setText("Go")
                         Log.d("Count","inside when")
                        }
                    }
                    }
                    }
            override fun onFinish() {
                val i = Intent(this@Countdown_Activity, ActivityProgress_Activity::class.java)
                i.putExtra("type",type)
                i.putExtra("goaltype",goal_type)
                i.putExtra("targetvalue",targetvalue)
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
                finish()
            }
        }.start()
    }
}