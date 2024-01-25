package com.colibrez.xkcdreader.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.max
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.colibrez.xkcdreader.android.ui.features.navigation.NavigationBar

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels<MainViewModel>()

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
            MyApplicationTheme {
                val state by mainViewModel.state.collectAsState()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(state = state, onTabSelected = mainViewModel::onTabSelected)
                    },
                ) {
                    state.tabs[state.currentTabIndex].component.Content(
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
                    )
                }
            }
        }
    }
}
