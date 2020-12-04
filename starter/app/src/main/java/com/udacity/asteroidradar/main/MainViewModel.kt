package com.udacity.asteroidradar.main

import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.AsteroidApiStatus
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDoa

import com.udacity.asteroidradar.database.toModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import kotlin.Exception

class MainViewModel(private val asteroidDao: AsteroidDoa) : ViewModel() {

    private val _image = MutableLiveData<PictureOfDay>()
    fun imageOfDay(): PictureOfDay? {
        return _image.value
    }

    private val _imageOfDayStatus = MutableLiveData<AsteroidApiStatus>()
    val imageOfDayStatus: LiveData<AsteroidApiStatus>
        get() = _imageOfDayStatus

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _asteroidStatus = MutableLiveData<AsteroidApiStatus>()
    val asteroidStatus: LiveData<AsteroidApiStatus>
        get() = _asteroidStatus

    private val _navigateToAsteroidDetails = MutableLiveData<Asteroid>()
    val navigateToAsteroidDetails: LiveData<Asteroid>
        get() = _navigateToAsteroidDetails

    init {
        getImageOfDay()
        getAsteroids(AsteroidFilter.WEEK)
    }

    private fun getImageOfDay() {
        viewModelScope.launch {
            _imageOfDayStatus.value = AsteroidApiStatus.LOADING
            try {
                _image.value = AsteroidApi.retrofitService.getImageOfDay()
                _imageOfDayStatus.value = AsteroidApiStatus.DONE
            } catch (e: Exception) {
                //getPicOfDayFromDB()
                _imageOfDayStatus.value = AsteroidApiStatus.ERROR
            }
        }
    }

/*    private fun getPicOfDayFromDB() {
        val pod = podDao.get(getToday())
        pod.observeForever {
            _image.value = it.toModel()
        }
    }*/

    fun getAsteroids(filter: AsteroidFilter) {
        viewModelScope.launch {
            _asteroidStatus.value = AsteroidApiStatus.LOADING
            try {
                var response: Response<String>? = null

                when (filter) {
                    AsteroidFilter.TODAY -> response = AsteroidApi.retrofitService.getTodaysAsteroids()
                    AsteroidFilter.WEEK -> {
                        val dates = getNextSevenDaysFormattedDates()
                        val startDate = dates[0] // today
                        val endDate = dates[7] // one week from today
                        response = AsteroidApi.retrofitService.getAsteroids(startDate, endDate)
                    }
                    else -> getAsteroidsFromDB()
                }

                if (response != null) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            val jsonResult = JSONObject(it)
                            _asteroids.value = parseAsteroidsJsonResult(jsonResult)
                        }
                    }
                }
                _asteroidStatus.value = AsteroidApiStatus.DONE
            } catch (e: Exception) {
                // no connection: get asteroids from database
                getAsteroidsFromDB()
                _asteroidStatus.value = AsteroidApiStatus.DONE
            }
        }
    }

    private fun getAsteroidsFromDB() {
        val asteroids = asteroidDao.getAsteroids()
        asteroids.observeForever {
            _asteroids.value = it.toModel()
        }
    }

    fun displayAsteroidData(asteroid: Asteroid) {
        _navigateToAsteroidDetails.value = asteroid
    }

    fun displayAsteroidDataComplete() {
        _navigateToAsteroidDetails.value = null
    }
}

enum class AsteroidFilter { TODAY, WEEK, SAVED }

class MainViewModelFactory(private val asteroidDao: AsteroidDoa) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(asteroidDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}