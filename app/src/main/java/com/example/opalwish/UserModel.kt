package com.example.opalwish

class UserModel {
    private var fistName: String = ""
    private var lastName: String = ""
    private var password: String = ""
    private var email: String = ""

    constructor(fistName: String, lastName: String, password: String, email: String) {
        this.fistName = fistName
        this.lastName = lastName
        this.password = password
        this.email = email
    }

    constructor()
}