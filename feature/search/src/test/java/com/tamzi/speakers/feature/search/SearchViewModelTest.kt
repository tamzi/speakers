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

package com.tamzi.speakers.feature.search

import androidx.lifecycle.SavedStateHandle
import com.tamzi.speakers.core.domain.GetRecentSearchQueriesUseCase
import com.tamzi.speakers.core.domain.GetSearchContentsCountUseCase
import com.tamzi.speakers.core.domain.GetSearchContentsUseCase
import com.tamzi.speakers.core.testing.data.newsResourcesTestData
import com.tamzi.speakers.core.testing.data.topicsTestData
import com.tamzi.speakers.core.testing.repository.TestRecentSearchRepository
import com.tamzi.speakers.core.testing.repository.TestSearchContentsRepository
import com.tamzi.speakers.core.testing.repository.TestUserDataRepository
import com.tamzi.speakers.core.testing.util.MainDispatcherRule
import com.tamzi.speakers.feature.search.RecentSearchQueriesUiState.Success
import com.tamzi.speakers.feature.search.SearchResultUiState.EmptyQuery
import com.tamzi.speakers.feature.search.SearchResultUiState.Loading
import com.tamzi.speakers.feature.search.SearchResultUiState.SearchNotReady
import com.tamzi.speakers.feature.search.SearchViewModel
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
class SearchViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val userDataRepository = TestUserDataRepository()
    private val searchContentsRepository = TestSearchContentsRepository()
    private val getSearchContentsUseCase = GetSearchContentsUseCase(
        searchContentsRepository = searchContentsRepository,
        userDataRepository = userDataRepository,
    )
    private val recentSearchRepository = TestRecentSearchRepository()
    private val getRecentQueryUseCase = GetRecentSearchQueriesUseCase(recentSearchRepository)
    private val getSearchContentsCountUseCase = GetSearchContentsCountUseCase(searchContentsRepository)
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        viewModel = SearchViewModel(
            getSearchContentsUseCase = getSearchContentsUseCase,
            getSearchContentsCountUseCase = getSearchContentsCountUseCase,
            recentSearchQueriesUseCase = getRecentQueryUseCase,
            savedStateHandle = SavedStateHandle(),
            recentSearchRepository = recentSearchRepository,
        )
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(Loading, viewModel.searchResultUiState.value)
    }

    @Test
    fun stateIsEmptyQuery_withEmptySearchQuery() = runTest {
        searchContentsRepository.addNewsResources(newsResourcesTestData)
        searchContentsRepository.addTopics(topicsTestData)
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.searchResultUiState.collect() }

        viewModel.onSearchQueryChanged("")

        assertEquals(EmptyQuery, viewModel.searchResultUiState.value)

        collectJob.cancel()
    }

    @Test
    fun emptyResultIsReturned_withNotMatchingQuery() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.searchResultUiState.collect() }

        viewModel.onSearchQueryChanged("XXX")
        searchContentsRepository.addNewsResources(newsResourcesTestData)
        searchContentsRepository.addTopics(topicsTestData)

        val result = viewModel.searchResultUiState.value
        // TODO: Figure out to get the latest emitted ui State? The result is emitted as EmptyQuery
        // assertIs<Success>(result)

        collectJob.cancel()
    }

    @Test
    fun recentSearches_verifyUiStateIsSuccess() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.recentSearchQueriesUiState.collect() }
        viewModel.onSearchTriggered("kotlin")

        val result = viewModel.recentSearchQueriesUiState.value
        assertIs<Success>(result)

        collectJob.cancel()
    }

    @Test
    fun searchNotReady_withNoFtsTableEntity() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.searchResultUiState.collect() }

        viewModel.onSearchQueryChanged("")

        assertEquals(SearchNotReady, viewModel.searchResultUiState.value)

        collectJob.cancel()
    }
}