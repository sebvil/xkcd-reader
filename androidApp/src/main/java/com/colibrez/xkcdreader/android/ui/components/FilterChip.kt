package com.colibrez.xkcdreader.android.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FilterChip(
    displayName: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedFilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(text = displayName)
        },
        modifier = modifier,
        leadingIcon = {
            AnimatedVisibility(visible = selected) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
            }
        },
    )
}
