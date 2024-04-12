package com.example.opalwish.data

data class ProductModel(
    var product_id : String?=null,
    var name: String?=null,
    var price: Double?=null,
    var disp: String?=null,
    var details: String?=null,
    var imageUrl: String?=null,
    var selectedQuantity: String?=null,
    var productCode: String?=null
)