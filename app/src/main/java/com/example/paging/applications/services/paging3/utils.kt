package com.example.paging.applications.services.paging3

import androidx.paging.PagingState

fun <K : Any, V : Any> PagingState<K, V>.lastAccessedKey(
    prevKeyToCurrentKey: (K) -> K, // prevKey + 1
    nextKeyToCurrentKey: (K) -> K // nextKey - 1
): K? {
    val lastAccessedPage = anchorPosition?.let { closestPageToPosition(it) }
    return lastAccessedPage?.let { page ->
        page.prevKey?.let { prevKeyToCurrentKey(it) }
            ?: page.nextKey?.let { nextKeyToCurrentKey(it) }
    }
}