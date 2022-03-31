package org.ksetrin.ksetrin.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.JsonObject
import org.json.JSONArray
import org.ksetrin.ksetrin.R

class AddReminderFragment : Fragment() {

    private lateinit var title : EditText
    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker
    private lateinit var chipGroup: ChipGroup
    private lateinit var button: Button
    private lateinit var sharedPreferences : SharedPreferences


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
    }

    private fun initViews(){
        sharedPreferences = requireActivity().getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)
        title = requireActivity().findViewById(R.id.addReminderTitle)
        datePicker = requireActivity().findViewById(R.id.addReminderDate)
        timePicker = requireActivity().findViewById(R.id.addReminderTime)
        chipGroup = requireActivity().findViewById(R.id.addReminderChipGroup)
        button = requireActivity().findViewById(R.id.addReminderButton)
    }

    private fun initListeners() {
        button.setOnClickListener {
            val jsonArray = getReminders()
            val jsonObject = JsonObject()
            jsonObject.addProperty("title", title.text.toString())
            val date = datePicker.dayOfMonth.toString() + " " + datePicker.month.toString()
            jsonObject.addProperty("date", date)
            val time = timePicker.currentHour.toString() + " " + timePicker.currentMinute.toString()
            jsonObject.addProperty("time", time)
            val chip = requireActivity().findViewById<Chip>(chipGroup.checkedChipId)
            jsonObject.addProperty("repeat", chip.text.toString())
            jsonArray.put(jsonObject)
            sharedPreferences.edit().putString("remindersData", jsonArray.toString()).apply()
        }
    }

    private fun getReminders(): JSONArray {
        val data = sharedPreferences.getString("remindersData", null)
        return if (data != null) {
            JSONArray(data)
        } else {
            JSONArray()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_reminder, container, false)
    }

}