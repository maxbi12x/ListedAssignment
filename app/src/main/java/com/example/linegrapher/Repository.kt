package com.example.linegrapher

import com.example.linegrapher.models.Dashboard
import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.linegrapher.constants.Constants
import com.example.linegrapher.service.Service

class Repository(private val context: Context) {
    val service: Service

    init {
        service = Constants.getInstance().create(Service::class.java)
    }

    private val dashboardMutableData = MutableLiveData<Dashboard>()
    val dashboard: LiveData<Dashboard>
        get() = dashboardMutableData

    suspend fun getService() {

        val result = service.getService("Bearer " + Constants.TOKEN)

        if (result.body() != null) {
            dashboardMutableData.postValue(result.body())
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: Repository? = null

        @Synchronized
        fun getInstance(context: Context): Repository {
            if (instance == null) {
                instance = Repository(context)
            }
            return instance!!
        }

    }
}