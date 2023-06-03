package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_memory_detail2.*

class Memory_detail_Activity : AppCompatActivity() {

    companion object{
        const val MEMORY_NAME = "memory_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory_detail2)
        val memory = intent.getStringExtra(MEMORY_NAME)
        setSupportActionBar(TOOLBAR)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        collapsingToolbar.title = "My Memory_Capsule~"
        //Glide.with(this).load(fruitImageId).into(fruitView)
        //fruitText.text = generateFruitContent(fruitName)
        fruitText.setText("asdhaskjfhsidjfhsuidfhsuidfnsdunfskdf".repeat(100))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}