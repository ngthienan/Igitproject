package com.annt.igitproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.annt.igitproject.db.WeatherData
import com.annt.igitproject.model.WeatherAdapter
import com.annt.igitproject.model.WeatherDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class SearchDbActivity : AppCompatActivity(),View.OnClickListener {
    lateinit var rcvSearchWeather : RecyclerView
    lateinit var btnSearch: Button
    var adapter : WeatherAdapter? = null
    var weatherLst : ArrayList<WeatherDataModel> = ArrayList()
    var weatherLstForDb : ArrayList<WeatherData> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_db)
        btnSearch = findViewById(R.id.btnGetWeather)
        rcvSearchWeather = findViewById(R.id.rcv_searchWeather)
        btnSearch.setOnClickListener(this)

        rcvSearchWeather.layoutManager = LinearLayoutManager(this)
        rcvSearchWeather.setHasFixedSize(true)
        rcvSearchWeather.itemAnimator = DefaultItemAnimator()
        rcvSearchWeather.addItemDecoration(
            DividerItemDecoration(
                rcvSearchWeather.context,
                DividerItemDecoration.VERTICAL
            )
        )
        adapter = WeatherAdapter(weatherLst)

        rcvSearchWeather.adapter = adapter

    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btnGetWeather -> {
                Log.d("SearchDbActivity", "onClick event")
                GlobalScope.launch(Dispatchers.IO) {
                    weatherLstForDb = MyApplication.instance.database.productDao().getAll() as ArrayList<WeatherData>

                    weatherLst.clear()
                    weatherLstForDb.forEach { weatherItem ->
                        weatherLst.add(WeatherDataModel(weatherItem.dt, weatherItem.eve, weatherItem.pressure, weatherItem.humidity, weatherItem.description))
                    }

                    withContext(Dispatchers.Main){
                        adapter!!.submitList(weatherLst)
                    }
                }

            }
        }
    }
}