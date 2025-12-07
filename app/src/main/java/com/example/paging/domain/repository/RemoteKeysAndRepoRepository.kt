package com.example.paging.domain.repository

import android.util.Log
import androidx.room.withTransaction
import com.example.paging.domain.AppDatabase
import com.example.paging.domain.dao.RepoAndRemoteKeyDao
import com.example.paging.domain.dao.RepoDao
import com.example.paging.domain.dao.RemoteKeyDao
import com.example.paging.domain.entities.Repo
import com.example.paging.domain.entities.RepoAndRemoteKey
import com.example.paging.domain.entities.RemoteKey
import org.koin.core.annotation.Single

// repository manages the persistence of Aggregates (though the aggregate is not created here, conceptually that is RemoteKeyAndRepos aggregate)
// Anyway, it sits in domain layer to handle the relationship among entities
@Single
class RemoteKeysAndRepoRepository(
    val database: AppDatabase,
    val repoDao: RepoDao,
    val remoteKeyDao: RemoteKeyDao,
    val repoAndRemoteKeyDao: RepoAndRemoteKeyDao,
) {

    suspend fun addByKeyAndRepos(remoteKey: RemoteKey, repo: List<Repo>) {
        database.withTransaction {
            if (remoteKeyDao.queryByLabel(remoteKey.label) == null) {
                remoteKeyDao.insert(remoteKey)
            } else {
                remoteKeyDao.update(remoteKey)
            }
            repoDao.insertAll(repo)
            repoAndRemoteKeyDao.insertAll(repo.map { RepoAndRemoteKey(remoteKey.label, it.id) })
        }
    }

    private suspend fun removeUnreferencedRepos() {
        database.withTransaction {
            val removedCount = repoDao.findUnreferencedReposCount()
            val split = 999
            if (removedCount > split * 10) {
                // remove all caches for efficiency since the number of removed targets is too large
                val keyRemoved = remoteKeyDao.deleteAll()
                val repoRemoved = repoDao.deleteAll()
                Log.i(
                    this::class.simpleName,
                    "All removed: (keyRemoved: $keyRemoved, repoRemoved: $repoRemoved)"
                )
            } else if (removedCount > split) {
                // split and remove
                val repos = repoDao.findUnreferencedRepos()
                (1..(repos.size / split).let { if (repos.size % split > 0) it + 1 else it }).forEach { index ->
                    val removedCount = repoDao.removeUnreferencedReposByIds(
                        repos.subList(
                            (index - 1) * split,
                            (index * split).let { if (it > repos.size) repos.size else it })
                    )
                    Log.i(this::class.simpleName, "removed: $removedCount")
                }
            } else {
                repoDao.removeUnreferencedReposLessThan1000()
            }
        }
    }

    suspend fun removeByLabel(label: String) {
        database.withTransaction {
            remoteKeyDao.deleteByLabel(label)
            removeUnreferencedRepos()
        }
    }

    suspend fun remoteOutdated(hours: Long) {
        database.withTransaction {
            remoteKeyDao.removeOutdatedKeys(hours)
            removeUnreferencedRepos()
        }
    }
}