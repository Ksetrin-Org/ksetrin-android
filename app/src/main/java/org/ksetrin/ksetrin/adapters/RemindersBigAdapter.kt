package org.ksetrin.ksetrin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.ksetrin.ksetrin.R
import org.ksetrin.ksetrin.helpers.RemindersData

class RemindersBigAdapter(private val data: MutableList<RemindersData>) : RecyclerView.Adapter<RemindersBigAdapter.ViewHolder>() {
    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val title : TextView = view.findViewById(R.id.eachReminderBigTitle)
        val date : TextView = view.findViewById(R.id.eachReminderBigDate)
        val time : TextView = view.findViewById(R.id.eachReminderBigTime)
        val repeat : TextView = view.findViewById(R.id.eachReminderBigRepeat)
        val delete : ImageButton = view.findViewById(R.id.eachReminderBigDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.each_reminder_big,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = data[position].title
        holder.date.text = data[position].date
        holder.time.text = data[position].time
        holder.repeat.text = data[position].repeat
        holder.delete.setOnClickListener {
            data.removeAt(position)
            this.notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}