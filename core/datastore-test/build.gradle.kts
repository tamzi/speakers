/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
plugins {
    id("speakers.android.library")
    id("speakers.android.hilt")
}

android {
    namespace = "com.tamzi.speakers.core.datastore.test"
}

dependencies {
    api(project(":core:datastore"))
    api(libs.androidx.dataStore.core)

    implementation(project(":core:common"))
    implementation(project(":core:testing"))
}
