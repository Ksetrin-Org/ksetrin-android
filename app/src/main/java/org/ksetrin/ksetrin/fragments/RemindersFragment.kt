package org.ksetrin.ksetrin.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.json.JSONArray
import org.json.JSONObject
import org.ksetrin.ksetrin.helpers.NewsData
import org.ksetrin.ksetrin.R
import org.ksetrin.ksetrin.adapters.NewsAdapter
import org.ksetrin.ksetrin.adapters.RemindersBigAdapter
import org.ksetrin.ksetrin.helpers.RemindersData


class RemindersFragment : Fragment() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var sharedPreferences : SharedPreferences


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initListeners()
        modifyNews()
        getReminders()
    }

    private fun initViews() {
        sharedPreferences = requireActivity().getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)
        floatingActionButton = requireActivity().findViewById(R.id.remindersFragmentFloatingButton)
        recyclerView = requireActivity().findViewById(R.id.remindersFragmentRecyclerView)
    }

    private fun initListeners() {
        floatingActionButton.setOnClickListener {
            setFragment(AddReminderFragment())
        }
    }

    private fun modifyNews(){
        recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = RemindersBigAdapter(mutableListOf())
    }

    private fun getReminders() {
        val data = sharedPreferences.getString("remindersData", null)
        if (data != null){
            val jsonArray = JSONArray(data)
            println(jsonArray)
            val list = jsonArrayToList(jsonArray)
            activity?.runOnUiThread {
                recyclerView.adapter = RemindersBigAdapter(list)
            }
        }
    }

    private fun jsonArrayToList(articles: JSONArray) : MutableList<RemindersData>{
        val mutableList : MutableList<RemindersData> = mutableListOf()
        for (i in 0 until articles.length()){
            val element = JSONObject(articles.getString(i))
            val data = RemindersData(
                element.getString("title"),
                "",
                element.getString("date"),
                element.getString("time"),
                element.getString("repeat")
            )
            mutableList.add(data)
        }
        return mutableList
    }

    private fun setFragment(fragment: Fragment) {
        val fragmentInFrame = activity?.supportFragmentManager?.findFragmentById(R.id.frameLayout)
        if (fragmentInFrame != null && fragmentInFrame::class == fragment::class) return
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.frameLayout, fragment)
            ?.commit()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reminders, container, false)
    }

}