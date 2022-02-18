package org.ksetrin.ksetrin.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import org.ksetrin.ksetrin.NewsData
import org.ksetrin.ksetrin.R
import org.ksetrin.ksetrin.adapters.NewsAdapter
import org.ksetrin.ksetrin.adapters.RemindersAdapter
import java.util.*


class NewsFragment : Fragment() {

    private val coroutineScope =  CoroutineScope(Dispatchers.IO)
    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)

        initViews()
        modifyNews()
        getSetNews()
    }

    private fun initViews(){
        recyclerView = requireActivity().findViewById(R.id.newsFragmentRecyclerView)
    }

    private fun modifyNews(){
        recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = NewsAdapter(mutableListOf())
    }

    private fun getSetNews() = coroutineScope.launch {
        val rawdata = getNews()
        rawdata?.let {
            val jsonObject = JSONObject(it)
            val articles = jsonObject.getJSONArray("articles")
            val list = jsonArrayToList(articles)
            requireActivity().runOnUiThread {
                recyclerView.adapter = NewsAdapter(list)
            }
        }
    }

    
    private fun getNews(): String? {
        if(!sharedPreferences.contains("newsData")  || isSavedNewsDataOld()) {
            println("Requesting News API")
            val response = khttp.get("https://gnews.io/api/v4/search?country=in&lang=en&q=farmer&token=${getString(R.string.news_api_key)}")
            if (response.statusCode>=400) return null
            saveRawData(response.text)
            return response.text
        } else {
            println("Loading Saved News Data")
            return sharedPreferences.getString("newsData", "")
        }
    }

    private fun jsonArrayToList(articles: JSONArray) : MutableList<NewsData>{
        val mutableList : MutableList<NewsData> = mutableListOf()
        for (i in 0 until articles.length()){
            val json = articles.getJSONObject(i)
            val newsData = NewsData(
                json.getString("title"),
                json.getString("description"),
                json.getString("content"),
                json.getString("url"),
                json.getString("image"),
                json.getString("publishedAt"),
                json.getString("source")
            )
            mutableList.add(newsData)
        }
        return mutableList
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