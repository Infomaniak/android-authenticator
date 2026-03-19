/*
 * Infomaniak Authenticator - Android
 * Copyright (C) 2026 Infomaniak Network SA
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
@file:Suppress("NOTHING_TO_INLINE")

package com.infomaniak.auth.lib.internal.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlin.experimental.ExperimentalTypeInference
import kotlinx.coroutines.CoroutineStart.UNDISPATCHED as Undispatched

/**
 * Pass at least one racer to use [raceOf], or use [race] if the racers need to be launched
 * dynamically.
 */
@Suppress("DeprecatedCallableAddReplaceWith", "RedundantSuspendModifier")
@Deprecated("A race needs racers.", level = DeprecationLevel.ERROR) // FOOL GUARD, DO NOT REMOVE
internal suspend fun <T> raceOf(): T = throw UnsupportedOperationException("A race needs racers.")

/**
 * Races all the [racers] concurrently. Once the winner completes, all other racers are cancelled,
 * then the value of the winner is returned.
 *
 * Use [race] if the racers need to be launched dynamically.
 */
internal suspend fun <T> raceOf(vararg racers: suspend CoroutineScope.() -> T): T {
    require(racers.isNotEmpty()) { "A race needs racers." }
    return coroutineScope {
        val racersScope = CoroutineScope(Job(parent = coroutineContext[Job]))
        @Suppress("RemoveExplicitTypeArguments")
        select<T> {
            racers.forEach { racer ->
                racersScope.async(
                    start = Undispatched,
                    block = racer
                ).onAwait { resultOfWinner: T ->
                    racersScope.cancel()
                    return@onAwait resultOfWinner
                }
            }
        }
    }
}

/**
 * A scope meant to be used in [race] lambda receiver.
 *
 * You should not implement this interface yourself.
 */
internal interface RacingScope<in T> : CoroutineScope {
    @Deprecated(
        message = "Internal API",
        replaceWith = ReplaceWith("launchRacer(block)", "splitties.coroutines.launchRacer")
    )
    fun launchRacerInternal(block: suspend CoroutineScope.() -> T)
}

/**
 * Launches a racer in this scope.
 * **Must be cancellable**, it will suspend [race] completion otherwise.
 *
 * Use it inside the lambda passed to the [race] function.
 */
internal inline fun <T> RacingScope<T>.launchRacer(noinline block: suspend CoroutineScope.() -> T) {
    @Suppress("DEPRECATION")
    launchRacerInternal(block)
}

/**
 * Starts a [RacingScope] with the suspending [builder] lambda in which you can call [launchRacer]
 * each time you want to launch a racer coroutine. Once a racer completes, the [builder] and all
 * racers are cancelled, then the value of the winning racer is returned.
 *
 * For races where the number of racers is static, you can use the slightly more efficient [raceOf]
 * function and directly pass the cancellable lambdas you want to race concurrently.
 */
@OptIn(ExperimentalTypeInference::class)
internal suspend fun <T> race(
    @BuilderInference
    builder: suspend RacingScope<T>.() -> Unit
): T = coroutineScope {
    @Suppress("RemoveExplicitTypeArguments")
    select<T> {
        val builderScope = CoroutineScope(Job(parent = coroutineContext[Job]))

        val racingScope = object : RacingScope<T>, CoroutineScope by this@coroutineScope {

            var raceWon = false

            @Suppress("OverridingDeprecatedMember", "OVERRIDE_DEPRECATION")
            override fun launchRacerInternal(block: suspend CoroutineScope.() -> T) {
                if (raceWon) return // A racer already completed.
                builderScope.async(
                    start = Undispatched,
                    block = block
                ).onAwait { resultOfWinner: T ->
                    raceWon = true
                    builderScope.cancel()
                    return@onAwait resultOfWinner
                }
            }
        }
        builderScope.launch(start = Undispatched) {
            racingScope.builder()
        }
    }
}
