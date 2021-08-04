package com.kord.slash

import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.behavior.createApplicationCommand
import dev.kord.core.entity.Guild
import dev.kord.core.entity.interaction.GuildApplicationCommand
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.core.on
import kotlinx.coroutines.flow.collect

@KordPreview
class SlashesCreateBuilder {
    val slashes = mutableMapOf<String, Slash>()

    @KordPreview
    suspend fun slash(name: String, description: String, handle: suspend SlashCreateBuilder.() -> Unit) {
        val inst = SlashCreateBuilder()
        inst.handle()
        slashes += name to Slash(description, inst.create, inst.event)
    }
}

@KordPreview
suspend fun Kord.slashes(guilds: List<Guild>, refresh: Boolean = false, handle: suspend SlashesCreateBuilder.() -> Unit) {
    if (refresh) {
        guilds.forEach {
            slashCommands.getGuildApplicationCommands(it.id).collect(GuildApplicationCommand::delete)
        }
    }

    val inst = SlashesCreateBuilder()
    inst.handle()


    inst.slashes.forEach { (name, slash) ->
        slash.create?.let {
            guilds.forEach { guild ->
                guild.createApplicationCommand(name, slash.description) {
                    it.invoke(this)
                }
            }
        }
    }

    on<InteractionCreateEvent> {
        val name = interaction.data.data.name.value
        inst.slashes[name]?.event?.invoke(this)
    }
}