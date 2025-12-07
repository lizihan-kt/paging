package com.example.paging.applications.services.interfaces

import com.example.paging.domain.entities.Repo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface RepoRemoteSearchService {
    suspend fun searchRepos(queryInput: String, page: Int, itemsPerPage: Int): RepoSearchResponse
    suspend fun getRepoById(id: Long): Repo
}

@Serializable
data class RepoSearchResponse(
    @SerialName("total_count") val totalCount: Int = 0,
    val items: List<Repo> = emptyList(),
)