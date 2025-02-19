package com.example.opalwish.room_database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "CartProduct")
data class RoomProductModel(

    @PrimaryKey
    var product_id : String = "",

    @ColumnInfo(name = "productName")
    var productName: String?=null,

    @ColumnInfo(name = "productPrice")
    var productPrice: Double?=null,

    @ColumnInfo(name = "productDisp")
    var productDisp: String?=null,

    @ColumnInfo(name = "productImageUrl")
    var productImageUrl: String?=null,

    @ColumnInfo(name = "isSelected")
    var isSelected: Boolean = false,

): Parcelable