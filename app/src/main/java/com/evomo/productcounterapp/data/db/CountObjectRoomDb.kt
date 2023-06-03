package com.evomo.productcounterapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CountObject::class], version = 1)
abstract class CountObjectRoomDb : RoomDatabase() {
    abstract fun countObjectDao() : CountObjectDao

    companion object {
        @Volatile
        private var INSTANCE: CountObjectRoomDb? = null

        @JvmStatic
        fun getDatabase(context: Context): CountObjectRoomDb {
            if (INSTANCE == null) {
                synchronized(CountObjectRoomDb::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        CountObjectRoomDb::class.java, "count_database")
                        .build()
                }
            }
            return INSTANCE as CountObjectRoomDb
        }
    }
}