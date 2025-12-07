package com.example.paging.applications.usecases

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.paging.applications.services.paging3.GithubRepoSearchPagingSourceBuilderService
import com.example.paging.applications.services.paging3.GithubRepoSearchRemoteMediatorBuilderService
import com.example.paging.domain.dao.RepoDao
import com.example.paging.domain.entities.Repo
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Property
import org.koin.core.annotation.Single

@Single
class ProvidePagingColdFlowUseCase(
    val repoDao: RepoDao,
    val remoteMediatorBuilder: GithubRepoSearchRemoteMediatorBuilderService,
    val pagingSourceBuilder: GithubRepoSearchPagingSourceBuilderService,
    // DI from properties file specified by `androidFileProperties("paging_setting.properties")` in `MainActivity.kt`
    // It seems that only String type is supported, no type conversion is applied, so you should convert it manually after DI
    @Property("paging.page_size") val pageSize: String,
    @Property("paging.initial_page_size") val initialPageSize: String,
    @Property("paging.use_room_cache") val useRoomCacheStr: String
) {
    @OptIn(ExperimentalPagingApi::class)
    operator fun invoke(
        query: String,
    ): Flow<PagingData<Repo>> {
        val useRoomCache = useRoomCacheStr.toBoolean()
        return Pager(
            config = PagingConfig(
                pageSize = pageSize.toInt(),
                // for infinite scrolling paging, we do not need to set this value (leave it to default 3*pageSize)
                // for paging with exacting paging number, set it equals to the initialPageSize
                // so that we get the same `params.loadSize` equals to `pageSize` here in `GithubRepoSearchPagingSourceBuilder`
                initialLoadSize = initialPageSize.toInt()
            ),
            remoteMediator = if (useRoomCache) remoteMediatorBuilder.build(query) else null
        ) {
            if (useRoomCache)
                repoDao.getRepoPagingSource(query)
            else
                pagingSourceBuilder.build(query)
        }.flow
    }
}