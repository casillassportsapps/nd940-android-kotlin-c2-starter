package com.udacity.asteroidradar.repository

import android.util.Log
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.PodDatabase
import com.udacity.asteroidradar.database.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository (private val podDB: PodDatabase, private val asteroidDB: AsteroidDatabase) {

    // store image of day in database
    suspend fun cachePicOfDay() {
        withContext(Dispatchers.IO) {
            val picOfDay = AsteroidApi.retrofitService.getImageOfDay()
            podDB.podDao.insert(picOfDay.toEntity())
        }
    }

    // store asteroids in database and delete yesterdays asteroids
    suspend fun cacheAsteroids() {
        withContext(Dispatchers.IO) {
            val dates = getNextSevenDaysFormattedDates()
            val startDate = dates[0] // today
            val endDate = dates[7] // one week from now
            val response = AsteroidApi.retrofitService.getAsteroids(startDate, endDate)
            if (response.isSuccessful) {
                response.body()?.let {
                    val asteroids = parseAsteroidsJsonResult(JSONObject(it))
                    asteroidDB.asteroidDao.insertAll(*asteroids.toEntity())
                }
            }
            // this will delete all asteroids before today
            asteroidDB.asteroidDao.delete(startDate)
        }
    }
}