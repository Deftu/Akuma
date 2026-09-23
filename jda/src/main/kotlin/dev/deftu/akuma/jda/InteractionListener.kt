package dev.deftu.akuma.jda

import dev.deftu.akuma.AkumaDispatcher
import dev.deftu.akuma.CommandDefinition
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

public class InteractionListener : ListenerAdapter() {
    public companion object {
        public fun getOrRegister(jda: JDA): InteractionListener {
            return jda.registeredListeners.find { it is InteractionListener } as? InteractionListener
                ?: InteractionListener().also { jda.addEventListener(it) }
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

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        dispatcher.dispatchCommand(
            commandName = event.name,
            groupName = event.subcommandGroup,
            subcommandName = event.subcommandName,
            context = JdaCommandContext(event, dispatcher.componentRegistry),
        )
    }

    override fun onUserContextInteraction(event: UserContextInteractionEvent) {
        dispatcher.dispatchCommand(
            commandName = event.name,
            groupName = null,
            subcommandName = null,
            context = JdaUserCommandContext(event, dispatcher.componentRegistry),
        )
    }

    override fun onMessageContextInteraction(event: MessageContextInteractionEvent) {
        dispatcher.dispatchCommand(
            commandName = event.name,
            groupName = null,
            subcommandName = null,
            context = JdaMessageCommandContext(event, dispatcher.componentRegistry),
        )
    }

    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        dispatcher.dispatchAutoComplete(
            commandName = event.name,
            groupName = event.subcommandGroup,
            subcommandName = event.subcommandName,
            optionName = event.focusedOption.name,
            context = JdaAutoCompleteContext(event),
        )
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        dispatcher.dispatchComponent(event.componentId, JdaButtonContext(event, dispatcher.componentRegistry))
    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        dispatcher.dispatchComponent(event.componentId, JdaStringSelectContext(event, dispatcher.componentRegistry))
    }

    override fun onEntitySelectInteraction(event: EntitySelectInteractionEvent) {
        dispatcher.dispatchComponent(event.componentId, JdaEntitySelectContext(event, dispatcher.componentRegistry))
    }

    override fun onModalInteraction(event: ModalInteractionEvent) {
        dispatcher.dispatchModal(event.modalId, JdaModalContext(event, dispatcher.componentRegistry))
    }
}
