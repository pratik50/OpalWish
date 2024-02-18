package com.example.opalwish

class UserModel {
     var fistName: String = ""
     var lastName: String = ""
     var password: String = ""
     var email: String = ""

    constructor(fistName: String, lastName: String, password: String, email: String) {
        this.fistName = fistName
        this.lastName = lastName
        this.password = password
        this.email = email
    }

    constructor()
}