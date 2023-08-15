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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import com.github.jnnkmsr.event.core.Event
import com.github.jnnkmsr.event.core.consumed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

/**
 * [Launches][LaunchedEffect] a side effect that execute the given event
 * [handler] when the [event] becomes [Triggered][Event.Triggered].
 *
 * @param event The [Event] triggering the effect whenever it becomes non-`null`
 * and [Triggered][Event.Triggered].
 *
 * @param onConsumed Callback to pass back any [Consumed][Event.Consumed] event
 * from the effect. Depending on the value of [consumeImmediately], the callback
 * will be invoked before or after invoking the event [handler].
 *
 * @param consumeImmediately Flag indicating whether [onConsumed] should be
 * called with the [Consumed][Event.Consumed] event before (`true`) or after
 * (`false`) invoking the suspending event [handler]. Set this to `true` to make
 * sure the event will be consumed no matter if the [handler] ever returns.
 *
 * @param whileConsumed Optional callback that is [launched][LaunchedEffect]
 * while the given [event] is `null` or [Consumed][Event.Consumed].
 *
 * @param handler The event handler receiving the [data][Event.Triggered.data]
 * of the [Triggered][Event.Triggered] event.
 */
@Composable
@NonRestartableComposable
public fun <T> EventHandler(
    event: Event<T>?,
    onConsumed: (consumed: Event.Consumed) -> Unit,
    consumeImmediately: Boolean = false,
    whileConsumed: (suspend () -> Unit)? = null,
    handler: suspend (content: T) -> Unit,
) {
    EventHandler(
        Unit,
        event = event,
        onConsumed = onConsumed,
        consumeImmediately = consumeImmediately,
        whileConsumed = whileConsumed,
        handler = handler
    )
}

/**
 * [Launches][LaunchedEffect] a side effect that execute the given event
 * [handler] when the [event] becomes [Triggered][Event.Triggered].
 *
 * @param keys Optional keys that will trigger the effect to be cancelled and
 * re-launched when the [EventHandler] is recomposed with any different keys.
 *
 * @param event The [Event] triggering the effect whenever it becomes non-`null`
 * and [Triggered][Event.Triggered].
 *
 * @param onConsumed Callback to pass back any [Consumed][Event.Consumed] event
 * from the effect. Depending on the value of [consumeImmediately], the callback
 * will be invoked before or after invoking the event [handler].
 *
 * @param consumeImmediately Flag indicating whether [onConsumed] should be
 * called with the [Consumed][Event.Consumed] event before (`true`) or after
 * (`false`) invoking the suspending event [handler]. Set this to `true` to make
 * sure the event will be consumed no matter if the [handler] ever returns.
 *
 * @param whileConsumed Optional callback that is [launched][LaunchedEffect]
 * while the given [event] is `null` or [Consumed][Event.Consumed].
 *
 * @param handler The event handler receiving the [data][Event.Triggered.data]
 * of the [Triggered][Event.Triggered] event.
 */
@Composable
@NonRestartableComposable
public fun <T> EventHandler(
    vararg keys: Any?,
    event: Event<T>?,
    onConsumed: (consumed: Event.Consumed) -> Unit,
    consumeImmediately: Boolean = false,
    whileConsumed: (suspend () -> Unit)? = null,
    handler: suspend (content: T) -> Unit,
) {
    val latestEvent by rememberUpdatedState(event)
    val latestHandler by rememberUpdatedState(handler)
    val latestOnConsumed by rememberUpdatedState(onConsumed)

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(*keys) {
        snapshotFlow { latestEvent }
            .filterNotNull()
            .filterIsInstance<Event.Triggered<T>>()
            .collect { event ->
                if (consumeImmediately) latestOnConsumed(event.consumed())
                coroutineScope.launch {
                    latestHandler(event.data)
                    if (!consumeImmediately) latestOnConsumed(event.consumed())
                }
            }
    }

    if (whileConsumed != null) {
        val latestWhileConsumed by rememberUpdatedState(whileConsumed)
        LaunchedEffect(*keys) {
            snapshotFlow { latestEvent == null || latestEvent is Event.Consumed }
                .collectLatest { isConsumed ->
                    if (isConsumed) latestWhileConsumed()
                }
        }
    }
}
