package com.example.paging.domain.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.paging.domain.entities.Repo


// DAO works a repository here
@Dao
interface RepoDao {
    // you do not need to set the limit and offset in the SQL, that is handled by paging3
    @Query(
        """
        SELECT r.* FROM repo_and_repo_remote_key rarrk JOIN repos r on rarrk.repoId=r.id
        WHERE rarrk.label = :queryInput
        ORDER BY r.stars DESC
    """
    )
    fun getRepoPagingSource(queryInput: String): PagingSource<Int, Repo>

    @Query("DELETE FROM repos")
    suspend fun deleteAll(): Int

    // This may crash for more than 1000 items in WHERE IN
    @Query(
        """
        DELETE FROM repos WHERE id in (
            SELECT r.id FROM repos r LEFT JOIN repo_and_repo_remote_key rarrk on r.id=rarrk.repoId
            WHERE rarrk.repoId is NULL
        )
    """
    )
    suspend fun removeUnreferencedReposLessThan1000(): Int

    // make sure the size of repoIds is no more than 999
    @Query("DELETE FROM repos WHERE id in (:repoIds)")
    suspend fun removeUnreferencedReposByIds(repoIds: List<Long>): Int

    @Query(
        """
        SELECT r.id FROM repos r 
            LEFT JOIN repo_and_repo_remote_key rarrk on r.id=rarrk.repoId
        WHERE rarrk.repoId is NULL"""
    )
    suspend fun findUnreferencedRepos(): List<Long>

    @Query(
        """
        SELECT count(r.id) FROM repos r 
            LEFT JOIN repo_and_repo_remote_key rarrk on r.id=rarrk.repoId
        WHERE rarrk.repoId is NULL
    """
    )
    suspend fun findUnreferencedReposCount(): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repo: List<Repo>): List<Long>

    @Upsert
    suspend fun upsertAll(repo: List<Repo>): List<Long>

    @Query("SELECT * FROM repos WHERE id = :repoId")
    suspend fun getRepoById(repoId: Long): Repo
}