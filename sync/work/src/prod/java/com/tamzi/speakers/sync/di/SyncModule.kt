
package com.tamzi.speakers.sync.di

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.tamzi.speakers.core.data.util.SyncManager
import com.tamzi.speakers.sync.status.FirebaseSyncSubscriber
import com.tamzi.speakers.sync.status.SyncSubscriber
import com.tamzi.speakers.sync.status.WorkManagerSyncManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SyncModule {
    @Binds
    fun bindsSyncStatusMonitor(
        syncStatusMonitor: WorkManagerSyncManager,
    ): SyncManager

    @Binds
    fun bindsSyncSubscriber(
        syncSubscriber: FirebaseSyncSubscriber,
    ): SyncSubscriber

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseMessaging(): FirebaseMessaging = Firebase.messaging
    }
}
