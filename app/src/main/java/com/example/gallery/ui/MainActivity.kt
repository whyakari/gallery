package com.example.gallery.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomNavigation
import androidx.compose.material3.BottomNavigationItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gallery.ui.home.HomeScreen
import com.example.gallery.ui.home.HomeTab
import com.example.gallery.ui.search.SearchScreen
import com.example.gallery.ui.theme.ExpressiveGalleryTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
                    MainNavHost()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavHost() {
    val navController = rememberNavController()
    val startDestination = "home"

    NavHost(navController, startDestination) {
        composable("home") {
            HomeScreen(
                onItemClick = { item ->
                    navController.navigate("media_viewer/${item.id}")
                },
                onItemLongClick = { },
                onSearchClick = { navController.navigate("search") },
                onPeopleClick = { navController.navigate("people") },
                onAlbumsClick = { navController.navigate("albums") },
                onSettingsClick = { navController.navigate("settings") },
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
        composable("people") {
            PeopleScreen(onBackPress = { navController.popBackStack() })
        }
        composable("albums") {
            AlbumsScreen(onBackPress = { navController.popBackStack() })
        }
        composable("settings") {
            SettingsScreen(onBackPress = { navController.popBackStack() })
        }
        composable(
            route = "media_viewer/{mediaId}",
            arguments = listOf(navArgument("mediaId") { type = NavType.LongType })
        ) { backStackEntry ->
            val mediaId = backStackEntry.getLong("mediaId")
            MediaViewerScreen(mediaId = mediaId, onClose = { navController.popBackStack() })
        }
    }

    // Bottom Navigation Bar
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem("home", "Início", Icons.Default.PhotoLibrary),
        BottomNavItem("search", "Buscar", Icons.Default.Search),
        BottomNavItem("people", "Pessoas", Icons.Default.People),
        BottomNavItem("albums", "Álbuns", Icons.Default.Folder),
    )

    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute?.startsWith(item.route) == true
            BottomNavigationItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(item.icon, contentDescription = null)
                },
                label = {
                    Text(text = item.label, fontSize = 12.sp)
                },
                alwaysShowLabel = true,
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)

@Composable
fun PeopleScreen(onBackPress: () -> Unit) {
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text(text = "Pessoas e Animais") },
                navigationIcon = { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", onClick = onBackPress) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.People, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(64.dp))
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.foundation.layout.height(16.dp))
                Text(text = "Pessoas e Animais", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.foundation.layout.height(8.dp))
                Text(text = "Reconhecimento facial em desenvolvimento", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun AlbumsScreen(onBackPress: () -> Unit) {
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text(text = "Álbuns") },
                navigationIcon = { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", onClick = onBackPress) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(64.dp))
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.foundation.layout.height(16.dp))
                Text(text = "Álbuns", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.foundation.layout.height(8.dp))
                Text(text = "Seus álbuns aparecerão aqui", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun SettingsScreen(onBackPress: () -> Unit) {
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text(text = "Configurações") },
                navigationIcon = { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", onClick = onBackPress) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Configurações", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
            }
        }
    }
}

@Composable
fun MediaViewerScreen(mediaId: Long, onClose: () -> Unit) {
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text(text = "Visualizador") },
                navigationIcon = { Icon(Icons.Default.ArrowBack, contentDescription = "Fechar", onClick = onClose) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Text(text = "Visualizador de mídia: $mediaId", fontSize = 18.sp)
        }
    }
}