package com.example.opalwish.data

data class UserModel(
    var firstName: String,
    var lastName: String,
    var password: String,
    var email: String,
) {
    constructor(): this("","","","")
}