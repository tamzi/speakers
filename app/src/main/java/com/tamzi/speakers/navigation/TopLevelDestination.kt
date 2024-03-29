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

package com.tamzi.speakers.navigation


import com.tamzi.speakers.core.stage.icon.Icon
import com.tamzi.speakers.core.stage.icon.Icon.DrawableResourceIcon
import com.tamzi.speakers.core.stage.icon.Icon.ImageVectorIcon
import com.tamzi.speakers.core.stage.icon.NiaIcons
import com.tamzi.speakers.R
import com.tamzi.speakers.feature.bookmarks.R as bookmarksR
import com.tamzi.speakers.feature.foryou.R as forYouR
import com.tamzi.speakers.feature.interests.R as interestsR

/**
 * Type for the top level destinations in the application. Each of these destinations
 * can contain one or more screens (based on the window size). Navigation from one screen to the
 * next within a single destination will be handled directly in composables.
 */
enum class TopLevelDestination(
    val selectedIcon: Icon,
    val unselectedIcon: Icon,
    val iconTextId: Int,
    val titleTextId: Int,
) {
    FOR_YOU(
        selectedIcon = DrawableResourceIcon(NiaIcons.Upcoming),
        unselectedIcon = DrawableResourceIcon(NiaIcons.UpcomingBorder),
        iconTextId = forYouR.string.for_you,
        titleTextId = R.string.app_name,
    ),
    BOOKMARKS(
        selectedIcon = DrawableResourceIcon(NiaIcons.Bookmarks),
        unselectedIcon = DrawableResourceIcon(NiaIcons.BookmarksBorder),
        iconTextId = bookmarksR.string.saved,
        titleTextId = bookmarksR.string.saved,
    ),
    INTERESTS(
        selectedIcon = ImageVectorIcon(NiaIcons.Grid3x3),
        unselectedIcon = ImageVectorIcon(NiaIcons.Grid3x3),
        iconTextId = interestsR.string.interests,
        titleTextId = interestsR.string.interests,
    ),
}
