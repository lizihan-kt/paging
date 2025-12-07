package com.example.paging.presentation.composable.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.paging.R

@Composable
fun SearchBarComposable(
    onSearch: (String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    var queryInputText by rememberSaveable { mutableStateOf("") }

    TextField(
        modifier = modifier
            .heightIn(min = 56.dp)
            .fillMaxWidth(),
        value = queryInputText,
        onValueChange = { queryInputText = it },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.search),
                contentDescription = "search icon",
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(
                        onClick = { onSearch(queryInputText) }
                    )
                    .padding(4.dp)
            )
        },
        trailingIcon = {
            Icon(
                painter = painterResource(R.drawable.refresh),
                contentDescription = "refresh icon",
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(
                        onClick = { onRefresh() }
                    )
                    .padding(4.dp)
            )
        },
        // when you set "singleLine = true", the keyboard shows a "->" at the right corner which can trigger onDone() callback
        // if "singleLine" is not set, then a new line indicator is shown, and onDone cannot be triggered by keyboard?
        singleLine = true,
        keyboardActions = KeyboardActions(onDone = { onSearch(queryInputText) }),
        placeholder = {
            Text(stringResource(R.string.placeholder_search))
        },
        colors = TextFieldDefaults.colors(
            // set the background color of TextField be the same as the background color of the theme
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
@Preview
private fun SearchBarComposablePreview() = SearchBarComposable({}, {})

