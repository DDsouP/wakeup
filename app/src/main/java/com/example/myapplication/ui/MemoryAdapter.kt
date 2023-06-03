package com.example.myapplication.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.ImgSeeActivity
import com.example.myapplication.R
import com.example.myapplication.TopBase
import com.example.myapplication.ui.home.HomeFragment

class MemoryAdapter(val context: HomeFragment, private val memorysList: MutableList<Uri>):
    RecyclerView.Adapter<MemoryAdapter.ViewHolder>(){

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val imageView: ImageView = view.findViewById(R.id.memory_pic)

        init{
            imageView.setOnClickListener {
                val intent = Intent(TopBase.context, ImgSeeActivity::class.java)
                val uri = Uri.parse(memorysList[adapterPosition].toString())
                intent.setData(uri)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                TopBase.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.memory_item, parent, false)
        val holder = ViewHolder(view)

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = memorysList[position]
        Glide.with(context).load(pos).into(holder.imageView)
    }

    override fun getItemCount() = memorysList.size

}