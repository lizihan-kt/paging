package com.example.paging.presentation.viewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.filter
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.paging.applications.usecases.InvalidatePagingCacheByLabelUseCase
import com.example.paging.applications.usecases.ProvidePagingColdFlowUseCase
import com.example.paging.domain.entities.Repo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class PagingViewModel(
    val providePagingFlowUseCase: ProvidePagingColdFlowUseCase,
    val invalidatePagingCacheByLabelUseCase: InvalidatePagingCacheByLabelUseCase
) : ViewModel() {
    private var pagingDataFlowScope: CoroutineScope? = null
    private val _pagingDataFlow = MutableStateFlow<Flow<PagingData<UiModel>>?>(null)
    val pagingDataFlow: StateFlow<Flow<PagingData<UiModel>>?>
        get() = _pagingDataFlow.asStateFlow()

    // change the Flow<PagingData> after searching text is changed
    fun setPagingDataFlowByQuery(query: String) {
        // cancel previous flow collection to avoid memory leak
        pagingDataFlowScope?.cancel()
        if (query.isEmpty()) {
            _pagingDataFlow.value = null // null for empty input
        } else {
            // start a new collection of Flow<PagingData<Repo>> in a new child coroutine of the viewModelScope
            viewModelScope.launch {
                pagingDataFlowScope = this
                _pagingDataFlow.value =
                    providePagingFlowUseCase(query)
                        .map { pagingData -> // apply Flow.map() multiple times
                            // you can filter(), map(), insertSeparators() to adjust the original PagingData<Repo>
                            pagingData
                                .filter { true }
                                .map { UiModel.RepoItem(it) }
                                .insertSeparators { before, after ->
                                    if (after == null) null
                                    else if (before == null) {
                                        if (after.repo.stars >= 10000) {
                                            UiModel.SeparatorItem("10,000+ stars")
                                        } else {
                                            UiModel.SeparatorItem("< 10,000 stars")
                                        }
                                    } else {
                                        if (before.repo.stars >= 10000 && after.repo.stars < 10000) {
                                            UiModel.SeparatorItem("< 10,000 stars")
                                        } else null
                                    }
                                }
                        }
                        .cachedIn(viewModelScope)
            }
        }
    }

    suspend fun invalidateQueryCache(query: String) {
        invalidatePagingCacheByLabelUseCase(query)
        setPagingDataFlowByQuery(query)
    }

    // Plain state holder class
    class PagingUIState(
        val pagingDataFlow: Flow<PagingData<UiModel>>?,
        val pagingItems: LazyPagingItems<UiModel>?,
        var queryText: String
    ) {
        val isNonEmptySearchPerformed: Boolean
            get() = pagingDataFlow != null
        val isSearchResultEmpty: Boolean
            get() = isNonEmptySearchPerformed && (pagingItems?.itemCount ?: 0) != 0

        operator fun component1() = pagingDataFlow
        operator fun component2() = pagingItems
        operator fun component3() = queryText
    }

    @Composable
    fun rememberPagingUIState(pagingDataFlow: Flow<PagingData<UiModel>>?): PagingUIState {
        val pagingItems = pagingDataFlow?.collectAsLazyPagingItems()
        var queryText by rememberSaveable { mutableStateOf("") } // state

        return PagingUIState(pagingDataFlow, pagingItems, queryText)
    }
}

sealed class UiModel {
    data class RepoItem(val repo: Repo) : UiModel()
    data class SeparatorItem(val description: String) : UiModel()
}