package com.example.paging.domain.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.paging.domain.entities.Repo
import com.example.paging.domain.entities.RepoAndRemoteKey

@Dao
interface RepoAndRemoteKeyDao {
    @Query(
        """
        SELECT r.* FROM repos r JOIN repo_and_repo_remote_key rarrk ON r.id = rarrk.repoId WHERE label=:label
    """
    )
    suspend fun getReposByLabel(label: String): List<Repo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(repoAndKey: List<RepoAndRemoteKey>)
}