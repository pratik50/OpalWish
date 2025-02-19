package com.example.opalwish.room_database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RoomDao  {

    @Insert
    suspend fun insertProduct(product: RoomProductModel)

    @Query("DELETE  FROM cartproduct WHERE product_id = :id")
    suspend fun deleteProduct(id: String)

    @Query("SELECT * FROM cartproduct")
    fun getAllProduct(): LiveData<MutableList<RoomProductModel>>

    @Query("SELECT * FROM cartproduct WHERE product_id = :id")
    fun isExit(id: String): RoomProductModel

    @Query("SELECT * FROM cartproduct")
    suspend fun getAllProductOnce(): List<RoomProductModel>

    @Update
    suspend fun updateProduct(product: RoomProductModel)

}