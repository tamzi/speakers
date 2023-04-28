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

package com.tamzi.samples.apps.speakers.core.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class NiaPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<com.tamzi.samples.apps.speakers.core.datastore.UserPreferences>,
) {
    val userData = userPreferences.data
        .map {
            UserData(
                bookmarkedNewsResources = it.bookmarkedNewsResourceIdsMap.keys,
                viewedNewsResources = it.viewedNewsResourceIdsMap.keys,
                followedTopics = it.followedTopicIdsMap.keys,
                themeBrand = when (it.themeBrand) {
                    null,
                    com.tamzi.samples.apps.speakers.core.datastore.ThemeBrandProto.THEME_BRAND_UNSPECIFIED,
                    com.tamzi.samples.apps.speakers.core.datastore.ThemeBrandProto.UNRECOGNIZED,
                    com.tamzi.samples.apps.speakers.core.datastore.ThemeBrandProto.THEME_BRAND_DEFAULT,
                    -> com.tamzi.samples.apps.speakers.core.datastore.ThemeBrand.DEFAULT
                    com.tamzi.samples.apps.speakers.core.datastore.ThemeBrandProto.THEME_BRAND_ANDROID -> com.tamzi.samples.apps.speakers.core.datastore.ThemeBrand.ANDROID
                },
                darkThemeConfig = when (it.darkThemeConfig) {
                    null,
                    com.tamzi.samples.apps.speakers.core.datastore.DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
                    com.tamzi.samples.apps.speakers.core.datastore.DarkThemeConfigProto.UNRECOGNIZED,
                    com.tamzi.samples.apps.speakers.core.datastore.DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM,
                    ->
                        com.tamzi.samples.apps.speakers.core.datastore.DarkThemeConfig.FOLLOW_SYSTEM
                    com.tamzi.samples.apps.speakers.core.datastore.DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT ->
                        com.tamzi.samples.apps.speakers.core.datastore.DarkThemeConfig.LIGHT
                    com.tamzi.samples.apps.speakers.core.datastore.DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> com.tamzi.samples.apps.speakers.core.datastore.DarkThemeConfig.DARK
                },
                useDynamicColor = it.useDynamicColor,
                shouldHideOnboarding = it.shouldHideOnboarding,
            )
        }

    suspend fun setFollowedTopicIds(topicIds: Set<String>) {
        try {
            userPreferences.updateData {
                it.copy {
                    followedTopicIds.clear()
                    followedTopicIds.putAll(topicIds.associateWith { true })
                    updateShouldHideOnboardingIfNecessary()
                }
            }
        } catch (ioException: IOException) {
            Log.e("NiaPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun toggleFollowedTopicId(topicId: String, followed: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (followed) {
                        followedTopicIds.put(topicId, true)
                    } else {
                        followedTopicIds.remove(topicId)
                    }
                    updateShouldHideOnboardingIfNecessary()
                }
            }
        } catch (ioException: IOException) {
            Log.e("NiaPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setThemeBrand(themeBrand: com.tamzi.samples.apps.speakers.core.datastore.ThemeBrand) {
        userPreferences.updateData {
            it.copy {
                this.themeBrand = when (themeBrand) {
                    ThemeBrand.DEFAULT -> com.google.samples.apps.speakers.core.datastore.ThemeBrandProto.THEME_BRAND_DEFAULT
                    ThemeBrand.ANDROID -> com.google.samples.apps.speakers.core.datastore.ThemeBrandProto.THEME_BRAND_ANDROID
                }
            }
        }
    }

    suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        userPreferences.updateData {
            it.copy {
                this.useDynamicColor = useDynamicColor
            }
        }
    }

    suspend fun setDarkThemeConfig(darkThemeConfig: com.tamzi.samples.apps.speakers.core.datastore.DarkThemeConfig) {
        userPreferences.updateData {
            it.copy {
                this.darkThemeConfig = when (darkThemeConfig) {
                    DarkThemeConfig.FOLLOW_SYSTEM ->
                        com.google.samples.apps.speakers.core.datastore.DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                    DarkThemeConfig.LIGHT -> com.google.samples.apps.speakers.core.datastore.DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    DarkThemeConfig.DARK -> com.google.samples.apps.speakers.core.datastore.DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                }
            }
        }
    }

    suspend fun toggleNewsResourceBookmark(newsResourceId: String, bookmarked: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (bookmarked) {
                        bookmarkedNewsResourceIds.put(newsResourceId, true)
                    } else {
                        bookmarkedNewsResourceIds.remove(newsResourceId)
                    }
                }
            }
        } catch (ioException: IOException) {
            Log.e("NiaPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        userPreferences.updateData {
            it.copy {
                if (viewed) {
                    viewedNewsResourceIds.put(newsResourceId, true)
                } else {
                    viewedNewsResourceIds.remove(newsResourceId)
                }
            }
        }
    }

    suspend fun getChangeListVersions() = userPreferences.data
        .map {
            ChangeListVersions(
                topicVersion = it.topicChangeListVersion,
                newsResourceVersion = it.newsResourceChangeListVersion,
            )
        }
        .firstOrNull() ?: ChangeListVersions()

    /**
     * Update the [ChangeListVersions] using [update].
     */
    suspend fun updateChangeListVersion(update: ChangeListVersions.() -> ChangeListVersions) {
        try {
            userPreferences.updateData { currentPreferences ->
                val updatedChangeListVersions = update(
                    ChangeListVersions(
                        topicVersion = currentPreferences.topicChangeListVersion,
                        newsResourceVersion = currentPreferences.newsResourceChangeListVersion,
                    ),
                )

                currentPreferences.copy {
                    topicChangeListVersion = updatedChangeListVersions.topicVersion
                    newsResourceChangeListVersion = updatedChangeListVersions.newsResourceVersion
                }
            }
        } catch (ioException: IOException) {
            Log.e("NiaPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferences.updateData {
            it.copy {
                this.shouldHideOnboarding = shouldHideOnboarding
            }
        }
    }
}

private fun UserPreferencesKt.Dsl.updateShouldHideOnboardingIfNecessary() {
    if (followedTopicIds.isEmpty() && followedAuthorIds.isEmpty()) {
        shouldHideOnboarding = false
    }
}
