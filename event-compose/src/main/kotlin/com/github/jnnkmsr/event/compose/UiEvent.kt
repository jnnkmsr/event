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

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Represents a one-time UI event that can be either [Triggered] or [Consumed].
 *
 * A [Triggered] event holds [data][Triggered.data] to be used when consuming
 * the event.
 */
@Immutable
public sealed interface UiEvent<out T> : Parcelable {

    /** A unique key identifying the emitter of the [UiEvent]. */
    public val emitter: @RawValue Any?

    /** A unique key identifying the designated receiver of the [UiEvent]. */
    public val receiver: @RawValue Any?

    /** A triggered [UiEvent] to be consumed. */
    @Parcelize
    public data class Triggered<out T>(
        /** Data to be provided when consuming the event. */
        public val data: @RawValue T,
        override val emitter: @RawValue Any? = null,
        override val receiver: @RawValue Any? = null,
    ) : UiEvent<T>

    /** A consumed [UiEvent] that cannot be consumed again. */
    @Parcelize
    public data class Consumed(
        override val emitter: @RawValue Any? = null,
        override val receiver: @RawValue Any? = null,
    ) : UiEvent<Nothing>
}

/**
 * Converts `this` [UiEvent] into a [Consumed][UiEvent.Consumed] event. If
 * `this` event is already [Consumed][UiEvent.Consumed], it will be returned
 * as is.
 */
public fun <T> UiEvent<T>.consumed(): UiEvent.Consumed =
    when (this) {
        is UiEvent.Triggered -> UiEvent.Consumed(emitter, receiver)
        is UiEvent.Consumed -> this
    }
