package com.example.gallery.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gallery.ui.home.HomeScreen
import com.example.gallery.ui.search.SearchScreen
import com.example.gallery.ui.theme.ExpressiveGalleryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpressiveGalleryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    AppNavHost()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val startDestination = "home"

    NavHost(navController, startDestination) {
        composable("home") {
            HomeScreen(
                onItemClick = { },
                onItemLongClick = { },
                onSearchClick = { navController.navigate("search") },
                onPeopleClick = { },
                onAlbumsClick = { },
                onSettingsClick = { }
            )
        }
        composable("search") {
            SearchScreen(
                onResultClick = { },
                onBackPress = { navController.popBackStack() },
                onSearchResultClick = { },
                onSuggestionClick = { },
                onFilterClick = { },
                onVoiceSearchClick = { }
            )
        }
        composable("people") { }
        composable("albums") { }
        composable("settings") { }
    }
}
