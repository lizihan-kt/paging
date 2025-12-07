package com.example.paging.applications.usecases

import com.example.paging.applications.services.interfaces.RepoRemoteSearchService
import com.example.paging.domain.dao.RepoDao
import com.example.paging.domain.entities.Repo
import org.koin.core.annotation.Property
import org.koin.core.annotation.Single

@Single
class FetchRepoUseCase(
    @Property("paging.use_room_cache") val useRoomCache: String,
    val repoRemoteSearchService: RepoRemoteSearchService,
    // just call DAO here instead of wrap the simple DAO method in a repository, which looks verbose and meaningless!
    val repoDao: RepoDao
) {
    suspend operator fun invoke(repoId: Long): Repo {
        return if (useRoomCache.toBoolean()) {
            repoDao.getRepoById(repoId)
        } else {
            repoRemoteSearchService.getRepoById(repoId)
        }
    }
}