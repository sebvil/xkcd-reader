package com.colibrez.xkcdreader.android.ui.features.comiclist.all.filters

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlin.enums.enumEntries

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBar(
    stateHolder: FilterStateHolder,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    val state by stateHolder.state.collectAsState()
    // Using LazyRow to reduce refactoring if more filters are added
    LazyRow(modifier = modifier, contentPadding = contentPadding) {
        item {
            var expanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .wrapContentSize(Alignment.TopStart),
            ) {
                ElevatedFilterChip(
                    selected = state.isReadFilter.selection != ReadFilter.All,
                    onClick = { expanded = true },
                    leadingIcon = if (state.isReadFilter.selection != ReadFilter.All) {
                        {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        }
                    } else {
                        null
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                        )
                    },
                    label = {
                        Text(text = state.isReadFilter.selection.displayName)
                    },
                )
                EnumFilterDropdown(
                    selection = state.isReadFilter.selection,
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    onItemSelected = { stateHolder.handle(FilterUserAction.IsReadFilterSelected(it)) },
                )
            }
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
@Composable
private inline fun <reified T> EnumFilterDropdown(
    selection: T,
    expanded: Boolean,
    noinline onDismissRequest: () -> Unit,
    crossinline onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
) where T : EnumFilter<T>, T : Enum<T> {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        enumEntries<T>().forEach {
            DropdownMenuItem(
                text = { Text(it.displayName) },
                onClick = {
                    onItemSelected(it)
                    onDismissRequest()
                },
                trailingIcon = if (selection == it) {
                    {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    }
                } else {
                    null
                },
            )
        }
    }
}
