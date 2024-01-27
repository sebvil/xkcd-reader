package com.colibrez.xkcdreader.android.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        windowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .padding(bottom = 8.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
        }
        HorizontalDivider()
        Column(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .padding(WindowInsets.navigationBars.asPaddingValues()),
        ) {
            content()
        }
    }
}
