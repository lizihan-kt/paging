package com.example.paging.presentation.composable

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.paging.R
import com.example.paging.presentation.composable.components.PageTitle
import com.example.paging.presentation.composable.components.RepoPageItem
import com.example.paging.presentation.composable.components.SearchBarComposable
import com.example.paging.presentation.composable.components.util.isAppendLoading
import com.example.paging.presentation.composable.components.util.isError
import com.example.paging.presentation.viewModel.PagingViewModel
import com.example.paging.presentation.viewModel.UiModel
import kotlinx.coroutines.launch


@Composable
fun GithubRepoPagingScreen(viewModel: PagingViewModel, onMoveToDetailPage: (repoId: Long) -> Unit) {
    val pagingDataFlow by viewModel.pagingDataFlow.collectAsStateWithLifecycle()
    val pagingItems = pagingDataFlow?.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()
    var queryText by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val pullToRefreshState = rememberPullToRefreshState()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Note pagingItems is defined as `val lazyPagingItems = remember(this) { LazyPagingItems(this) }` in `collectAsStateWithLifecycle()`
    // so it is remembered and never changes unless the pagingDataFlow is changed. (`remember(this)`)
    // Use `val pagingItemCountUpdated by rememberUpdatedState(pagingItems)`
    // and access `pagingItemCountUpdated?.itemCount` always get outdated values due to lambda closure captures the initial value of `pagingItems?.itemCount`
    // instead you should use `rememberUpdatedState(pagingItems?.itemCount)` to make rememberUpdatedState changes when `pagingItems?.itemCount` is changed
    val pagingItemCountUpdated by rememberUpdatedState(pagingItems?.itemCount)
    SideEffect {
        Log.i("GithubRepoPagingScreen", "pagingItems size: $pagingItemCountUpdated")
    }

    Scaffold(
        floatingActionButton = {
            // Scroll to top button
            SmallFloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(painterResource(R.drawable.arrow_upward), "Small floating action button.")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        Column(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    })
                }
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SearchBarComposable(
                onSearch = { queryInputText ->
                    Log.i("SearchBarComposable", "onSearch is called!")
                    // To start a new query, change the StateFlow of pagingDataFlow by viewModel
                    // then pagingDataFlowState changes, which triggers a recomposition.
                    // Check the source code of collectAsLazyPagingItems(), when pagingDataFlow changes,
                    // a new lazyPagingItems is created and two LaunchedEffect collecting page data from the new source of the query
                    // then the UI shows new content for the new lazyPagingItems
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    viewModel.setPagingDataFlowByQuery(queryInputText.trim())
                    queryText = queryInputText
                },
                onRefresh = {
                    coroutineScope.launch {
                        viewModel.invalidateQueryCache(queryText)
                    }
                }
            )

            PullToRefreshBox(
                isRefreshing = pagingItems?.loadState?.refresh is LoadState.Loading,
                // use pagingItems?.refresh() with `PullToRefreshBox` so than when `PullToRefresh` is triggered
                // A refresh to the first PagingData is triggered in RemoteMediator
                // Then you should remote all the cache first, then refetch the data!
                onRefresh = { pagingItems?.refresh() },
                state = pullToRefreshState,
                indicator = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Indicator(
                            isRefreshing = pagingItems?.loadState?.refresh is LoadState.Loading,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            state = pullToRefreshState
                        )
                    }
                },
            ) {
                LazyColumn(
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(count = pagingItems?.itemCount ?: 0) { index ->
                        pagingItems?.get(index)?.let {
                            when (it) {
                                is UiModel.RepoItem -> {
                                    RepoPageItem(
                                        it.repo,
                                        queryText,
                                        onMoveToDetailPage
                                    )
                                    HorizontalDivider()
                                }

                                is UiModel.SeparatorItem -> PageTitle(it.description)
                            }
                        }
                    }
                    // handle pagingItems's loadState
                    if (pagingItems?.loadState?.isAppendLoading() == true) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(40.dp),
                                    color = MaterialTheme.colorScheme.secondary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                )
                            }
                        }
                    }
                    if (pagingItems?.loadState?.isError() == true) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    onClick = { pagingItems.retry() },
                                ) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}