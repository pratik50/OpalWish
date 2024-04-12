package com.example.opalwish.room_database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "CartProduct")
data class RoomProductModel(

    @PrimaryKey
    @NotNull

    var product_id : String,

    @ColumnInfo(name = "productName")
    var productName: String?=null,

    @ColumnInfo(name = "productPrice")
    var productPrice: Double?=null,

    @ColumnInfo(name = "productDisp")
    var productDisp: String?=null,

    @ColumnInfo(name = "productImageUrl")
    var productImageUrl: String?=null
)