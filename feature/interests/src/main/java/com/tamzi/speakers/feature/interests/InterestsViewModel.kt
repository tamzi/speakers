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

package com.tamzi.speakers.feature.interests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tamzi.speakers.core.data.repository.UserDataRepository
import com.tamzi.speakers.core.domain.GetFollowableTopicsUseCase
import com.tamzi.speakers.core.domain.TopicSortField
import com.tamzi.speakers.core.model.data.FollowableTopic
import com.tamzi.speakers.feature.interests.InterestsUiState.Loading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterestsViewModel @Inject constructor(
    val userDataRepository: UserDataRepository,
    getFollowableTopics: GetFollowableTopicsUseCase,
) : ViewModel() {

    val uiState: StateFlow<InterestsUiState> =
        getFollowableTopics(sortBy = TopicSortField.NAME).map(
            InterestsUiState::Interests,
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Loading,
        )

    fun followTopic(followedTopicId: String, followed: Boolean) {
        viewModelScope.launch {
            userDataRepository.toggleFollowedTopicId(followedTopicId, followed)
        }
    }
}

sealed interface InterestsUiState {
    object Loading : InterestsUiState

    data class Interests(
        val topics: List<FollowableTopic>,
    ) : InterestsUiState

    object Empty : InterestsUiState
}