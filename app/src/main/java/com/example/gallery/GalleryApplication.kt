package com.example.gallery

import android.app.Application
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import com.example.gallery.data.sync.InitialMediaSyncWorker
import com.example.gallery.data.sync.PeriodicMediaSyncWorker
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class GalleryApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        scheduleMediaSync()
    }

    private fun scheduleMediaSync() {
        // Initial sync - runs once on app install
        val initialSync = androidx.work.OneTimeWorkRequestBuilder<InitialMediaSyncWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .addTag("initial_media_sync")
            .build()
        WorkManager.getInstance(this).enqueueUniqueWork(
            "initial_media_sync",
            androidx.work.ExistingWorkPolicy.KEEP,
            initialSync
        )

        // Periodic sync - runs daily
        val periodicSync = PeriodicWorkRequestBuilder<PeriodicMediaSyncWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(1, TimeUnit.HOURS)
            .addTag("periodic_media_sync")
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "periodic_media_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicSync
        )
    }

    companion object {
        fun triggerManualSync(context: Context) {
            val sync = androidx.work.OneTimeWorkRequestBuilder<InitialMediaSyncWorker>()
                .addTag("manual_media_sync")
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "manual_media_sync",
                androidx.work.ExistingWorkPolicy.REPLACE,
                sync
            )
        }
    }
}