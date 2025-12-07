package com.example.paging.domain

import androidx.room.TypeConverter
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date

class Converters {
    @TypeConverter
    fun timestampToDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun timestampToLocalDateTime(value: Long?): ZonedDateTime? {
        return value?.let {
            ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(it),
                ZoneId.systemDefault()
            )
        }
    }

    @TypeConverter
    fun dateToTimestampMillisecond(dateTime: ZonedDateTime?): Long? {
        return dateTime?.let {
            dateTime.toInstant().toEpochMilli()
        }
    }
}