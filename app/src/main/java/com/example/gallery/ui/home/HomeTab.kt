package com.example.gallery.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Videocam

enum class HomeTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val order: Int) {
    PHOTOS("Fotos", Icons.Default.PhotoLibrary, 0),
    FAVORITES("Favoritos", Icons.Default.Favorite, 1),
    VIDEOS("Vídeos", Icons.Default.Videocam, 2),
    SCREENSHOTS("Capturas", Icons.Default.Image, 3),
    DOCUMENTS("Documentos", Icons.Default.InsertDriveFile, 4),
    FOLDERS("Pastas", Icons.Default.Folder, 5);

    companion object {
        val all = values().sortedBy { it.order }
    }
}