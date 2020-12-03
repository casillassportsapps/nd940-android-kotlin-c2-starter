package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.reflect.Type

enum class AsteroidApiStatus { LOADING, DONE }

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(HandleScalarAndJsonConverterFactory.create())
    .baseUrl(Constants.BASE_URL)
    .build()

@Target(AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class Json

@Target(AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class Scalar

interface AsteroidApiService {

    @Json
    @GET("planetary/apod?api_key=${Constants.API_KEY}")
    suspend fun getImageOfDay(): PictureOfDay

    @Scalar
    @GET("neo/rest/v1/feed/today?detailed=true&api_key=${Constants.API_KEY}")
    suspend fun getTodaysAsteroids(): Response<String>

    @Scalar
    @GET("neo/rest/v1/feed?")
    suspend fun getAsteroids(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") key: String = Constants.API_KEY): Response<String>
}

object AsteroidApi {
    val retrofitService: AsteroidApiService by lazy { retrofit.create(AsteroidApiService::class.java) }
}

class HandleScalarAndJsonConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        annotations.forEach { annotation ->
            return when (annotation) {
                is Scalar -> ScalarsConverterFactory.create().responseBodyConverter(type, annotations, retrofit)
                is Json -> MoshiConverterFactory.create(moshi).responseBodyConverter(type, annotations, retrofit)
                else -> null
            }
        }
        return null
    }
    companion object {
        fun create() = HandleScalarAndJsonConverterFactory()
    }
}