package com.udacity.asteroidradar.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import java.text.SimpleDateFormat
import java.util.*

@JsonClass(generateAdapter = true)
@Entity(tableName = AsteroidDatabase.DATABASE_NAME)
data class AsteroidEnt constructor(
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "codename")
    val codename: String,
    @ColumnInfo(name = "close_approach_date")
    val closeApproachDate: String,
    @ColumnInfo(name = "absolute_magnitude")
    val absoluteMagnitude: Double,
    @ColumnInfo(name = "estimated_diameter")
    val estimatedDiameter: Double,
    @ColumnInfo(name = "relative_velocity")
    val relativeVelocity: Double,
    @ColumnInfo(name = "distance_from_earth")
    val distanceFromEarth: Double,
    @ColumnInfo(name = "is_potentially_hazardous")
    val isPotentiallyHazardous: Boolean
)

fun List<AsteroidEnt>.toModel(): List<Asteroid> {
    return map {
        Asteroid(
            it.id,
            it.codename,
            it.closeApproachDate,
            it.absoluteMagnitude,
            it.estimatedDiameter,
            it.relativeVelocity,
            it.distanceFromEarth,
            it.isPotentiallyHazardous
        )
    }
}

fun List<Asteroid>.toEntity(): Array<AsteroidEnt> {
    return map {
        AsteroidEnt(
            it.id,
            it.codename,
            it.closeApproachDate,
            it.absoluteMagnitude,
            it.estimatedDiameter,
            it.relativeVelocity,
            it.distanceFromEarth,
            it.isPotentiallyHazardous
        )
    }.toTypedArray()
}

/*@JsonClass(generateAdapter = true)
@Entity(tableName = PodDatabase.DATABASE_NAME)
data class PodEnt constructor(
    @PrimaryKey
    var date: String,
    @ColumnInfo(name = "media_type")
    val mediaType: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "url")
    val url: String
)*/

/*
fun PictureOfDay.toEntity(): PodEnt {
    return PodEnt(getToday(), mediaType, title, url)
}

fun PodEnt.toModel(): PictureOfDay {
    return PictureOfDay(mediaType, title, url)
}

fun getToday(): String {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
    return dateFormat.format(calendar.time)
}*/
