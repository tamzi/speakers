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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tamzi.speakers.core.data.repository.RecentSearchRepository
import com.tamzi.speakers.core.domain.GetRecentSearchQueriesUseCase
import com.tamzi.speakers.core.domain.GetSearchContentsCountUseCase
import com.tamzi.speakers.core.domain.GetSearchContentsUseCase
import com.tamzi.speakers.core.result.Result
import com.tamzi.speakers.core.result.asResult
import com.tamzi.speakers.feature.search.SearchResultUiState.EmptyQuery
import com.tamzi.speakers.feature.search.SearchResultUiState.LoadFailed
import com.tamzi.speakers.feature.search.SearchResultUiState.Loading
import com.tamzi.speakers.feature.search.SearchResultUiState.SearchNotReady
import com.tamzi.speakers.feature.search.SearchResultUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    getSearchContentsUseCase: GetSearchContentsUseCase,
    getSearchContentsCountUseCase: GetSearchContentsCountUseCase,
    recentSearchQueriesUseCase: GetRecentSearchQueriesUseCase,
    private val recentSearchRepository: RecentSearchRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val searchQuery = savedStateHandle.getStateFlow(SEARCH_QUERY, "")

    val searchResultUiState: StateFlow<SearchResultUiState> =
        getSearchContentsCountUseCase().flatMapLatest { totalCount ->
            if (totalCount < SEARCH_MIN_FTS_ENTITY_COUNT) {
                flowOf(SearchNotReady)
            } else {
                searchQuery.flatMapLatest { query ->
                    if (query.length < SEARCH_QUERY_MIN_LENGTH) {
                        flowOf(EmptyQuery)
                    } else {
                        getSearchContentsUseCase(query).asResult().map {
                            when (it) {
                                is Result.Success -> {
                                    Success(
                                        topics = it.data.topics,
                                        newsResources = it.data.newsResources,
                                    )
                                }

                                is Result.Loading -> {
                                    Loading
                                }

                                is Result.Error -> {
                                    LoadFailed
                                }
                            }
                        }
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Loading,
        )

    val recentSearchQueriesUiState: StateFlow<RecentSearchQueriesUiState> =
        recentSearchQueriesUseCase().map(RecentSearchQueriesUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = RecentSearchQueriesUiState.Loading,
            )

    fun onSearchQueryChanged(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }

    /**
     * Called when the search action is explicitly triggered by the user. For example, when the
     * search icon is tapped in the IME or when the enter key is pressed in the search text field.
     *
     * The search results are displayed on the fly as the user types, but to explicitly save the
     * search query in the search text field, defining this method.
     */
    fun onSearchTriggered(query: String) {
        viewModelScope.launch {
            recentSearchRepository.insertOrReplaceRecentSearch(query)
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            recentSearchRepository.clearRecentSearches()
        }
    }
}

/** Minimum length where search query is considered as [SearchResultUiState.EmptyQuery] */
private const val SEARCH_QUERY_MIN_LENGTH = 2

/** Minimum number of the fts table's entity count where it's considered as search is not ready */
private const val SEARCH_MIN_FTS_ENTITY_COUNT = 1
private const val SEARCH_QUERY = "searchQuery"
