package com.example.opalwish.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PinCodeServices {
    @GET("pincode/{pincode}")
    suspend fun getPincodeDetails(@Path("pincode") pincode: String): Response<List<PinCodeResponse>>
}