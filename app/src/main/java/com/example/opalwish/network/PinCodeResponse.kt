package com.example.opalwish.network

data class PinCodeResponse(
    val Status: String,
    val PostOffice: List<PostOffice>?
)

data class PostOffice(
    val Name: String?,
    val District: String?,
    val State: String?,
    val Country: String?
)
