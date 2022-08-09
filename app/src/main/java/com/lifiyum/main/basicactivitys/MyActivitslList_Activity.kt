package com.lifiyum.main.basicactivitys

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.lifiyum.main.adapters.RunAdapter
import com.lifiyum.main.basicactivitys.viewmodelss.MAINViewModel
import com.lifiyum.main.databinding.ActivityMyActivitslListBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MyActivitslList_Activity : AppCompatActivity() {

    private lateinit var binding : ActivityMyActivitslListBinding
    private val viewModelc: MAINViewModel by viewModels()
   private lateinit var  context: Context;
    /*private val trackingViewModel: TRACKINGViewModel by viewModels()*/
    private lateinit var runAdapter: RunAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context =this
        binding = ActivityMyActivitslListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if(getSupportActionBar()!=null){
            getSupportActionBar()?.hide();
        }
        init()
    }

    fun init()
    {
        binding.imgBack.setOnClickListener({finish()})
        setupRecyclerView()
        viewModelc.runs.observe(this, Observer {
            runAdapter.submitList(it)
        })
    }

    private fun setupRecyclerView() {

        runAdapter = RunAdapter(RunAdapter.OnClickListener{pos ->
            Log.d("Position",">>   "+pos)

            val i = Intent(this, AnalisisActivity::class.java)
            i.putExtra("id",pos.toString())
            startActivity(i) })
        binding.rvRuns.apply {

            adapter = runAdapter
            layoutManager = LinearLayoutManager(context)

        }


    }
}