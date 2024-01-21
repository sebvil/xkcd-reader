package com.colibrez.xkcdreader.android.ui.features.comiclist.filters

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> FilterChip(
    filter: EnumFilter<T>,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: (@Composable () -> Unit)? = null
) where T : Enum<T>, T : EnumFilter<T> {
    ElevatedFilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(text = filter.displayName)
        },
        modifier = modifier,
        leadingIcon = if (selected) {
            {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
            }
        } else {
            null
        },
        trailingIcon = trailingIcon,
    )
}
