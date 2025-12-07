package com.example.paging.applications.services.paging3

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.paging.applications.services.interfaces.RepoRemoteSearchService
import com.example.paging.applications.usecases.InvalidateOutdatedPagingCacheUseCase
import com.example.paging.applications.usecases.InvalidatePagingCacheByLabelUseCase
import com.example.paging.domain.repository.RemoteKeysAndRepoRepository
import com.example.paging.domain.dao.RemoteKeyDao
import com.example.paging.domain.entities.Repo
import com.example.paging.domain.entities.RemoteKey
import org.koin.core.annotation.Property
import org.koin.core.annotation.Single
import java.time.ZonedDateTime

@Single
class GithubRepoSearchRemoteMediatorBuilderService(
    val githubRepoSearchService: RepoRemoteSearchService,
    val invalidateOutdatedPagingCacheUseCase: InvalidateOutdatedPagingCacheUseCase,
    val invalidatePagingCacheByLabelUseCase: InvalidatePagingCacheByLabelUseCase,
    val remoteKeyDao: RemoteKeyDao,
    val remoteKeysAndRepoRepository: RemoteKeysAndRepoRepository,
    @Property("remote_mediator.cache_valid_hours") val cacheValidHours: String,
) {
    // remoteKeyLabel can be query input
    @OptIn(ExperimentalPagingApi::class)
    fun build(remoteKeyLabel: String, initialPageKey: Int = 1) =
        object : RemoteMediator<Int, Repo>() {
            override suspend fun initialize(): InitializeAction {
                val lastAccessed = remoteKeyDao.queryByLabel(remoteKeyLabel)?.createdAt
                // cache valid for 24 hours
                return if (lastAccessed != null && lastAccessed.plusHours(cacheValidHours.toLong()) > ZonedDateTime.now()) {
                    InitializeAction.SKIP_INITIAL_REFRESH
                } else {
                    // Cache invalid, clear DB cache first!
                    // Note if you decide to run LAUNCH_INITIAL_REFRESH, you should clear DB cache first
                    // Otherwise some old cache from previous requests may mix with the new cache in your DB
                    // which may causes the screen flickers on the first load

                    // Because the old cache is from Room DB, it is displayed immediately,
                    // the new cache is from RemoteMediator and displayed later when new cache is stored in Room and invalidate the previous PagingSource
                    // which causes an extra reload in your page which is annoying.
                    // To avoid that, clear all the possible the old cache if you decide to `LAUNCH_INITIAL_REFRESH`
                    invalidateOutdatedPagingCacheUseCase(cacheValidHours.toLong())
                    InitializeAction.LAUNCH_INITIAL_REFRESH
                }
            }

            // load more data from the network when either the Pager runs out of data or the existing data is invalidated.
            override suspend fun load(
                loadType: LoadType,
                state: PagingState<Int, Repo>
            ): MediatorResult {
                return try {
                    val pageSize = state.config.pageSize
                    val loadKey: Int? = when (loadType) {
                        LoadType.APPEND -> {
                            Log.i(this::class.simpleName, "APPEND is called!")
                            // use repoRemoteKeyDao to get the key (which is page number here) of the last RemoteMediator load from network
                            // Note APPEND request always runs after `LoadType.REFRESH`
                            // So `repoRemoteKeyDao.queryByLabel(remoteKeyLabel)` must not be null
                            remoteKeyDao.queryByLabel(remoteKeyLabel)!!.nextKey
                        }

                        LoadType.REFRESH -> { // for initial load or the result of `PagingSource` invalidation
                            Log.i(this::class.simpleName, "REFRESH is called!")
                            // clear all the relevant cache first when refresh happens
                            invalidatePagingCacheByLabelUseCase(remoteKeyLabel)
                            initialPageKey
                        }
                        // In this example (LazyColumn, infinite scrolling), you never need to prepend, since REFRESH will always load the first page in the list.
                        // However, somehow, PREPEND happens, you may immediately return null or do the calculation below, reporting end of pagination for prepend.
                        // Note do not return `initialPageKey` here, otherwise, the `initialPageKey` will be requested by `githubRepoSearchService` all the time
                        // and finally you will get `invalid: 403 rate limit exceeded` from GitHub API!
                        LoadType.PREPEND -> {
                            Log.i(this::class.simpleName, "PREPEND is called!")
                            val lastAccessedKey = state.lastAccessedKey({ it + 1 }, { it - 1 })
                            if (lastAccessedKey == null || lastAccessedKey - 1 < initialPageKey) null
                            else lastAccessedKey
                        }
                    }
                    Log.i(this::class.simpleName, "load is called with key: $loadKey")

                    if (loadKey != null) {
                        // load data, we ignore the handling of `incompleteResults` here
                        val (totalCount, items) = githubRepoSearchService.searchRepos(
                            remoteKeyLabel,
                            loadKey,
                            pageSize
                        )
                        val nextKey = if (totalCount > loadKey * pageSize) loadKey + 1 else null

                        remoteKeysAndRepoRepository.addByKeyAndRepos(
                            RemoteKey(
                                remoteKeyLabel,
                                loadKey,
                                nextKey,
                                ZonedDateTime.now()
                            ),
                            items
                        )
                        MediatorResult.Success(endOfPaginationReached = nextKey == null)
                    } else {
                        MediatorResult.Success(endOfPaginationReached = true)
                    }
                } catch (e: Exception) {
                    Log.e(this::class.simpleName, e.stackTraceToString())
                    MediatorResult.Error(e)
                }
            }
        }
}