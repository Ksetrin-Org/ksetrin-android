package org.ksetrin.ksetrin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.ksetrin.ksetrin.helpers.NewsData
import org.ksetrin.ksetrin.R

class NewsAdapter(private val data: MutableList<NewsData>) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val textView1 : TextView = view.findViewById(R.id.eachNewsTextView1)
        val textView2 : TextView = view.findViewById(R.id.eachNewsTextView2)
        val imageView : ImageView = view.findViewById(R.id.eachNewsImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.each_news, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView1.text = data[position].title
        holder.textView2.text = data[position].description
        Picasso.with(holder.imageView.context).load(data[position].image).into(holder.imageView);
    }

    override fun getItemCount(): Int {
        return data.size
    }
}