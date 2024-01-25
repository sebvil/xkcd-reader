package com.colibrez.xkcdreader.android.ui.features.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.colibrez.xkcdreader.android.ui.MainState

@Composable
fun NavigationBar(
    state: MainState,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.NavigationBar(modifier = modifier) {
        state.tabs.forEachIndexed { index, destination ->
            NavigationBarItem(
                selected = index == state.currentTabIndex,
                onClick = {
                    onTabSelected(index)
                },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label,
                    )
                },
                label = { Text(text = destination.label) },
            )
        }
    }
}
