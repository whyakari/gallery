package com.example.gallery.data.sync

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.room.RoomDatabase
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gallery.data.local.MediaDatabase
import com.example.gallery.data.local.MediaDao
import com.example.gallery.domain.model.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class MediaSyncWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val mediaItems = fetchMediaFromMediaStore(applicationContext.contentResolver)
            if (mediaItems.isNotEmpty()) {
                val db = MediaDatabase.getInstance(applicationContext)
                db.runInTransaction {
                    db.mediaDao().insertMediaItems(mediaItems)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun fetchMediaFromMediaStore(resolver: ContentResolver): List<MediaItem> {
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_TAKEN,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.RELATIVE_PATH,
            MediaStore.MediaColumns.IS_FAVORITE,
            MediaStore.MediaColumns.LATITUDE,
            MediaStore.MediaColumns.LONGITUDE,
            MediaStore.MediaColumns.DURATION,
        )

        val selection = "${MediaStore.MediaColumns.MIME_TYPE} LIKE 'image/%' OR ${MediaStore.MediaColumns.MIME_TYPE} LIKE 'video/%'"
        val sortOrder = "${MediaStore.MediaColumns.DATE_TAKEN} DESC"

        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Files.getContentUri("external")
        }

        val items = mutableListOf<MediaItem>()

        resolver.query(uri, projection, selection, null, sortOrder)?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val mimeIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
            val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
            val dateTakenIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_TAKEN)
            val dateAddedIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)
            val dateModifiedIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED)
            val widthIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH)
            val heightIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT)
            val bucketIdIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID)
            val bucketNameIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)
            val relativePathIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH)
            val isFavoriteIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.IS_FAVORITE)
            val latIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.LATITUDE)
            val lngIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.LONGITUDE)
            val durationIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val uri = ContentUris.withAppendedId(uri, id)

                items.add(MediaItem(
                    id = id,
                    uri = uri.toString(),
                    displayName = cursor.getString(nameIndex) ?: "",
                    mimeType = cursor.getString(mimeIndex) ?: "",
                    size = cursor.getLong(sizeIndex),
                    dateTaken = cursor.getLong(dateTakenIndex),
                    dateAdded = cursor.getLong(dateAddedIndex),
                    dateModified = cursor.getLong(dateModifiedIndex),
                    width = cursor.getInt(widthIndex),
                    height = cursor.getInt(heightIndex),
                    bucketId = cursor.getLong(bucketIdIndex),
                    bucketDisplayName = cursor.getString(bucketNameIndex) ?: "",
                    relativePath = cursor.getString(relativePathIndex) ?: "",
                    isFavorite = cursor.getInt(isFavoriteIndex) == 1,
                    locationLat = if (!cursor.isNull(latIndex)) cursor.getDouble(latIndex) else null,
                    locationLng = if (!cursor.isNull(lngIndex)) cursor.getDouble(lngIndex) else null,
                    duration = if (cursor.getInt(durationIndex) > 0) cursor.getLong(durationIndex) else null,
                    labels = emptyList(),
                    faceGroupIds = emptyList(),
                    dominantColors = emptyList(),
                    blurScore = 0f,
                    aestheticScore = 0f,
                    isTrashed = false,
                    trashedDate = null,
                    version = 1,
                ))
            }
        }

        return items
    }
}

// Periodic sync worker - runs daily
class PeriodicMediaSyncWorker(
    context: Context,
    params: WorkerParameters,
) : MediaSyncWorker(context, params)

// Initial sync worker - runs once on app install
class InitialMediaSyncWorker(
    context: Context,
    params: WorkerParameters,
) : MediaSyncWorker(context, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        // Check if already synced
        val prefs = applicationContext.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
        val hasSynced = prefs.getBoolean("initial_sync_done", false)

        if (hasSynced) {
            return Result.success()
        }

        val result = super.doWork()
        if (result == Result.success()) {
            prefs.edit().putBoolean("initial_sync_done", true).apply()
        }
        result
    }
}