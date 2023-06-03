package com.example.myapplication.ui

import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

object Repository {
    fun searhEditText() = fire(Dispatchers.IO){
        Result.success(1)
    }
    private fun <T> fire(context: CoroutineContext, block: suspend() -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try{
                block()
            }catch (e: Exception){
                Result.failure<T>(e)
            }
            emit(result)
        }
}