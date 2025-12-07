package com.example.paging.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity(
    tableName = "remote_keys",
    indices = [Index("created_at")]
)
data class RemoteKey(
    @PrimaryKey val label: String, // query Input
    val key: Int,
    @ColumnInfo("next_key") val nextKey: Int?,
    // invalidate cache for created_at
    @ColumnInfo("created_at") val createdAt: ZonedDateTime
)