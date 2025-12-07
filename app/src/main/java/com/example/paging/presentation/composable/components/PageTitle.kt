package com.example.paging.presentation.composable.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.paging.R

@Composable
fun PageTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        // you must set the size of Box, like fillMaxSize() modifier, otherwise the size of Box is zero and show nothing
        modifier = modifier
            .background(color = colorResource(R.color.separatorBackground))
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = text,
            color = colorResource(R.color.separatorText),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
@Preview
private fun PageTitlePreview() = PageTitle("10000+ stars")