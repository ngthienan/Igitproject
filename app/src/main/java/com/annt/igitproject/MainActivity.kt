package com.annt.igitproject

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.annt.igitproject.model.WeatherAdapter
import com.annt.igitproject.model.WeatherDateModel
import com.google.samples.apps.sunflower.api.WeatherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var weatherService: WeatherService
    var adapter : WeatherAdapter? = null
    var weatherLst : ArrayList<WeatherDateModel> = ArrayList()
    lateinit var editText : EditText
    lateinit var rcvWeather: RecyclerView
    lateinit var btnSearch: Button
    @SuppressLint("NewApi", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editText = findViewById(R.id.edtNameCity)
        rcvWeather = findViewById(R.id.rcv_weather)
        btnSearch = findViewById(R.id.btnGetWeather)
        btnSearch.setOnClickListener(this)
       /* btnSearch.setOnClickListener {
            var textSearch = editText.text
            searchWeather(textSearch.toString())
        }
*/
        rcvWeather.layoutManager = LinearLayoutManager(this)
        rcvWeather.setHasFixedSize(true)
        rcvWeather.itemAnimator = DefaultItemAnimator()
        rcvWeather.addItemDecoration(
            DividerItemDecoration(
                rcvWeather.context,
                DividerItemDecoration.VERTICAL
            )

        )

        weatherService = WeatherService.create()
        adapter = WeatherAdapter(weatherLst)
        rcvWeather.adapter = adapter
    }

    override fun onClick(view: View?) {
        Log.d("MainActivity", "onClick ${view?.id}")
        when (view?.id) {
            R.id.btnGetWeather -> {
               var textSearch = editText.text
                searchWeather(textSearch.toString())
            }
        }

    }

    @SuppressLint("NewApi")
    fun searchWeather(textSearch: String = "SaiGon", numberOfDay: Int = 7) {
        //Handle API
        GlobalScope.launch(Dispatchers.IO) {
            val response = weatherService.getUser(textSearch, numberOfDay, "60c6fbeb4b93ac653c492ba806fc346d").execute()
           // Log.d("MainActivity", "Response: ${response.body()}")
            val body = response.body()
            val listWeather = body?.list
            withContext(Dispatchers.Main){
                weatherLst.clear()
                adapter?.notifyDataSetChanged()
            }

            listWeather?.forEach { item ->
                val timeD = item.dt?.times(1000)?.let { Date(it) }
                val sdf = SimpleDateFormat("EEEE, dd/MMM/yyyy", Locale("en"))
                val dateFormatted = sdf.format(timeD)
                var weatherDateModel = WeatherDateModel(
                    "Date : $dateFormatted",
                    "Average temperature : ${item.temp?.eve}",
                    "Pressure : ${item.pressure}",
                    "Humidity : ${item.humidity}",
                    "Description :${
                        item.weather?.get(
                            0
                        )?.description
                    }"
                )
                weatherLst.add(weatherDateModel)
            }
            withContext(Dispatchers.Main){
                Log.d("MainActivity", "Update RCV: ${weatherLst.size}")
                //adapter!!.notifyDataSetChanged()
                adapter!!.submitList(weatherLst)
            }
        }
        Toast.makeText(this, "Searching weather at $textSearch", Toast.LENGTH_LONG).show()
    }
}