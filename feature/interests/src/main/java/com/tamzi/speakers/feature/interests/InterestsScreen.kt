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

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamzi.speakers.core.stage.component.SpeakerBackground
import com.tamzi.speakers.core.stage.component.NiaLoadingWheel
import com.tamzi.speakers.core.stage.theme.NiaTheme
import com.tamzi.speakers.core.model.data.FollowableTopic
import com.tamzi.speakers.core.ui.DevicePreviews
import com.tamzi.speakers.core.ui.FollowableTopicPreviewParameterProvider
import com.tamzi.speakers.core.ui.TrackScreenViewEvent
import com.tamzi.speakers.feature.interests.InterestsUiState.Empty
import com.tamzi.speakers.feature.interests.InterestsUiState.Interests
import com.tamzi.speakers.feature.interests.InterestsUiState.Loading

@Composable
internal fun InterestsRoute(
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InterestsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InterestsScreen(
        uiState = uiState,
        followTopic = viewModel::followTopic,
        onTopicClick = onTopicClick,
        modifier = modifier,
    )
}

@Composable
internal fun InterestsScreen(
    uiState: InterestsUiState,
    followTopic: (String, Boolean) -> Unit,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState) {
            Loading ->
                NiaLoadingWheel(
                    modifier = modifier,
                    contentDesc = stringResource(id = R.string.loading),
                )
            is Interests ->
                TopicsTabContent(
                    topics = uiState.topics,
                    onTopicClick = onTopicClick,
                    onFollowButtonClick = followTopic,
                    modifier = modifier,
                )
            is Empty -> InterestsEmptyScreen()
        }
    }
    TrackScreenViewEvent(screenName = "Interests")
}

@Composable
private fun InterestsEmptyScreen() {
    Text(text = stringResource(id = R.string.empty_header))
}

@DevicePreviews
@Composable
fun InterestsScreenPopulated(
    @PreviewParameter(FollowableTopicPreviewParameterProvider::class)
    followableTopics: List<FollowableTopic>,
) {
    NiaTheme {
        SpeakerBackground {
            InterestsScreen(
                uiState = Interests(
                    topics = followableTopics,
                ),
                followTopic = { _, _ -> },
                onTopicClick = {},
            )
        }
    }
}

@DevicePreviews
@Composable
fun InterestsScreenLoading() {
    NiaTheme {
        SpeakerBackground {
            InterestsScreen(
                uiState = Loading,
                followTopic = { _, _ -> },
                onTopicClick = {},
            )
        }
    }
}

@DevicePreviews
@Composable
fun InterestsScreenEmpty() {
    NiaTheme {
        SpeakerBackground {
            InterestsScreen(
                uiState = Empty,
                followTopic = { _, _ -> },
                onTopicClick = {},
            )
        }
    }
}
