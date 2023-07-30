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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable

/**
 * [Launches][LaunchedEffect] a side effect that execute the given event
 * [handler] when the [event] becomes [Triggered][UiEvent.Triggered].
 *
 * @param event The [UiEvent] triggering the effect whenever it becomes
 *   non-`null` and [Triggered][UiEvent.Triggered].
 * @param onConsumed Callback to be used to pass back any
 *   [Consumed][UiEvent.Consumed] event from the effect. Depending on the value
 *   of [consumeFirst], the callback will be invoked before or after invoking
 *   the event [handler].
 * @param consumeFirst Flag indicating whether [onConsumed] should be called
 *   with the [Consumed][UiEvent.Consumed] event before (`true`) or after
 *   (`false`) invoking the suspending event [handler]. Set this to `true` to
 *   make sure the event will be consumed no matter if the [handler] ever
 *   returns.
 * @param handler The event handler receiving the [data][UiEvent.Triggered.data]
 *   of the [Triggered][UiEvent.Triggered] event.
 */
@Composable
@NonRestartableComposable
public fun <T> EventHandler(
    event: UiEvent<T>?,
    onConsumed: (consumed: UiEvent.Consumed) -> Unit,
    consumeFirst: Boolean = false,
    handler: suspend (content: T) -> Unit,
) {
    LaunchedEffect(event) {
        if (event as? UiEvent.Triggered != null) {
            if (consumeFirst) onConsumed(event.consumed())

            handler(event.data)

            if (!consumeFirst) onConsumed(event.consumed())
        }
    }
}
