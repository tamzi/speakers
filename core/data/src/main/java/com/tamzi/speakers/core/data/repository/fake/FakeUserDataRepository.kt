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

package com.tamzi.speakers.core.data.repository.fake

import com.tamzi.speakers.core.data.repository.UserDataRepository
import com.tamzi.speakers.core.datastore.SpeakerPreferencesDataSource
import com.tamzi.speakers.core.model.data.DarkThemeConfig
import com.tamzi.speakers.core.model.data.ThemeBrand
import com.tamzi.speakers.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Fake implementation of the [UserDataRepository] that returns hardcoded user data.
 *
 * This allows us to run the app with fake data, without needing an internet connection or working
 * backend.
 */
class FakeUserDataRepository @Inject constructor(
    private val speakerPreferencesDataSource: SpeakerPreferencesDataSource,
) : UserDataRepository {

    override val userData: Flow<UserData> =
        speakerPreferencesDataSource.userData

    override suspend fun setFollowedTopicIds(followedTopicIds: Set<String>) =
        speakerPreferencesDataSource.setFollowedTopicIds(followedTopicIds)

    override suspend fun toggleFollowedTopicId(followedTopicId: String, followed: Boolean) =
        speakerPreferencesDataSource.toggleFollowedTopicId(followedTopicId, followed)

    override suspend fun updateNewsResourceBookmark(newsResourceId: String, bookmarked: Boolean) {
        speakerPreferencesDataSource.toggleNewsResourceBookmark(newsResourceId, bookmarked)
    }

    override suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) =
        speakerPreferencesDataSource.setNewsResourceViewed(newsResourceId, viewed)

    override suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        speakerPreferencesDataSource.setThemeBrand(themeBrand)
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        speakerPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        speakerPreferencesDataSource.setDynamicColorPreference(useDynamicColor)
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        speakerPreferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding)
    }
}
