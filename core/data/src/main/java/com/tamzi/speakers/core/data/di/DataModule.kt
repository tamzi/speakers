/*
 * Copyright 2023 The Android Open Source Project
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.tamzi.speakers.core.data.di

import com.tamzi.speakers.core.data.repository.DefaultRecentSearchRepository
import com.tamzi.speakers.core.data.repository.DefaultSearchContentsRepository
import com.tamzi.speakers.core.data.repository.NewsRepository
import com.tamzi.speakers.core.data.repository.OfflineFirstNewsRepository
import com.tamzi.speakers.core.data.repository.OfflineFirstTopicsRepository
import com.tamzi.speakers.core.data.repository.OfflineFirstUserDataRepository
import com.tamzi.speakers.core.data.repository.RecentSearchRepository
import com.tamzi.speakers.core.data.repository.SearchContentsRepository
import com.tamzi.speakers.core.data.repository.TopicsRepository
import com.tamzi.speakers.core.data.repository.UserDataRepository
import com.tamzi.speakers.core.data.util.ConnectivityManagerNetworkMonitor
import com.tamzi.speakers.core.data.util.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindsTopicRepository(
        topicsRepository: OfflineFirstTopicsRepository,
    ): TopicsRepository

    @Binds
    fun bindsNewsResourceRepository(
        newsRepository: OfflineFirstNewsRepository,
    ): NewsRepository

    @Binds
    fun bindsUserDataRepository(
        userDataRepository: OfflineFirstUserDataRepository,
    ): UserDataRepository

    @Binds
    fun bindsRecentSearchRepository(
        recentSearchRepository: DefaultRecentSearchRepository,
    ): RecentSearchRepository

    @Binds
    fun bindsSearchContentsRepository(
        searchContentsRepository: DefaultSearchContentsRepository,
    ): SearchContentsRepository

    @Binds
    fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor
}
