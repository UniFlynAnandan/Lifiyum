package com.lifiyum.main.basicactivitys


import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.lifiyum.main.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreen_Activity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if(getSupportActionBar()!=null){
            getSupportActionBar()?.hide();
        }
        init()
    }
    fun init()
    {
        Handler().postDelayed( Runnable() {

            run() {
                val i = Intent(this@SplashScreen_Activity, SelectActivity_Activitykt::class.java)
                startActivity(i)
                finish()   }
        }, 2000)
    }
}