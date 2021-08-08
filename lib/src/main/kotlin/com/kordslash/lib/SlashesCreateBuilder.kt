package com.kordslash.lib

import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.behavior.createApplicationCommand
import dev.kord.core.entity.Guild
import dev.kord.core.entity.interaction.GlobalApplicationCommand
import dev.kord.core.entity.interaction.GuildApplicationCommand
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.core.on
import kotlinx.coroutines.flow.first


@KordPreview
sealed class Command {
    data class Guild(val cmd: GuildApplicationCommand) : Command() {}
    data class Global(val cmd: GlobalApplicationCommand) : Command() {}

    suspend fun delete() {
        when(this) {
            is Guild -> { cmd.delete() }
            is Global -> { cmd.delete() }
        }
    }
}

@KordPreview
class SlashesCreateBuilder internal constructor(private val client: Kord){
    internal val slashes = mutableListOf<Slash>()

    suspend fun slash(name: String, guild: Guild? = null): Command {
        return if (guild != null) {
            Command.Guild(guild.commands.first { it.name == name })
        } else {
            Command.Global(client.slashCommands.getGlobalApplicationCommands().first { it.name == name })
        }
    }

    @KordPreview
    suspend fun slash(name: String, description: String, guild: Guild? = null, handle: suspend SlashCreateBuilder.() -> Unit) {
        val inst = SlashCreateBuilder()
        inst.handle()
        slashes += Slash(name, description, inst.create, inst.event, guild)
    }
}

@KordPreview
suspend fun Kord.slashes(handle: suspend SlashesCreateBuilder.(Kord) -> Unit) {
    val inst = SlashesCreateBuilder(this)
    inst.handle(this)

    inst.slashes.forEach { slash ->
        slash.create?.let {
            if (slash.guild != null) {
                slash.guild.createApplicationCommand(slash.name, slash.description) {
                    it.invoke(this)
                }
            } else {
                this.slashCommands.createGlobalApplicationCommand(slash.name, slash.description) {
                    it.invoke(this)
                }
            }
        }
    }

    on<InteractionCreateEvent> {
        val name = interaction.data.data.name.value!!
        inst.slashes.first { it.name == name }.event(this)
    }
}