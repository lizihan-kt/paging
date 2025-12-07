package com.example.paging.applications.services.paging3

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.paging.applications.services.interfaces.RepoRemoteSearchService
import com.example.paging.domain.entities.Repo
import org.koin.core.annotation.Single
import kotlin.Int

// Use this one when RemoteMediator and Room cache is not needed
@Single
class GithubRepoSearchPagingSourceBuilderService(
    val repoRemoteSearchService: RepoRemoteSearchService
) {
    fun build(query: String, initialPageKey: Int = 1) = object : PagingSource<Int, Repo>() {
        override fun getRefreshKey(state: PagingState<Int, Repo>): Int? {
            return state.lastAccessedKey({ it + 1 }, { it - 1 })
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> {
            Log.i(
                this::class.simpleName,
                "load() is called with [key: ${params.key}, loadSize: ${params.loadSize}"
            )

            return try {
                val pageKey = params.key ?: initialPageKey
                val response =
                    repoRemoteSearchService.searchRepos(query, pageKey, params.loadSize)
                return LoadResult.Page(
                    data = response.items,
                    prevKey = if (pageKey - 1 > 0) pageKey - 1 else null,
                    nextKey = if (response.totalCount > params.loadSize * pageKey) pageKey + 1 else null
                ).apply {
                    Log.i(
                        this@GithubRepoSearchPagingSourceBuilderService::class.simpleName,
                        "LoadResult.Page: $this"
                    )
                }
            } catch (e: Exception) {
                Log.e(this::class.simpleName, e.stackTraceToString())
                LoadResult.Error(e)
            }
        }
    }
}