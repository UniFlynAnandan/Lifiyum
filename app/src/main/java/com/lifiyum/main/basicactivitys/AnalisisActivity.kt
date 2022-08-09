package com.lifiyum.main.basicactivitys


import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import com.lifiyum.main.R
import com.lifiyum.main.basicactivitys.viewmodelss.MAINViewModel
import com.lifiyum.main.databinding.ActivityAnalisisBinding
import com.lifiyum.main.other.TrackingUtility
import com.lifiyum.main.util.Score
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnalisisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalisisBinding
    private lateinit var barChart: BarChart
    private lateinit var lineChart: LineChart
    private val viewModelc: MAINViewModel by viewModels()
    lateinit var id : String
    private var scoreList = ArrayList<Score>()
    private var elevationList : ArrayList<Double> = ArrayList()
    private var speedPaceList : ArrayList<Double> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalisisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if(getSupportActionBar()!=null){
            getSupportActionBar()?.hide();
        }

        val bundle: Bundle? = intent.extras
        val type_value: String? = bundle?.getString("type")
        id   = bundle?.getString("id").toString()
        Log.d("databaseid",id)

        barChart = findViewById(R.id.barChart)
        barChart.setScaleEnabled(false)
        lineChart = findViewById(R.id.lineChart)
        lineChart.setScaleEnabled(false)
        scoreList = getScoreList()

        initBarChart()
        initLineChart()
        binding.imgBack.setOnClickListener({finish()})

        binding.icShare.setOnClickListener{
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Morning Running Feeling Awsome post via Lifium Android App.")
                type = "text/plain"
            }
             val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        viewModelc.getSingledata(id).observe(this, Observer {
            Glide.with(this).load(it.img).into(binding.ivRunImage)
            Log.d("databaseSingle","if  >>>   "+it.toString())
            Log.d("databaseSingle","if  >>>   "+it.elevation)


            binding.movingValueTxt.text="${TrackingUtility.getFormattedStopWatchTime(it.timeInMillis)}"

           val totaltime:  Long
            totaltime=it.timeInMillis+60000
            binding.totalTimeValue.text="${TrackingUtility.getFormattedStopWatchTime(totaltime)}"
            binding.distanceValueTxt.text="${TrackingUtility.getFormattedDistance(it.distanceInMeters)} km"


           if(it.activityTYPE.equals("walk")) {
               binding.llCadance.visibility= View.GONE
               binding.viewCadance.visibility= View.GONE
               /*if(!it.walkingSteps.toLong().equals(0L) && !it.timeInMillis.equals(0L)) {
                   Log.d("cadance"," >>"+ "walk")
                   val temp = to_calucuaate_Cadence(
                       it.walkingSteps.toLong(),
                       it.timeInMillis
                   ) + " steps per minute"

                   binding.cadenseValue.text = temp
               }
               else{
                   binding.llCadance.visibility= View.GONE
                   binding.viewCadance.visibility= View.GONE
               }*/

           }
            else if(it.activityTYPE.equals("run"))
           {
               binding.llCadance.visibility= View.GONE
               binding.viewCadance.visibility= View.GONE

            /*   val totalDistance: Long = it.distanceInMeters / 1000L

               val total_step: Long = 1265 * totalDistance

                if(!total_step.equals(0L) && !it.timeInMillis.equals(0L)) {
                    Log.d("cadance"," >>"+ "run")


                    val temp = to_calucuaate_Cadence(
                        total_step,
                        it.timeInMillis
                    ) + " steps per minute"

                    binding.cadenseValue.text = temp
                }
               binding.llCadance.visibility= View.GONE
               binding.viewCadance.visibility= View.GONE*/
           }
            else{
               binding.llCadance.visibility= View.GONE
               binding.viewCadance.visibility= View.GONE
           }

             binding.setData(it)
            maximumspeedSetup(it.speedandpaceData)
            elevation_Setup(it.elevation)
                   })

    }



    private fun maximumspeedSetup(json:String)
    {
        speedPaceList


        val gs= Gson()
        speedPaceList  = gs.fromJson(json, ArrayList::class.java) as ArrayList<Double>



        val minValue: Double
        val maxValue: Double
        val diff: Double
        minValue = speedPaceList.minOf { it }
        maxValue = speedPaceList.maxOf { it }
        binding.maxSpeedValue.text="${TrackingUtility.getFormattedElevation(maxValue)} km/h"
    }


    private fun elevation_Setup(json:String)
    {
        val gs= Gson()
        elevationList  = gs.fromJson(json, ArrayList::class.java) as ArrayList<Double>

        for(dd :Double in elevationList)
        {
            Log.d("elevationlist",">> "+dd)
        }

        val minValue: Double
        val maxValue: Double
        val diff: Double
        minValue = elevationList.minOf { it }
        maxValue = elevationList.maxOf { it }
        diff=maxValue-minValue
        Log.d("elevationlist", "Minimum " + minValue)
        Log.d("elevationlist", "Maximum " + maxValue)
        Log.d("elevationlist", "gainelevation " +diff +"   >>>"+TrackingUtility.getFormattedElevation(diff))

        binding.elevationValueTxt.text="${TrackingUtility.getFormattedElevation(diff)} m"


         if(elevationList.isNotEmpty()) {

             if(elevationList.size>5) {
                 setDataToLineChart()
             }
         }
    }

    private fun initBarChart() {
        //hiuuunde grid lines
        barChart.axisLeft.setDrawGridLines(false)
        val xAxis: XAxis = barChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        //remove right y-axis
        barChart.axisRight.isEnabled = false
        //remove legend
        barChart.legend.isEnabled = false
        //remove description label
        barChart.description.isEnabled = false
        //add animation
        barChart.animateY(3000)
        // to draw label on xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.valueFormatter = MyAxisFormatter()
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = +90f


        //now draw bar chart with dynamic data
        val entries: ArrayList<BarEntry> = ArrayList()
        scoreList=getScoreList();
        //you can replace this data object with  your custom object
        for (i in scoreList.indices) {
            val score = scoreList[i]
            entries.add(BarEntry(i.toFloat(), score.score.toFloat()))
        }

        val barDataSet = BarDataSet(entries, "")
        barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

        val data = BarData(barDataSet)
        barChart.data = data

        barChart.invalidate()

    }

    inner class MyAxisFormatter : IndexAxisValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {

           if(scoreList.size>0)
           {
            val index = value.toInt()

               /*if(index>0)*/

            Log.d("indexvalue","++"+index)
            Log.d("indexvalue","scorelistsize"+ scoreList.size)
            return if (index>0 && index < scoreList.size) {
                scoreList[index].name
            } else {
                ""
            }
           }
        else{
               return ""
           }

        }
    }


  // simulate api call
    // we are initialising it directly

    private fun initLineChart() {

//        hide grid lines
        lineChart.axisLeft.setDrawGridLines(false)
        val xAxis: XAxis = lineChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        //remove right y-axis
        lineChart.axisRight.isEnabled = false
        //remove legend
        lineChart.legend.isEnabled = false
        //remove description label
        lineChart.description.isEnabled = false
        //add animation
        lineChart.animateX(1000, Easing.EaseInSine)
        // to draw label on xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.valueFormatter = MyAxisFormatter()
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = +90f

    }
    private fun setDataToLineChart() {
        //now draw bar chart with dynamic data
        val entries: ArrayList<Entry> = ArrayList()

     //   scoreList = getScoreList()
        val s: Set<Double> = LinkedHashSet<Double>(elevationList)
         val elevationListTemp : ArrayList<Double> = ArrayList()
        elevationListTemp.addAll(s)
        //you can replace this data object with  your custom object
        for (i in elevationListTemp.indices) {
            val score = elevationListTemp[i]
            entries.add(Entry(i.toFloat(), score.toFloat()))
        }

        val lineDataSet = LineDataSet(entries, "")
        lineDataSet.setMode( LineDataSet.Mode.LINEAR)
        lineDataSet.setColor(Color.MAGENTA);
        lineDataSet.setCircleColor(Color.BLUE);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setFillAlpha(65);
        lineDataSet.setFillColor(ColorTemplate.colorWithAlpha(Color.YELLOW, 200));
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setHighLightColor(Color.rgb(244, 117, 117));
        val data = LineData(lineDataSet)
        lineChart.data = data

        lineChart.invalidate()
    }



    /*private fun setDataToLineChart() {
        //now draw bar chart with dynamic data
       val entries: ArrayList<Entry> = ArrayList()
      //scoreList = getScoreList()




  //    scoreList=asignelevationvalue(6)
        //you can replace this data object with  your custom object
        for (i in elevationList.indices) {
            val score = elevationList[i]
            entries.add(Entry(i.toFloat(), score.toFloat()))
        }

      // entries.addAll(asignelevationvalue(4))

        val lineDataSet = LineDataSet(entries, "")
        lineDataSet.setMode( LineDataSet.Mode.CUBIC_BEZIER)
        lineDataSet.setColor(Color.MAGENTA);
        lineDataSet.setCircleColor(Color.BLUE);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setFillAlpha(65);
        lineDataSet.setFillColor(ColorTemplate.colorWithAlpha(Color.YELLOW, 200));
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setHighLightColor(Color.rgb(244, 117, 117));
        val data = LineData(lineDataSet)
        lineChart.data = data
        lineChart.invalidate()
    }*/

    // simulate api call
    // we are initialising it directly
    private fun getScoreList(): ArrayList<Score> {
        scoreList.add(Score("", 0))
        scoreList.add(Score("", 3))
        scoreList.add(Score("", 2))
        scoreList.add(Score("", -3))
        /*scoreList.add(Score("1 k", -4))
        scoreList.add(Score("1 k", 0))*/
        return scoreList
    }

    fun asignelevationvalue(elevation:Int): ArrayList<Entry> {
        val startpoint=0
       val startpointValue=elevation
       val endpoint=0
       val endpointValue= -elevation

        val entries: ArrayList<Entry> = ArrayList()
        entries.add(Entry(startpoint.toFloat(),0f))
        entries.add(Entry(startpoint.toFloat(),startpointValue.toFloat()))
        entries.add(Entry(startpoint.toFloat(),0f))
        entries.add(Entry(startpoint.toFloat(),endpointValue.toFloat()))
        Log.d("elevationdetials","elevivation"+startpoint+">>"+ endpoint+"+endpointValue")
         return entries
      }


    fun to_calucuaate_Cadence(total_Step : Long, totalTime:Long): String {
        var cadanece: Long=0L
        try {

            Log.d(
                "cadance",
                total_Step.toString() + " >>" + TrackingUtility.getFormatedMinitus(totalTime)
            )
            cadanece = total_Step / TrackingUtility.getFormatedMinitus(totalTime)
            Log.d("cadance", " >>" + cadanece)
            return cadanece.toString();
        }catch(e: ArithmeticException){
//code that handles exception
            binding.llCadance.visibility= View.GONE
            binding.viewCadance.visibility= View.GONE
        }
        return cadanece.toString();
    }


/*
    On average, there are 1265-1515 steps in a kilometer. This will of course depend on your step length. Simply put, your step length is the distance you move with each step. An average step length is 0.79 m (2.6 ft) for men and 0.66 (2.2 ft) for women (Source). That is where my number above comes from; 1265 steps on average for men and 1515 steps for women. If you divide 1000 meters by your step length,
    you will get the number of steps it would take you for a kilometer.
    */
}

