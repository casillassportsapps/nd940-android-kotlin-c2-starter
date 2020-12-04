package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDoa {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg  asteroids: AsteroidEnt)

    @Query("SELECT * FROM ${AsteroidDatabase.DATABASE_NAME} ORDER BY close_approach_date")
    fun getAsteroids(): LiveData<List<AsteroidEnt>>

    @Query("DELETE FROM ${AsteroidDatabase.DATABASE_NAME} WHERE close_approach_date < :date")
    fun delete(date: String)
}

/*
@Dao
interface PodDoa {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(podEnt: PodEnt)

    @Query("SELECT * FROM ${PodDatabase.DATABASE_NAME} WHERE date = :date")
    fun get(date: String): LiveData<PodEnt>
}*/
