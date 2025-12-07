package com.example.paging.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paging.applications.usecases.FetchRepoUseCase
import com.example.paging.presentation.composable.navigation3.DetainScreenKey
import com.example.paging.domain.entities.Repo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
class DetailScreenViewModel(
    @InjectedParam val detailScreenKey: DetainScreenKey,
    fetchRepoUseCase: FetchRepoUseCase
) : ViewModel() {
    private val _repoStateFlow: MutableStateFlow<Repo?> = MutableStateFlow(null)
    val repoStateFlow: StateFlow<Repo?>
        get() = _repoStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            _repoStateFlow.value = fetchRepoUseCase(detailScreenKey.id)
        }
    }

    override fun onCleared() {
        // the following is called when you click `back` button to move from ProductScreen back to HomeScreen
        Log.i("DetailScreenViewModel", "ViewModel is cleared")
    }
}