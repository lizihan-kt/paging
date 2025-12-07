package com.example.paging.domain.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "repo_and_repo_remote_key",
    primaryKeys = ["label", "repoId"],
    foreignKeys = [
        ForeignKey(
            entity = Repo::class,
            parentColumns = ["id"],
            childColumns = ["repoId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = RemoteKey::class,
            parentColumns = ["label"],
            childColumns = ["label"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["label"]), Index("repoId")]
)
data class RepoAndRemoteKey(
    val label: String,
    val repoId: Long
)