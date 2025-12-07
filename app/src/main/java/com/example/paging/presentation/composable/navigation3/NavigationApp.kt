package com.example.paging.presentation.composable.navigation3

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.paging.presentation.composable.GithubRepoPagingScreen
import com.example.paging.presentation.composable.RepoDetailScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data object HomeScreenKey : NavKey

@Serializable
data class DetainScreenKey(val id: Long) : NavKey

@Composable
fun NavigationApp(modifier: Modifier) {
    val backStack = rememberNavBackStack(HomeScreenKey)

    NavDisplay(
        modifier = modifier,
        transitionSpec = {
            slideInHorizontally (
                // set the initialOffsetY as the full height of the content, so invisible at the beginning
                initialOffsetX = { it },
                animationSpec = tween(500)
            ) togetherWith ExitTransition.None
        },
        popTransitionSpec = {
            EnterTransition.None togetherWith slideOutHorizontally (
                targetOffsetX = { it },
                animationSpec = tween(500)
            )
        },
        predictivePopTransitionSpec = {
            EnterTransition.None togetherWith slideOutHorizontally (
                targetOffsetX = { it },
                animationSpec = tween(500)
            )
        },
        // bind the lifecycle of ViewModel (created by `viewModel()`) to the NavEntry
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backStack,
        // remove the last item in the list to move back
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<HomeScreenKey> {
                GithubRepoPagingScreen(
                    viewModel = koinViewModel()
                ) { repoId ->
                    backStack.add(DetainScreenKey(repoId))
                }
            }
            entry<DetainScreenKey> { key ->
                RepoDetailScreen(
                    // pass repoId to the ViewModel of `RepoDetailScreen`
                    // You can access it by injecting `val savedStateHandle: SavedStateHandle` in that `DetailPageViewModel`
                    viewModel = koinViewModel {
                        // https://stackoverflow.com/questions/79763944/how-to-pass-arguments-with-navigation3-using-savedstatehandle
                        // the approach using SavedStateHandle is no longer recommended by Google.
                        // Instead, consider using the idea of 'assisted injection' - e.g., just passing your key class to the constructor of your ViewModel
                        // then you can get that key in ViewModel by Koin DI
                        parametersOf(key)
                    },
                ) {
                    backStack.removeLastOrNull()
                }
            }
        }
    )
}