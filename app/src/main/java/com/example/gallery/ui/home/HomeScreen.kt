package com.example.gallery.ui.home

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ContentScale
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.lazyItemsKey
import androidx.paging.compose.lazyPagingItemsKey
import com.example.gallery.domain.model.MediaItem
import com.example.gallery.ui.theme.ExpressiveGalleryTheme
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.transform.RoundedCornersTransformation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun HomeScreen(
    onItemClick: (MediaItem) -> Unit,
    onItemLongClick: (MediaItem) -> Unit,
    onSearchClick: () -> Unit,
    onPeopleClick: () -> Unit,
    onAlbumsClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<HomeViewModel>()

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val mediaItems = viewModel.mediaPagingData.collectAsLazyPagingItems()

    var isSelectionMode by remember { mutableStateOf(false) }
    val selectedItems = remember { mutableStateSetOf<Long>() }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        // Top App Bar with dynamic elevation
        TopAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            title = {
                Text(
                    text = currentTab.label,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            navigationIcon = {
                if (isSelectionMode) {
                    IconButton(onClick = { isSelectionMode = false; selectedItems.clear() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Sair da seleção", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
            },
            actions = {
                if (isSelectionMode) {
                    SelectionModeActions(
                        selectedCount = selectedItems.size,
                        onSelectAll = { /* TODO */ },
                        onDelete = { /* TODO */ },
                        onFavorite = { /* TODO */ },
                        onAddToAlbum = { /* TODO */ },
                        onCancel = { isSelectionMode = false; selectedItems.clear() },
                    )
                } else {
                    Row {
                        IconButton(onClick = onSearchClick) {
                            Icon(Icons.Default.Search, contentDescription = "Buscar", tint = MaterialTheme.colorScheme.onSurface)
                        }
                        IconButton(onClick = onAlbumsClick) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = "Álbuns", tint = MaterialTheme.colorScheme.onSurface)
                        }
                        IconButton(onClick = onPeopleClick) {
                            Icon(Icons.Default.Favorite, contentDescription = "Pessoas", tint = MaterialTheme.colorScheme.onSurface)
                        }
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Mais", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            )
        )

        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = currentTab.order,
            onTabSelected = { tab ->
                viewModel.setTab(HomeTab.all[tab])
                isSelectionMode = false
                selectedItems.clear()
            },
            tabs = { HomeTab.all.forEach { tab ->
                Tab(
                    selected = tab == currentTab,
                    onClick = { viewModel.setTab(tab) },
                    text = { Text(text = tab.label) },
                    icon = { Icon(tab.icon, contentDescription = null) },
                )
            }},
            indicator = { tabPositions ->
                // Custom indicator handled by Material3
            },
            divider = { androidx.compose.material3.Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp) },
        )

        // Grid Content
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                mediaItems.loadState.refresh is androidx.paging.LoadState.Loading && mediaItems.itemCount == 0 -> {
                    LoadingState()
                }
                mediaItems.loadState.refresh is androidx.paging.LoadState.Error -> {
                    ErrorState(mediaItems.loadState.refresh as androidx.paging.LoadState.Error)
                }
                mediaItems.itemCount == 0 -> {
                    EmptyState(currentTab)
                }
                else -> {
                    MediaGrid(
                        mediaItems = mediaItems,
                        currentTab = currentTab,
                        isSelectionMode = isSelectionMode,
                        selectedItems = selectedItems,
                        onItemClick = { item ->
                            if (isSelectionMode) {
                                toggleSelection(item.id, selectedItems)
                            } else {
                                onItemClick(item)
                            }
                        },
                        onItemLongClick = { item ->
                            if (!isSelectionMode) {
                                isSelectionMode = true
                                toggleSelection(item.id, selectedItems)
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectionModeActions(
    selectedCount: Int,
    onSelectAll: () -> Unit,
    onDelete: () -> Unit,
    onFavorite: () -> Unit,
    onAddToAlbum: () -> Unit,
    onCancel: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "$selectedCount selecionados", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
        IconButton(onClick = onSelectAll) { Icon(Icons.Default.SelectAll, contentDescription = "Selecionar todos") }
        IconButton(onClick = onFavorite) { Icon(Icons.Default.Favorite, contentDescription = "Favoritar", tint = MaterialTheme.colorScheme.onSurface) }
        IconButton(onClick = onAddToAlbum) { Icon(Icons.Default.Folder, contentDescription = "Adicionar ao álbum", tint = MaterialTheme.colorScheme.onSurface) }
        IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error) }
        IconButton(onClick = onCancel) { Icon(Icons.Default.ArrowBack, contentDescription = "Cancelar") }
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        androidx.compose.material3.CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
private fun ErrorState(error: androidx.paging.LoadState.Error) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Erro ao carregar", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
            Text(text = error.error.message ?: "Erro desconhecido", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))
        }
    }
}

@Composable
private fun EmptyState(tab: HomeTab) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(tab.icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Nenhum ${tab.label.toLowerCase()}", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Suas ${tab.label.toLowerCase()} aparecerão aqui", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MediaGrid(
    mediaItems: androidx.paging.compose.LazyPagingItems<MediaItem>,
    currentTab: HomeTab,
    isSelectionMode: Boolean,
    selectedItems: kotlinx.coroutines.sync.MutableStateSet<Long>,
    onItemClick: (MediaItem) -> Unit,
    onItemLongClick: (MediaItem) -> Unit,
) {
    val context = LocalContext.current

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        cells = GridCells.Fixed(if (currentTab == HomeTab.FOLDERS) 2 else 3),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(mediaItems, key = { it.id }) { item ->
            if (item != null) {
                MediaGridItem(
                    item = item,
                    isSelected = isSelectionMode && selectedItems.contains(item.id),
                    isSelectionMode = isSelectionMode,
                    onClick = { onItemClick(item) },
                    onLongClick = { onItemLongClick(item) },
                    onSelectionToggle = { toggleSelection(item.id, selectedItems) },
                    context = context,
                )
            } else {
                // Placeholder for null items (if placeholders enabled)
                PlaceholderItem()
            }
        }
    }
}

@Composable
private fun PlaceholderItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(12.dp))
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MediaGridItem(
    item: MediaItem,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onSelectionToggle: () -> Unit,
    context: Context,
) {
    val isVideo = item.mimeType.startsWith("video/")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.surfaceContainerHighest
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onLongClick() },
                )
            },
        contentAlignment = Alignment.BottomEnd
    ) {
        // Media thumbnail
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(item.uri.toString())
                .crossfade(true)
                .transformations(RoundedCornersTransformation(12.dp))
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .build(),
            contentDescription = item.displayName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        // Selection overlay
        if (isSelectionMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                    )
                    .clip(RoundedCornerShape(12.dp))
            ) {
                if (isSelected) {
                    androidx.compose.material3.Checkbox(
                        checked = true,
                        onCheckedChange = { onSelectionToggle() },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    )
                } else {
                    androidx.compose.material3.Checkbox(
                        checked = false,
                        onCheckedChange = { onSelectionToggle() },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    )
                }
            }
        }

        // Video badge
        if (isVideo) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.IconButton(
                    onClick = { /* handled by parent */ },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                ) {
                    androidx.compose.material.icons.Icons.Default.PlayArrow.toImageVector()
                        ?.let { Icon(it, contentDescription = "Vídeo", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(8.dp)) }
                }
            }
        }

        // Favorite badge
        if (item.isFavorite) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Favorito",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                        .padding(6.dp)
                )
            }
        }
    }
}

private fun toggleSelection(id: Long, selectedItems: kotlinx.coroutines.sync.MutableStateSet<Long>) {
    if (selectedItems.contains(id)) {
        selectedItems.remove(id)
    } else {
        selectedItems.add(id)
    }
}

// Missing import for detectTapGestures
import androidx.compose.foundation.gestures.detectTapGestures