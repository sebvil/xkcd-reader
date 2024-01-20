package com.colibrez.xkcdreader.android.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.max
import androidx.navigation.compose.rememberNavController
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.colibrez.xkcdreader.android.ui.features.NavGraphs
import com.colibrez.xkcdreader.android.ui.features.navigation.NavigationBar
import com.ramcosta.composedestinations.DestinationsNavHost

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalLayoutApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Coil.setImageLoader {
            ImageLoader.Builder(this)
                .memoryCache {
                    MemoryCache.Builder(this).build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(this.cacheDir.resolve("image_cache"))
                        .build()
                }
                .build()
        }
        setContent {
            val navController = rememberNavController()
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(navController = navController)
                    },
                ) {
                    DestinationsNavHost(
                        navGraph = NavGraphs.root,
                        modifier = Modifier
                            .padding(
                                bottom = max(
                                    it.calculateBottomPadding(),
                                    WindowInsets.ime
                                        .asPaddingValues()
                                        .calculateBottomPadding(),
                                ),
                            )
                            .consumeWindowInsets(
                                WindowInsets.navigationBars,
                            ),
                        navController = navController,
                    )
                }
            }
        }
    }
}
