package com.example.linegrapher.service

import com.example.linegrapher.models.Dashboard
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface Service {

    @GET("api/v1/dashboardNew")
    suspend fun getService(@Header("Authorization") token: String): Response<Dashboard>
}