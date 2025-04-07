package dev.deftu.akuma

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

public class CommandListener : ListenerAdapter() {

    public companion object {

        public fun getOrRegister(jda: JDA): CommandListener {
            return jda.registeredListeners.find { it is CommandListener } as? CommandListener
                ?: CommandListener().also { jda.addEventListener(it) }
        }

    }

    internal val commands = mutableListOf<CommandDefinition>()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val command = commands.find { definition -> definition.name == event.name }
        if (command == null) {
            return
        }

        when {
            event.subcommandGroup != null -> {
                val group = command.groups.find { it.name == event.subcommandGroup }
                if (group == null) {
                    return
                }

                val subcommand = group.children.find { it.name == event.subcommandName }
                if (subcommand == null) {
                    return
                }

                val context = CommandContext(event)

                try {
                    GlobalScope.launch {
                        subcommand.action?.invoke(context)
                    }
                } catch (_: Exception) {
                }
            }

            event.subcommandName != null -> {
                val subcommand = command.children.find { it.name == event.subcommandName }
                if (subcommand == null) {
                    return
                }

                val context = CommandContext(event)

                try {
                    GlobalScope.launch {
                        subcommand.action?.invoke(context)
                    }
                } catch (_: Exception) {
                }
            }

            else -> {
                val context = CommandContext(event)

                try {
                    GlobalScope.launch {
                        command.action?.invoke(context)
                    }
                } catch (_: Exception) {
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        val command = commands.find { definition -> definition.name == event.name }
        if (command == null) {
            return
        }

        when {
            event.subcommandGroup != null -> {
                val group = command.groups.find { it.name == event.subcommandGroup }
                if (group == null) {
                    return
                }

                val subcommand = group.children.find { it.name == event.subcommandName }
                if (subcommand == null) {
                    return
                }

                val option = subcommand.options.find { it.name == event.focusedOption.name } as? CommandOption.AutoCompletingCommandOption
                if (option == null) {
                    return
                }

                GlobalScope.launch {
                    option.autoComplete?.invoke(event)
                }
            }

            event.subcommandName != null -> {
                val subcommand = command.children.find { it.name == event.subcommandName }
                if (subcommand == null) {
                    return
                }

                val option = subcommand.options.find { it.name == event.focusedOption.name } as? CommandOption.AutoCompletingCommandOption
                if (option == null) {
                    return
                }

                GlobalScope.launch {
                    option.autoComplete?.invoke(event)
                }
            }

            else -> {
                val option = command.options.find { it.name == event.focusedOption.name } as? CommandOption.AutoCompletingCommandOption
                if (option == null) {
                    return
                }

                GlobalScope.launch {
                    option.autoComplete?.invoke(event)
                }
            }
        }
    }

}
