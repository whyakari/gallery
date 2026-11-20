package com.example.gallery.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.gallery.domain.model.Album
import com.example.gallery.domain.model.Face
import com.example.gallery.domain.model.FaceGroup
import com.example.gallery.domain.model.MediaItem
import com.example.gallery.domain.model.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {

    // Media Items
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItems(items: List<MediaItem>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMediaItems(items: List<MediaItem>)

    @Query("DELETE FROM media_items WHERE id IN (:ids)")
    suspend fun deleteMediaItems(ids: List<Long>)

    @Query("SELECT * FROM media_items WHERE id = :id")
    suspend fun getMediaItem(id: Long): MediaItem?

    @Query("SELECT * FROM media_items WHERE isTrashed = 0 ORDER BY dateTaken DESC LIMIT :limit OFFSET :offset")
    fun getMediaItemsPaged(limit: Int, offset: Int): Flow<List<MediaItem>>

    @Query("SELECT * FROM media_items WHERE isTrashed = 0 AND bucketId = :bucketId ORDER BY dateTaken DESC")
    fun getMediaItemsByBucket(bucketId: Long): Flow<List<MediaItem>>

    @Query("SELECT * FROM media_items WHERE isTrashed = 0 AND isFavorite = 1 ORDER BY dateTaken DESC")
    fun getFavoriteMediaItems(): Flow<List<MediaItem>>

    @Query("SELECT * FROM media_items WHERE isTrashed = 1 ORDER BY trashedDate DESC")
    fun getTrashedMediaItems(): Flow<List<MediaItem>>

    @Query("SELECT * FROM media_items WHERE isTrashed = 0 AND dateTaken BETWEEN :startDate AND :endDate ORDER BY dateTaken DESC")
    fun getMediaItemsByDateRange(startDate: Long, endDate: Long): Flow<List<MediaItem>>

    @Query("SELECT * FROM media_items WHERE isTrashed = 0 AND (locationLat IS NOT NULL AND locationLng IS NOT NULL) ORDER BY dateTaken DESC")
    fun getMediaItemsWithLocation(): Flow<List<MediaItem>>

    @Query("SELECT * FROM media_items WHERE isTrashed = 0 AND mimeType LIKE 'video/%' ORDER BY dateTaken DESC")
    fun getVideoMediaItems(): Flow<List<MediaItem>>

    @Query("SELECT * FROM media_items WHERE isTrashed = 0 AND mimeType LIKE 'image/%' ORDER BY dateTaken DESC")
    fun getImageMediaItems(): Flow<List<MediaItem>>

    // Search
    @Query("""
        SELECT * FROM media_items
        WHERE isTrashed = 0
        AND (displayName LIKE :query OR locationName LIKE :query OR labels LIKE :query)
        ORDER BY dateTaken DESC
        LIMIT :limit
    """)
    fun searchMediaItems(query: String, limit: Int): Flow<List<MediaItem>>

    @Query("""
        SELECT * FROM media_items
        WHERE isTrashed = 0
        AND :faceGroupId IN (SELECT value FROM json_each(faceGroupIds))
        ORDER BY dateTaken DESC
    """)
    fun getMediaItemsByFaceGroup(faceGroupId: Long): Flow<List<MediaItem>>

    @Query("""
        SELECT * FROM media_items
        WHERE isTrashed = 0
        AND :label IN (SELECT value FROM json_each(labels))
        ORDER BY dateTaken DESC
    """)
    fun getMediaItemsByLabel(label: String): Flow<List<MediaItem>>

    @Query("SELECT COUNT(*) FROM media_items WHERE isTrashed = 0")
    suspend fun getTotalMediaCount(): Int

    // Buckets (Folders)
    @Query("SELECT DISTINCT bucketId, bucketDisplayName FROM media_items WHERE isTrashed = 0 ORDER BY bucketDisplayName")
    suspend fun getAllBuckets(): List<BucketInfo>

    @Query("SELECT COUNT(*) FROM media_items WHERE bucketId = :bucketId AND isTrashed = 0")
    suspend fun getBucketMediaCount(bucketId: Long): Int

    data class BucketInfo(val bucketId: Long, val bucketDisplayName: String)

    // Face Groups
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaceGroups(groups: List<FaceGroup>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateFaceGroups(groups: List<FaceGroup>)

    @Query("SELECT * FROM face_groups WHERE isHidden = 0 ORDER BY faceCount DESC")
    fun getVisibleFaceGroups(): Flow<List<FaceGroup>>

    @Query("SELECT * FROM face_groups WHERE id = :id")
    suspend fun getFaceGroup(id: Long): FaceGroup?

    // Faces
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaces(faces: List<Face>)

    @Query("SELECT * FROM faces WHERE mediaItemId = :mediaItemId")
    suspend fun getFacesForMediaItem(mediaItemId: Long): List<Face>

    @Query("SELECT * FROM faces WHERE faceGroupId = :faceGroupId")
    suspend fun getFacesForGroup(faceGroupId: Long): List<Face>

    // Albums
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albums: List<Album>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAlbums(albums: List<Album>)

    @Query("SELECT * FROM albums ORDER BY updatedAt DESC")
    fun getAllAlbums(): Flow<List<Album>>

    @Query("SELECT * FROM albums WHERE albumType = :type")
    suspend fun getAlbumsByType(type: com.example.gallery.domain.model.AlbumType): List<Album>

    @Query("SELECT * FROM albums WHERE id = :id")
    suspend fun getAlbum(id: Long): Album?

    @Transaction
    @Query("SELECT * FROM albums WHERE :mediaItemId IN (SELECT value FROM json_each(mediaItemIds))")
    suspend fun getAlbumsForMediaItem(mediaItemId: Long): List<Album>

    // Search History
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(history: SearchHistory)

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 20")
    fun getRecentSearchHistory(): Flow<List<SearchHistory>>

    @Query("DELETE FROM search_history WHERE timestamp < :cutoff")
    suspend fun clearOldSearchHistory(cutoff: Long)

    // Favorites toggle
    @Query("UPDATE media_items SET isFavorite = :isFavorite, version = version + 1 WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    // Trash operations
    @Query("UPDATE media_items SET isTrashed = 1, trashedDate = :date, version = version + 1 WHERE id = :id")
    suspend fun trashMediaItem(id: Long, date: Long)

    @Query("UPDATE media_items SET isTrashed = 0, trashedDate = NULL, version = version + 1 WHERE id = :id")
    suspend fun restoreMediaItem(id: Long)

    @Query("DELETE FROM media_items WHERE isTrashed = 1 AND trashedDate < :cutoff")
    suspend fun permanentlyDeleteTrashed(cutoff: Long)
}