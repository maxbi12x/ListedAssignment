package com.example.linegrapher

import com.example.linegrapher.models.Dashboard
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyViewModel(private val repository: Repository, context: Context) : ViewModel() {
    init {

    }

    fun getService() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getService()
        }
    }

    val dashboard: LiveData<Dashboard>
        get() = repository.dashboard
}