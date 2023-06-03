package com.example.myapplication.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.ImgSeeActivity
import com.example.myapplication.R
import com.example.myapplication.ui.Data.Picture
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.picture_item.view.*

class PictureAdapter(
    val context: Context,
    val ImgList: List<Picture>,
    val selectButton: Button,
    val rv: RecyclerView
) :
    RecyclerView.Adapter<PictureAdapter.ViewHolder>() {

    val checkimgList: ArrayList<ImageView> = ArrayList()
    val shareimgList: ArrayList<Int> = ArrayList()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img_pic: ImageView = view.findViewById(R.id.image_view)
        val check_img: ImageView = view.findViewById<ImageView?>(R.id.checkimg)

        init {
            img_pic.setOnClickListener {
                if(selectButton.text == "取消"){
                    ImgList[adapterPosition].isSelected = true
                    ImgList[adapterPosition].isChecked = !ImgList[adapterPosition].isChecked
                    if(check_img.visibility == View.VISIBLE){
                        check_img.visibility = View.GONE
                    }else{
                        check_img.visibility = View.VISIBLE
                    }
                }else{
                    val intent = Intent(context, ImgSeeActivity::class.java)
                    intent.putExtra("Img", ImgList[adapterPosition].imageId)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.picture_item, parent, false)
        val viewholder = ViewHolder(view)

        return viewholder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pic = ImgList[position]
        Glide.with(context).load(pic.imageId).into(holder.img_pic)
        checkimgList.add(holder.check_img)
        if(pic.isSelected){
            if(pic.isChecked == true){
                holder.itemView.checkimg.visibility = View.VISIBLE
            }else{
                holder.itemView.checkimg.visibility = View.GONE
            }
        }else{
            holder.itemView.checkimg.visibility = View.GONE
        }
    }

    override fun getItemCount() = ImgList.size

    fun doWithAllCheckBox(check: Boolean, vis: Int) {
        for (i in 0 until ImgList.size) {
            ImgList[i].isChecked = check
            ImgList[i].isSelected = check
        }
        for(j in 0 until checkimgList.size){
            checkimgList[j].visibility = vis
        }
    }

    // 获取选择的图片信息
    fun canSharedPictures(): ArrayList<Int>{
        for(i in 0 until ImgList.size){
            if(ImgList[i].isChecked){
                shareimgList.add(ImgList[i].imageId)
            }
        }
        return shareimgList
    }

    fun Refresh(){
        rv.scrollToPosition(0)
    }
}