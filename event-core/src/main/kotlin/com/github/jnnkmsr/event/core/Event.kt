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

package com.github.jnnkmsr.event.core

import androidx.compose.runtime.Immutable

/**
 * Represents a one-time event that can be either [Triggered] or [Consumed].
 *
 * A [Triggered] event holds [data][Triggered.data] to be used when consuming
 * the event.
 */
@Immutable
public sealed interface Event<out T> {

    /** A unique key identifying the emitter of the [Event]. */
    public val emitter: Any?

    /** A unique key identifying the designated receiver of the [Event]. */
    public val receiver: Any?

    /** A triggered [Event] to be consumed. */
    public data class Triggered<out T>(
        /** Data to be provided when consuming the event. */
        public val data: T,
        override val emitter: Any? = null,
        override val receiver: Any? = null,
    ) : Event<T>

    /** A consumed [Event] that cannot be consumed again. */
    public data class Consumed(
        override val emitter: Any? = null,
        override val receiver: Any? = null,
    ) : Event<Nothing>
}

/**
 * Converts `this` [Event] into a [Consumed][Event.Consumed] event. If `this`
 * event is already [Consumed][Event.Consumed], it will be returned as is.
 */
public fun <T> Event<T>.consumed(): Event.Consumed = when (this) {
    is Event.Triggered -> Event.Consumed(emitter, receiver)
    is Event.Consumed -> this
}
