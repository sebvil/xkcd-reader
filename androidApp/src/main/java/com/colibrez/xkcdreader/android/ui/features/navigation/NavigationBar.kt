package com.colibrez.xkcdreader.android.ui.features.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.colibrez.xkcdreader.android.ui.features.NavGraphs
import com.colibrez.xkcdreader.android.ui.features.appCurrentDestinationAsState
import com.colibrez.xkcdreader.android.ui.features.startAppDestination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DestinationSpec

@Composable
fun NavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val currentDestination: DestinationSpec<*> = navController.appCurrentDestinationAsState().value
        ?: NavGraphs.root.startAppDestination
    if (currentDestination in NavigationBarDestination.entries.map { it.direction }) {
        androidx.compose.material3.NavigationBar(modifier = modifier) {
            NavigationBarDestination.entries.forEach { destination ->
                NavigationBarItem(
                    selected = currentDestination == destination.direction,
                    onClick = {
                        navController.navigate(destination.direction) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                            anim {
                                exit = 0
                                enter = 0
                            }
                        }
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
}
