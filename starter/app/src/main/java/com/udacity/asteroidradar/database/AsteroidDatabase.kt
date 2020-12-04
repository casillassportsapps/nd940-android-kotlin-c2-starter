package com.udacity.asteroidradar.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.udacity.asteroidradar.Asteroid

@Database(entities = [AsteroidEnt::class], version = 1, exportSchema = false)
abstract class AsteroidDatabase : RoomDatabase() {

    abstract val asteroidDao: AsteroidDoa

    companion object {
        const val DATABASE_NAME = "asteroid_database"

        private lateinit var INSTANCE: AsteroidDatabase

        fun getDatabase(context: Context): AsteroidDatabase {
            synchronized(this) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AsteroidDatabase::class.java,
                        DATABASE_NAME
                    ).fallbackToDestructiveMigration()
                        .build()
                }
                return INSTANCE
            }
        }
    }
}

/*
@Database(entities = [PodEnt::class], version = 1, exportSchema = false)
abstract class PodDatabase: RoomDatabase() {
    abstract val podDao: PodDoa

    companion object {
        const val DATABASE_NAME = "pod_database"

        private lateinit var INSTANCE: PodDatabase

        fun getDatabase(context: Context): PodDatabase {
            synchronized(this) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        PodDatabase::class.java,
                        DATABASE_NAME
                    ).fallbackToDestructiveMigration()
                        .build()
                }
                return INSTANCE
            }
        }
    }
}*/
