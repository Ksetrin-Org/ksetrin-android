package org.ksetrin.ksetrin.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.ksetrin.ksetrin.R
import java.util.*


class NewsFragment : Fragment() {

    private val coroutineScope =  CoroutineScope(Dispatchers.IO)
    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)
        getSetNews()
    }

    private fun getSetNews() = coroutineScope.launch {
        val rawdata = getNews()
        println(rawdata)
    }
    
    private fun getNews(): String? {
        if(!sharedPreferences.contains("newsData")  || isSavedNewsDataOld()) {
            println("Requesting API")
            val response = khttp.get("https://gnews.io/api/v4/search?country=in&lang=en&q=farmer&token=${getString(R.string.news_api_key)}")
            if (response.statusCode>=400) return null
            saveRawData(response.text)
            return response.text
        } else {
            println("Loading Saved Data")
            return sharedPreferences.getString("weatherData", "")
        }
    }

    private fun isSavedNewsDataOld(): Boolean {
        val jsonString = sharedPreferences.getString("weatherData", "")
        val jsonObject = JSONObject(jsonString.toString())
        val calendar = Calendar.getInstance()
        return calendar.timeInMillis - jsonObject.getLong("time") > 3600000
    }

    private fun saveRawData(rawdata: String?) {
        rawdata?.let {
            val jsonObject = JSONObject(rawdata)
            jsonObject.put("time", Calendar.getInstance().timeInMillis)
            sharedPreferences.edit().putString("newsData", jsonObject.toString()).apply()
        }
    }
}