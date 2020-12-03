package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.PodDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException

class AsteroidWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "AsteroidWorker"
    }

    override suspend fun doWork(): Result {
        val podDatabase = PodDatabase.getDatabase(applicationContext)
        val asteroidDatabase = AsteroidDatabase.getDatabase(applicationContext)
        val repo = AsteroidRepository(podDatabase, asteroidDatabase)

        return try {
            repo.cachePicOfDay()
            repo.cacheAsteroids()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}