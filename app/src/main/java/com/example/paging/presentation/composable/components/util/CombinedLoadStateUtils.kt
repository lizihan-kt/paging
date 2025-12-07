package com.example.paging.presentation.composable.components.util

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState

fun CombinedLoadStates.isAppendLoading(): Boolean {
    return append is LoadState.Loading
}

fun CombinedLoadStates.isError(): Boolean {
    return prepend is LoadState.Error || append is LoadState.Error || refresh is LoadState.Error
}