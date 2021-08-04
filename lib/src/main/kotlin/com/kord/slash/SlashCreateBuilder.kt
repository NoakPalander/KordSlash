package com.kord.slash

import dev.kord.common.annotation.KordPreview
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.rest.builder.interaction.ApplicationCommandCreateBuilder

@KordPreview
class SlashCreateBuilder {
    var create: (suspend ApplicationCommandCreateBuilder.() -> Unit)? = null
        private set

    lateinit var event: suspend InteractionCreateEvent.() -> Unit
        private set

    fun onCreate(enabled: Boolean, handle: suspend ApplicationCommandCreateBuilder.() -> Unit) {
        if (enabled)
            create = handle
    }

    fun onEvent(handle: suspend InteractionCreateEvent.() -> Unit) {
        event = handle
    }
}