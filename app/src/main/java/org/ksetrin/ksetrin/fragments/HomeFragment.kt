package org.ksetrin.ksetrin.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.coroutines.sendSuspend
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import org.json.JSONObject
import org.ksetrin.ksetrin.R
import org.ksetrin.ksetrin.RemindersData
import org.ksetrin.ksetrin.adapters.RemindersAdapter
import java.util.*


class HomeFragment : Fragment() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var temperatureTextView: TextView
    private lateinit var feelsLikeTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var windTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var visibilityTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var weatherIcon: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences =
            requireActivity().getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)
        initViews()
        modifyViews()
        getWeatherData()
    }

    private fun initViews() {
        temperatureTextView = requireActivity().findViewById(R.id.homeFragmentTemperatureTextView)
        feelsLikeTextView = requireActivity().findViewById(R.id.homeFragmentFeelsTextView)
        locationTextView = requireActivity().findViewById(R.id.homeFragmentLocationTextView)
        windTextView = requireActivity().findViewById(R.id.homeFragmentWindTextView)
        humidityTextView = requireActivity().findViewById(R.id.homeFragmentHumidityTextView)
        visibilityTextView = requireActivity().findViewById(R.id.homeFragmentVisibilityTextView)
        recyclerView = requireActivity().findViewById(R.id.homeFragmentRecyclerView)
        weatherIcon = requireActivity().findViewById(R.id.homeFragmentWeatherIcon)
    }

    private fun modifyViews() {
        val linearManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.HORIZONTAL, false
        )
        recyclerView.layoutManager = linearManager
        val a = RemindersData("Title1", "Timeleft1")
        val b = RemindersData("Title2", "Timeleft2")
        recyclerView.adapter = RemindersAdapter(mutableListOf(a, b, a))
    }

    private fun getWeatherData() {
        if (!sharedPreferences.contains("weatherData") || isSavedWeatherDataOld()) {
            getLocation {
                coroutineScope.launch {
                    val jsonObject = requestWeatherApi(it.latitude, it.longitude)
                    jsonObject.put("time", Calendar.getInstance().timeInMillis)
                    sharedPreferences.edit().putString("weatherData", jsonObject.toString()).apply()
                    println("Requesting API")
                    val savedWeatherData = getSavedWeatherData()
                    updateWeatherInfo(savedWeatherData)
                }
            }
        } else {
            val savedWeatherData = getSavedWeatherData()
            updateWeatherInfo(savedWeatherData)
        }
    }

    private fun updateWeatherInfo(savedWeatherData: JSONObject) =
        coroutineScope.launch(Dispatchers.Main) {
            println(savedWeatherData)
            val temp = savedWeatherData.getJSONObject("main").getDouble("temp") - 273.15
            val feels_like = savedWeatherData.getJSONObject("main").getDouble("feels_like") - 273.15
            val wind = savedWeatherData.getJSONObject("wind").getString("speed")
            val humidity = savedWeatherData.getJSONObject("main").getInt("humidity")
            val visibility = savedWeatherData.getInt("visibility")
            val placee = savedWeatherData.getString("name")
            val iconCode = savedWeatherData.getJSONArray("weather").getJSONObject(0).getString("icon")
            var iconUrl = "https://openweathermap.org/img/w/$iconCode.png"

            temperatureTextView.text = String.format("%.1f", temp) + "°"
            feelsLikeTextView.text = "Feels like " + String.format("%.1f", feels_like) + "°"
            windTextView.text = wind
            humidityTextView.text = humidity.toString()
            visibilityTextView.text = visibility.toString()
            locationTextView.text = placee
            Picasso.with(context).load(iconUrl).into(weatherIcon);
        }

    private fun getSavedWeatherData(): JSONObject {
        val jsonString = sharedPreferences.getString("weatherData", "")
        return JSONObject(jsonString.toString())
    }

    private fun isSavedWeatherDataOld(): Boolean {
        val jsonObject = getSavedWeatherData()
        val calendar = Calendar.getInstance()
        return calendar.timeInMillis - jsonObject.getLong("time") > 1800000
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(function: (it: Location) -> Unit) =
        coroutineScope.launch(Dispatchers.Default) {
            if (!isPermissionPresent()) {
                requestMyPermissions()
            }
            if (isPermissionPresent()) {
                val mFusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(requireActivity())
                mFusedLocationClient.lastLocation
                    .addOnSuccessListener {
                        function(it)
                    }
                    .addOnFailureListener {
                        println("Error getting location")
                    }
            }
        }


    private fun isPermissionPresent(): Boolean {
        val permission1 = Manifest.permission.ACCESS_FINE_LOCATION
        val permission2 = Manifest.permission.ACCESS_COARSE_LOCATION
        val res1 = requireContext().checkCallingOrSelfPermission(permission1)
        val res2 = requireContext().checkCallingOrSelfPermission(permission2)
        return res1 == PackageManager.PERMISSION_GRANTED && res2 == PackageManager.PERMISSION_GRANTED
    }

    private suspend fun requestMyPermissions(): Boolean {
        val result = permissionsBuilder(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).build().sendSuspend()
        return result.allGranted()
    }

    private fun requestWeatherApi(lat: Double, lng: Double): JSONObject {
        val jsonString = khttp.get(
            "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lng&appid=${
                getString(R.string.weather_api_key)
            }"
        ).text
        return JSONObject(jsonString)
    }

}