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

package com.tamzi.speakers.core.data.test

import com.tamzi.speakers.core.data.di.DataModule
import com.tamzi.speakers.core.data.repository.NewsRepository
import com.tamzi.speakers.core.data.repository.RecentSearchRepository
import com.tamzi.speakers.core.data.repository.SearchContentsRepository
import com.tamzi.speakers.core.data.repository.TopicsRepository
import com.tamzi.speakers.core.data.repository.UserDataRepository
import com.tamzi.speakers.core.data.repository.fake.FakeNewsRepository
import com.tamzi.speakers.core.data.repository.fake.FakeRecentSearchRepository
import com.tamzi.speakers.core.data.repository.fake.FakeSearchContentsRepository
import com.tamzi.speakers.core.data.repository.fake.FakeTopicsRepository
import com.tamzi.speakers.core.data.repository.fake.FakeUserDataRepository
import com.tamzi.speakers.core.data.util.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class],
)
interface TestDataModule {
    @Binds
    fun bindsTopicRepository(
        fakeTopicsRepository: FakeTopicsRepository,
    ): TopicsRepository

    @Binds
    fun bindsNewsResourceRepository(
        fakeNewsRepository: FakeNewsRepository,
    ): NewsRepository

    @Binds
    fun bindsUserDataRepository(
        userDataRepository: FakeUserDataRepository,
    ): UserDataRepository

    @Binds
    fun bindsRecentSearchRepository(
        recentSearchRepository: FakeRecentSearchRepository,
    ): RecentSearchRepository

    @Binds
    fun bindsSearchContentsRepository(
        searchContentsRepository: FakeSearchContentsRepository,
    ): SearchContentsRepository

    @Binds
    fun bindsNetworkMonitor(
        networkMonitor: AlwaysOnlineNetworkMonitor,
    ): NetworkMonitor
}
