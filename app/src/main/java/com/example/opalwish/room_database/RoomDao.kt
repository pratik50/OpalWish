package com.example.opalwish.room_database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RoomDao  {

    @Insert
    suspend fun insertProduct(product: RoomProductModel)

    @Delete
    suspend fun deleteProduct(product: RoomProductModel)

    @Query("SELECT * FROM cartproduct")
    fun getAllProduct(): LiveData<List<RoomProductModel>>

    @Query("SELECT * FROM cartproduct WHERE product_id = :id")
    fun isExit(id: String): RoomProductModel
}