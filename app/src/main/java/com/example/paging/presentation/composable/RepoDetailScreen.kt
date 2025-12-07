package com.example.paging.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.paging.R
import com.example.paging.presentation.viewModel.DetailScreenViewModel

@Composable
fun RepoDetailScreen(viewModel: DetailScreenViewModel, onMoveBack: () -> Unit) {
    val repo by viewModel.repoStateFlow.collectAsStateWithLifecycle()

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.arrow_back),
            contentDescription = "back icon",
            modifier = Modifier
                .clip(CircleShape)
                .clickable(onClick = onMoveBack)
                .padding(4.dp)
        )

        if (repo == null) {
            Text("Loading...")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = repo?.name ?: "No Title",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = repo?.description ?: "No description"
                )
            }
        }
    }
}