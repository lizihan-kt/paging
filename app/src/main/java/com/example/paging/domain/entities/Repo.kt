package com.example.paging.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "repos")
@Serializable
data class Repo(
    @PrimaryKey val id: Long,
    val name: String,    // query string like "Android"
    @SerialName("full_name") @ColumnInfo(name = "full_name") val fullName: String,
    val description: String?,
    val url: String,
    @SerialName("stargazers_count") val stars: Int,
    val forks: Int,
    val language: String?,
)