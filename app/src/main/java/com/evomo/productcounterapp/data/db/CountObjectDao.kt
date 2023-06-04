package com.evomo.productcounterapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CountObjectDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(countObject: CountObject)

    @Update
    fun update(countObject: CountObject)

    @Delete
    fun delete(countObject: CountObject)

    @Query("SELECT * from countObject ORDER BY id ASC")
    fun getAllCount(): LiveData<List<CountObject>>

    @Query("SELECT * from countObject ORDER BY date DESC")
    fun getAllCountByDate(): LiveData<List<CountObject>>
}