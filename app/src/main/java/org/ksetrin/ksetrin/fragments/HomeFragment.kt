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
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fondesa.kpermissions.coroutines.sendSuspend
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.github.pwittchen.weathericonview.WeatherIconView
import com.github.pwittchen.weathericonview.library.R.string
import com.google.android.gms.location.LocationServices
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import org.ksetrin.ksetrin.R
import org.ksetrin.ksetrin.helpers.RemindersData
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
    private lateinit var weatherIcon: WeatherIconView
    private lateinit var progressIndicator: CircularProgressIndicator


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
        getSetWeather()
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
        progressIndicator = requireActivity().findViewById(R.id.homeFragmentProgressBar)
    }

    private fun modifyViews() {
        temperatureTextView.visibility = View.INVISIBLE
        feelsLikeTextView.visibility = View.INVISIBLE
        locationTextView.visibility = View.INVISIBLE
        windTextView.visibility = View.INVISIBLE
        humidityTextView.visibility = View.INVISIBLE
        visibilityTextView.visibility = View.INVISIBLE
        weatherIcon.visibility = View.INVISIBLE
        progressIndicator.isIndeterminate = true
        val linearManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = linearManager
        val jsonArray = getReminders()
        val mutableList = jsonArrayToList(jsonArray)
        recyclerView.adapter = RemindersAdapter(mutableList)
    }

    private fun getReminders(): JSONArray {
        val data = sharedPreferences.getString("remindersData", null)
        return if (data != null) {
            JSONArray(data)
        } else {
            JSONArray()
        }
    }

    private fun jsonArrayToList(articles: JSONArray) : MutableList<RemindersData>{
        val mutableList : MutableList<RemindersData> = mutableListOf()
        for (i in 0 until articles.length()){
            val element = JSONObject(articles.getString(i))
            val data = RemindersData(
                element.getString("title"),
                element.getString("time"),
                element.getString("date"),
                element.getString("repeat")
            )
            mutableList.add(data)
        }
        return mutableList
    }

    private fun getSetWeather() = coroutineScope.launch {
        if (!sharedPreferences.contains("weatherData") || isSavedWeatherDataOld()) {
            accessLocation {
                requestApi(it)
                println("Requesting Weather API")
            }
        } else {
            println("Loading Saved Weather Data")
            val jsonString = sharedPreferences.getString("weatherData", "")
            updateWeatherInfo(jsonString.toString())
        }
    }

    private fun requestApi(it: Location) = coroutineScope.launch {
        val response = khttp.get(
            "https://api.openweathermap.org/data/2.5/weather?lat=${it.latitude}&lon=${it.longitude}&appid=${
                getString(R.string.weather_api_key)
            }"
        )
        if (response.statusCode < 400) {
            saveRawData(response.text)
            updateWeatherInfo(response.text)
        }
    }

    private fun updateWeatherInfo(jsonString: String) = coroutineScope.launch(Dispatchers.Main) {
        val jsonObject = JSONObject(jsonString)
        val temp = jsonObject.getJSONObject("main").getDouble("temp") - 273.15
        val feels_like = jsonObject.getJSONObject("main").getDouble("feels_like") - 273.15
        val wind = jsonObject.getJSONObject("wind").getString("speed")
        val humidity = jsonObject.getJSONObject("main").getInt("humidity")
        val visibility = jsonObject.getInt("visibility")
        val placee = jsonObject.getString("name")
        val iconCode =
            jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon")

        temperatureTextView.text = String.format("%.1f", temp) + "°"
        feelsLikeTextView.text = "Feels like " + String.format("%.1f", feels_like) + "°"
        windTextView.text = wind
        humidityTextView.text = humidity.toString()
        visibilityTextView.text = visibility.toString()
        locationTextView.text = placee
        weatherIcon.setIconResource(getWeatherIcon(iconCode))
        progressIndicator.isIndeterminate = false

        temperatureTextView.visibility = View.VISIBLE
        feelsLikeTextView.visibility = View.VISIBLE
        locationTextView.visibility = View.VISIBLE
        windTextView.visibility = View.VISIBLE
        humidityTextView.visibility = View.VISIBLE
        visibilityTextView.visibility = View.VISIBLE
        weatherIcon.visibility = View.VISIBLE
    }

    private fun getWeatherIcon(iconCode: String): String {
        when (iconCode) {
            "01d" -> return getString(string.wi_day_sunny)
            "02d" -> return getString(string.wi_day_cloudy)
            "03d" -> return getString(string.wi_cloud)
            "04d" -> return getString(string.wi_cloudy)
            "09d" -> return getString(string.wi_day_showers)
            "10d" -> return getString(string.wi_day_rain)
            "11d" -> return getString(string.wi_day_thunderstorm)
            "13d" -> return getString(string.wi_day_snow)
            "50d" -> return getString(string.wi_dust)
            "01n" -> return getString(string.wi_night_clear)
            "02n" -> return getString(string.wi_night_alt_cloudy)
            "03n" -> return getString(string.wi_cloud)
            "04n" -> return getString(string.wi_cloudy)
            "09n" -> return getString(string.wi_day_showers)
            "10n" -> return getString(string.wi_night_rain)
            "11n" -> return getString(string.wi_night_thunderstorm)
            "13n" -> return getString(string.wi_night_snow)
            "50n" -> return getString(string.wi_dust)
            else -> return ""
        }
    }

    private fun isSavedWeatherDataOld(): Boolean {
        val jsonString = sharedPreferences.getString("weatherData", "")
        val jsonObject = JSONObject(jsonString.toString())
        val calendar = Calendar.getInstance()
        return calendar.timeInMillis - jsonObject.getLong("time") > 1800000
    }

    @SuppressLint("MissingPermission")
    private suspend fun accessLocation(function: (it: Location) -> Unit) {
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
                    Toast.makeText(requireContext(), "Error getting location", Toast.LENGTH_SHORT)
                        .show()
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

    private suspend fun requestMyPermissions() {
        permissionsBuilder(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).build().sendSuspend()
    }

    private fun saveRawData(rawdata: String?) {
        rawdata?.let {
            val jsonObject = JSONObject(rawdata)
            jsonObject.put("time", Calendar.getInstance().timeInMillis)
            sharedPreferences.edit().putString("weatherData", jsonObject.toString()).apply()
        }
    }
}

