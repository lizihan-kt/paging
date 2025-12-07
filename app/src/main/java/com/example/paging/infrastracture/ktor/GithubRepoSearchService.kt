package com.example.paging.infrastracture.ktor

import android.util.Log
import com.example.paging.applications.services.interfaces.RepoRemoteSearchService
import com.example.paging.applications.services.interfaces.RepoSearchResponse
import com.example.paging.infrastracture.ktor.requests.GithubApiRepositoriesRequest
import com.example.paging.infrastracture.ktor.requests.GithubApiSearchRequest
import com.example.paging.domain.entities.Repo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class GithubRepoSearchService(@Named("ktorGithubApiClient") val ktorHttpClient: HttpClient) :
    RepoRemoteSearchService {
    override suspend fun searchRepos(
        queryInput: String,
        page: Int,
        itemsPerPage: Int
    ): RepoSearchResponse {
        Log.i(this::class.simpleName, "queryInput in searchRepos(): $queryInput")
        val response = ktorHttpClient.get(
            GithubApiSearchRequest.Repository.toRequestObject(
                queryInput,
                page,
                itemsPerPage
            )
        ).body<RepoSearchResponse>()
        Log.i(this::class.simpleName, "response: $response")
        return response
    }

    override suspend fun getRepoById(id: Long): Repo {
        val response = ktorHttpClient.get(GithubApiRepositoriesRequest.Id(id)).body<Repo>()
        return response
    }
}