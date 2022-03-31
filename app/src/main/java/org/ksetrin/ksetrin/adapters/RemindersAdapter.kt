package org.ksetrin.ksetrin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.ksetrin.ksetrin.R
import org.ksetrin.ksetrin.helpers.RemindersData

class RemindersAdapter(private val data: MutableList<RemindersData>) : RecyclerView.Adapter<RemindersAdapter.ViewHolder>() {
    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val textView1 : TextView = view.findViewById(R.id.eachReminderTextView1)
        val textView2 : TextView = view.findViewById(R.id.eachReminderTextView2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.each_reminder,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView1.text = data[position].title
        holder.textView2.text = data[position].timeleft
    }

    override fun getItemCount(): Int {
        return data.size
    }

}