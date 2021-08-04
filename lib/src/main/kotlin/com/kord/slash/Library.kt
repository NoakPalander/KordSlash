package com.kord.slash

import dev.kord.common.annotation.KordPreview
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.rest.builder.interaction.ApplicationCommandCreateBuilder

@KordPreview
data class Slash(val description: String,
                 val create: (suspend ApplicationCommandCreateBuilder.() -> Unit)?,
                 val event: suspend (InteractionCreateEvent) -> Unit)