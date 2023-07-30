/*
 * Copyright 2023 Jannik Möser
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.jnnkmsr.event.compose

import com.github.jnnkmsr.event.core.Event

/** Maps `this` [Event] to a matching [UiEvent]. */
public fun <T> Event<T>.toUiEvent(): UiEvent<T> =
    when (this) {
        is Event.Triggered -> UiEvent.Triggered(data, emitter, receiver)
        is Event.Consumed -> UiEvent.Consumed(emitter, receiver)
    }

/** Maps `this` [UiEvent] to a matching [Event]. */
public fun <T> UiEvent<T>.toEvent(): Event<T> =
    when (this) {
        is UiEvent.Triggered -> Event.Triggered(data, emitter, receiver)
        is UiEvent.Consumed -> Event.Consumed(emitter, receiver)
    }
