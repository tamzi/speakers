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

package com.tamzi.speakers.feature.bookmarks

import com.tamzi.speakers.core.data.repository.CompositeUserNewsResourceRepository
import com.tamzi.speakers.core.testing.data.newsResourcesTestData
import com.tamzi.speakers.core.testing.repository.TestNewsRepository
import com.tamzi.speakers.core.testing.repository.TestUserDataRepository
import com.tamzi.speakers.core.testing.util.MainDispatcherRule
import com.tamzi.speakers.core.ui.NewsFeedUiState.Loading
import com.tamzi.speakers.core.ui.NewsFeedUiState.Success
import com.tamzi.speakers.feature.bookmarks.BookmarksViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 */
class BookmarksViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val userDataRepository = TestUserDataRepository()
    private val newsRepository = TestNewsRepository()
    private val userNewsResourceRepository = CompositeUserNewsResourceRepository(
        newsRepository = newsRepository,
        userDataRepository = userDataRepository,
    )
    private lateinit var viewModel: BookmarksViewModel

    @Before
    fun setup() {
        viewModel = BookmarksViewModel(
            userDataRepository = userDataRepository,
            userNewsResourceRepository = userNewsResourceRepository,
        )
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(Loading, viewModel.feedUiState.value)
    }

    @Test
    fun oneBookmark_showsInFeed() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.feedUiState.collect() }

        newsRepository.sendNewsResources(newsResourcesTestData)
        userDataRepository.updateNewsResourceBookmark(newsResourcesTestData[0].id, true)
        val item = viewModel.feedUiState.value
        assertIs<Success>(item)
        assertEquals(item.feed.size, 1)

        collectJob.cancel()
    }

    @Test
    fun oneBookmark_whenRemoving_removesFromFeed() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.feedUiState.collect() }
        // Set the news resources to be used by this test
        newsRepository.sendNewsResources(newsResourcesTestData)
        // Start with the resource saved
        userDataRepository.updateNewsResourceBookmark(newsResourcesTestData[0].id, true)
        // Use viewModel to remove saved resource
        viewModel.removeFromSavedResources(newsResourcesTestData[0].id)
        // Verify list of saved resources is now empty
        val item = viewModel.feedUiState.value
        assertIs<Success>(item)
        assertEquals(item.feed.size, 0)

        collectJob.cancel()
    }
}
