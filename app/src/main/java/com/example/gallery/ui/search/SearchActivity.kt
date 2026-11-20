package com.example.gallery.ui.search

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.gallery.ui.theme.ExpressiveGalleryTheme

class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpressiveGalleryTheme {
                // SearchScreen() - will be implemented later
            }
        }
    }
}
