package com.example.opalwish.room_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RoomProductModel::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun RoomDao(): RoomDao

    companion object{

        private var database: AppDatabase?=null
        private var DATABASE_NAME = "CartProducts.db"

        @Synchronized
        fun getInstance(context: Context): AppDatabase{

            if(database == null){

                database = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME).fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
            }
            return database!!
        }
    }

}