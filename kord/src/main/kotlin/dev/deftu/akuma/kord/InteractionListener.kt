package dev.deftu.akuma.kord

import dev.deftu.akuma.AkumaDispatcher
import dev.deftu.akuma.CommandDefinition
import dev.kord.common.entity.ComponentType
import dev.kord.core.Kord
import dev.kord.core.entity.interaction.GroupCommand
import dev.kord.core.entity.interaction.InteractionCommand
import dev.kord.core.entity.interaction.RootCommand
import dev.kord.core.entity.interaction.SubCommand
import dev.kord.core.event.interaction.AutoCompleteInteractionCreateEvent
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.MessageCommandInteractionCreateEvent
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.core.event.interaction.UserCommandInteractionCreateEvent
import dev.kord.core.on
import java.util.concurrent.ConcurrentHashMap

public class InteractionListener private constructor(kord: Kord) {
    public companion object {
        private val instances = ConcurrentHashMap<Kord, InteractionListener>()

        public fun getOrRegister(kord: Kord): InteractionListener {
            return instances.computeIfAbsent(kord) { InteractionListener(it) }
        }
    }

    public val dispatcher: AkumaDispatcher = AkumaDispatcher()

    public val commands: List<CommandDefinition>
        get() = dispatcher.commands

    public var errorHandler: (Throwable) -> Unit
        get() = dispatcher.errorHandler
        set(value) {
            dispatcher.errorHandler = value
        }

    public fun register(new: List<CommandDefinition>) {
        dispatcher.register(new)
    }

    init {
        kord.on<ChatInputCommandInteractionCreateEvent> {
            val (commandName, groupName, subcommandName) = interaction.command.dispatchNames()
            dispatcher.dispatchCommand(
                commandName = commandName,
                groupName = groupName,
                subcommandName = subcommandName,
                context = KordCommandContext(interaction, dispatcher.componentRegistry),
            )
        }

        kord.on<UserCommandInteractionCreateEvent> {
            dispatcher.dispatchCommand(
                commandName = interaction.invokedCommandName,
                groupName = null,
                subcommandName = null,
                context = KordUserCommandContext(interaction, dispatcher.componentRegistry),
            )
        }

        kord.on<MessageCommandInteractionCreateEvent> {
            dispatcher.dispatchCommand(
                commandName = interaction.invokedCommandName,
                groupName = null,
                subcommandName = null,
                context = KordMessageCommandContext(interaction, dispatcher.componentRegistry),
            )
        }

        kord.on<AutoCompleteInteractionCreateEvent> {
            val (commandName, groupName, subcommandName) = interaction.command.dispatchNames()
            dispatcher.dispatchAutoComplete(
                commandName = commandName,
                groupName = groupName,
                subcommandName = subcommandName,
                optionName = interaction.command.options.entries.first { it.value.focused }.key,
                context = KordAutoCompleteContext(interaction),
            )
        }

        kord.on<ButtonInteractionCreateEvent> {
            dispatcher.dispatchComponent(interaction.componentId, KordButtonContext(interaction, dispatcher.componentRegistry))
        }

        kord.on<SelectMenuInteractionCreateEvent> {
            val context = if (interaction.componentType == ComponentType.StringSelect) {
                KordStringSelectContext(interaction, dispatcher.componentRegistry)
            } else {
                KordEntitySelectContext(interaction, dispatcher.componentRegistry)
            }

            dispatcher.dispatchComponent(interaction.componentId, context)
        }

        kord.on<ModalSubmitInteractionCreateEvent> {
            dispatcher.dispatchModal(interaction.modalId, KordModalContext(interaction, dispatcher.componentRegistry))
        }
    }
}

private fun InteractionCommand.dispatchNames(): Triple<String, String?, String?> {
    return when (this) {
        is GroupCommand -> Triple(rootName, groupName, name)
        is SubCommand -> Triple(rootName, null, name)
        is RootCommand -> Triple(rootName, null, null)
    }
}
