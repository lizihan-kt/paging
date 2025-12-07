package com.example.paging.applications.usecases

import com.example.paging.domain.repository.RemoteKeysAndRepoRepository
import org.koin.core.annotation.Single

@Single
class InvalidateOutdatedPagingCacheUseCase(
    val remoteKeysAndRepoRepository: RemoteKeysAndRepoRepository
) {
    suspend operator fun invoke(hours: Long) {
        remoteKeysAndRepoRepository.remoteOutdated(hours)
    }
}