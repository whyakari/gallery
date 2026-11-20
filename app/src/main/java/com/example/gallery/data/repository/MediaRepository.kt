package com.example.gallery.data.repository

import com.example.gallery.data.local.MediaDao
import com.example.gallery.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor(
    private val mediaDao: MediaDao,
) {

    // Media Items
    suspend fun insertMediaItems(items: List<MediaItem>) = mediaDao.insertMediaItems(items)
    suspend fun updateMediaItems(items: List<MediaItem>) = mediaDao.updateMediaItems(items)
    suspend fun deleteMediaItems(ids: List<Long>) = mediaDao.deleteMediaItems(ids)

    suspend fun getMediaItem(id: Long): MediaItem? = mediaDao.getMediaItem(id)

    fun getMediaItemsPaged(limit: Int, offset: Int): Flow<List<MediaItem>> =
        mediaDao.getMediaItemsPaged(limit, offset)

    fun getMediaItemsByBucket(bucketId: Long): Flow<List<MediaItem>> =
        mediaDao.getMediaItemsByBucket(bucketId)

    fun getFavoriteMediaItems(): Flow<List<MediaItem>> = mediaDao.getFavoriteMediaItems()
    fun getTrashedMediaItems(): Flow<List<MediaItem>> = mediaDao.getTrashedMediaItems()
    fun getMediaItemsByDateRange(startDate: Long, endDate: Long): Flow<List<MediaItem>> =
        mediaDao.getMediaItemsByDateRange(startDate, endDate)
    fun getMediaItemsWithLocation(): Flow<List<MediaItem>> = mediaDao.getMediaItemsWithLocation()
    fun getVideoMediaItems(): Flow<List<MediaItem>> = mediaDao.getVideoMediaItems()
    fun getImageMediaItems(): Flow<List<MediaItem>> = mediaDao.getImageMediaItems()

    // Search
    fun searchMediaItems(query: String, limit: Int = 100): Flow<List<MediaItem>> =
        mediaDao.searchMediaItems("%$query%", limit)

    fun getMediaItemsByFaceGroup(faceGroupId: Long): Flow<List<MediaItem>> =
        mediaDao.getMediaItemsByFaceGroup(faceGroupId)

    fun getMediaItemsByLabel(label: String): Flow<List<MediaItem>> =
        mediaDao.getMediaItemsByLabel(label)

    suspend fun getTotalMediaCount(): Int = mediaDao.getTotalMediaCount()

    // Buckets
    suspend fun getAllBuckets(): List<MediaDao.BucketInfo> = mediaDao.getAllBuckets()
    suspend fun getBucketMediaCount(bucketId: Long): Int = mediaDao.getBucketMediaCount(bucketId)

    // Face Groups
    suspend fun insertFaceGroups(groups: List<FaceGroup>) = mediaDao.insertFaceGroups(groups)
    suspend fun updateFaceGroups(groups: List<FaceGroup>) = mediaDao.updateFaceGroups(groups)
    fun getVisibleFaceGroups(): Flow<List<FaceGroup>> = mediaDao.getVisibleFaceGroups()
    suspend fun getFaceGroup(id: Long): FaceGroup? = mediaDao.getFaceGroup(id)

    // Faces
    suspend fun insertFaces(faces: List<Face>) = mediaDao.insertFaces(faces)
    suspend fun getFacesForMediaItem(mediaItemId: Long): List<Face> = mediaDao.getFacesForMediaItem(mediaItemId)
    suspend fun getFacesForGroup(faceGroupId: Long): List<Face> = mediaDao.getFacesForGroup(faceGroupId)

    // Albums
    suspend fun insertAlbums(albums: List<Album>) = mediaDao.insertAlbums(albums)
    suspend fun updateAlbums(albums: List<Album>) = mediaDao.updateAlbums(albums)
    fun getAllAlbums(): Flow<List<Album>> = mediaDao.getAllAlbums()
    suspend fun getAlbumsByType(type: AlbumType): List<Album> = mediaDao.getAlbumsByType(type)
    suspend fun getAlbum(id: Long): Album? = mediaDao.getAlbum(id)
    suspend fun getAlbumsForMediaItem(mediaItemId: Long): List<Album> = mediaDao.getAlbumsForMediaItem(mediaItemId)

    // Search History
    suspend fun insertSearchHistory(history: SearchHistory) = mediaDao.insertSearchHistory(history)
    fun getRecentSearchHistory(): Flow<List<SearchHistory>> = mediaDao.getRecentSearchHistory()
    suspend fun clearOldSearchHistory(cutoff: Long) = mediaDao.clearOldSearchHistory(cutoff)

    // Favorites
    suspend fun setFavorite(id: Long, isFavorite: Boolean) = mediaDao.setFavorite(id, isFavorite)

    // Trash
    suspend fun trashMediaItem(id: Long, date: Long) = mediaDao.trashMediaItem(id, date)
    suspend fun restoreMediaItem(id: Long) = mediaDao.restoreMediaItem(id)
    suspend fun permanentlyDeleteTrashed(cutoff: Long) = mediaDao.permanentlyDeleteTrashed(cutoff)

    // Combined search - returns SearchResult with all types
    fun searchAll(query: String): Flow<SearchResult> {
        val mediaItems = searchMediaItems(query, 50)
        val faceGroups = mediaDao.getVisibleFaceGroups().map { groups ->
            groups.filter { it.name?.lowercase()?.contains(query.lowercase()) == true }
        }
        val albums = getAllAlbums().map { albums ->
            albums.filter { it.title.lowercase().contains(query.lowercase()) }
        }
        val history = getRecentSearchHistory().map { history ->
            history.filter { it.query.lowercase().contains(query.lowercase()) }
                .map { SearchSuggestion(it.query, SuggestionType.RECENT_QUERY) }
        }

        return combine(mediaItems, faceGroups, albums, history) { items, faces, albums, history ->
            val suggestions = history + generateSuggestions(query, items, faces, albums)
            SearchResult(
                mediaItems = items,
                faceGroups = faces,
                albums = albums,
                suggestions = suggestions.distinctBy { it.text },
                totalCount = items.size + faces.size + albums.size,
                searchTimeMs = 0, // Would be measured in real implementation
            )
        }
    }

    private fun generateSuggestions(
        query: String,
        items: List<MediaItem>,
        faces: List<FaceGroup>,
        albums: List<Album>
    ): List<SearchSuggestion> {
        val suggestions = mutableListOf<SearchSuggestion>()

        // People suggestions
        faces.forEach { face ->
            face.name?.let { name ->
                if (name.lowercase().contains(query.lowercase())) {
                    suggestions.add(SearchSuggestion(name, SuggestionType.PEOPLE, "person", face.faceCount))
                }
            }
        }

        // Category suggestions based on labels
        val labelCounts = mutableMapOf<String, Int>()
        items.forEach { item ->
            item.labels.forEach { label ->
                if (label.lowercase().contains(query.lowercase())) {
                    labelCounts[label] = labelCounts.getOrDefault(label, 0) + 1
                }
            }
        }
        labelCounts.toList().sortedByDescending { it.second }.take(5).forEach { (label, count) ->
            suggestions.add(SearchSuggestion(label, SuggestionType.THING, getIconForLabel(label), count))
        }

        // Date range suggestions
        if (query.contains("202") || query.contains("jan") || query.contains("feb") ||
            query.contains("mar") || query.contains("apr") || query.contains("may") ||
            query.contains("jun") || query.contains("jul") || query.contains("aug") ||
            query.contains("sep") || query.contains("oct") || query.contains("nov") ||
            query.contains("dec")) {
            suggestions.add(SearchSuggestion("Photos from $query", SuggestionType.DATE_RANGE, "calendar_month"))
        }

        return suggestions
    }

    private fun getIconForLabel(label: String): String = when (label.lowercase()) {
        "dog", "cat", "pet" -> "pets"
        "beach", "ocean", "sea" -> "beach_access"
        "food", "meal", "restaurant" -> "restaurant"
        "car", "vehicle" -> "directions_car"
        "building", "house", "architecture" -> "home"
        "nature", "tree", "forest", "park" -> "nature"
        "sunset", "sunrise" -> "wb_sunny"
        "night", "dark" -> "nightlight"
        "people", "person", "group" -> "group"
        "selfie" -> "camera_front"
        "screenshot" -> "screenshot"
        "document", "paper", "text" -> "description"
        "video", "movie" -> "videocam"
        else -> "label"
    }
}