package com.example.paging.applications.usecases

import com.example.paging.domain.repository.RemoteKeysAndRepoRepository
import org.koin.core.annotation.Single

@Single
class InvalidatePagingCacheByLabelUseCase(
    val remoteKeysAndRepoRepository: RemoteKeysAndRepoRepository
) {
    suspend operator fun invoke(label: String) {
        remoteKeysAndRepoRepository.removeByLabel(label)
    }
}