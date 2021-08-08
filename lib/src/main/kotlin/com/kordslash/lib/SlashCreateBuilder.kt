package com.kordslash.lib

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.Guild
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.rest.builder.interaction.ApplicationCommandCreateBuilder

@KordPreview
class SlashCreateBuilder internal constructor() {
    var create: (suspend ApplicationCommandCreateBuilder.() -> Unit)? = null
        private set

    lateinit var event: suspend InteractionCreateEvent.() -> Unit
        private set

    fun onCreate(handle: suspend ApplicationCommandCreateBuilder.() -> Unit) {
        create = handle
    }

    fun onEvent(handle: suspend InteractionCreateEvent.() -> Unit) {
        event = handle
    }
}

@KordPreview
internal data class Slash(
    val name: String, internal val description: String,
    val create: (suspend ApplicationCommandCreateBuilder.() -> Unit)?,
    val event: suspend (InteractionCreateEvent) -> Unit,
    val guild: Guild? = null)
