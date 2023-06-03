package com.example.myapplication.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.myapplication.TopBase
import com.example.myapplication.showToast
import com.example.myapplication.ui.Repository

class DashboardViewModel: ViewModel() {

    val searchFlag = MutableLiveData<Int>()

    var count = 1

    init{
        searchFlag.value = 1
    }

    val searchLiveData = Transformations.switchMap(searchFlag){
        query -> Repository.searhEditText()
    }

    fun ChangeFlag(){
        count++
        searchFlag.postValue(count)
    }

}