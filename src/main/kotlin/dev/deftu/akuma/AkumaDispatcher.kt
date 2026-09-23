package dev.deftu.akuma

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

// Framework-agnostic command registry and dispatch. Adapters (e.g. akuma-jda's
// `CommandListener`) translate their native interaction events into the (commandName,
// groupName, subcommandName) triple this resolves against, then hand off a context built
// from that event.
public open class AkumaDispatcher {

    private val _commands = mutableListOf<CommandDefinition>()
    public val commands: List<CommandDefinition>
        get() = _commands

    public val componentRegistry: ComponentRegistry = ComponentRegistry()

    // Overridable so a consumer can route command/autocomplete failures into its own
    // error-reporting setup instead of them vanishing into an unhandled coroutine exception.
    public var errorHandler: (Throwable) -> Unit = Throwable::printStackTrace

    private val scope = CoroutineScope(SupervisorJob() + CoroutineExceptionHandler { _, throwable ->
        errorHandler(throwable)
    })

    // Guild-scoped commands (e.g. one per dynamically registered tag) get re-registered on
    // every mutation, which would otherwise accumulate a stale duplicate per name on every
    // resync for the lifetime of the process -- replace by name instead of blindly appending.
    public fun register(new: List<CommandDefinition>) {
        val names = new.map(CommandDefinition::name).toSet()
        _commands.removeAll { it.name in names }
        _commands.addAll(new)
    }

    public fun findCommand(name: String): CommandDefinition? {
        return _commands.find { it.name == name }
    }

    public fun dispatchCommand(
        commandName: String,
        groupName: String?,
        subcommandName: String?,
        context: CommandContext,
    ) {
        val command = findCommand(commandName) ?: return
        val action = resolveAction(command, groupName, subcommandName) ?: return
        scope.launch { action.invoke(context) }
    }

    public fun dispatchAutoComplete(
        commandName: String,
        groupName: String?,
        subcommandName: String?,
        optionName: String,
        context: AutoCompleteContext,
    ) {
        val command = findCommand(commandName) ?: return
        val option = resolveOption(command, groupName, subcommandName, optionName) as? CommandOption.AutoCompletingCommandOption ?: return
        scope.launch { option.autoComplete?.invoke(context) }
    }

    public fun dispatchComponent(customId: String, context: ComponentContext) {
        componentRegistry.dispatch(customId, context)
    }

    public fun dispatchModal(customId: String, context: ModalContext) {
        componentRegistry.dispatch(customId, context)
    }

    private fun resolveAction(
        command: CommandDefinition,
        groupName: String?,
        subcommandName: String?,
    ): (suspend CommandContext.() -> Unit)? {
        return when {
            groupName != null -> command.groups
                .find { it.name == groupName }
                ?.children?.find { it.name == subcommandName }
                ?.action

            subcommandName != null -> command.children
                .find { it.name == subcommandName }
                ?.action

            else -> command.action
        }
    }

    private fun resolveOption(
        command: CommandDefinition,
        groupName: String?,
        subcommandName: String?,
        optionName: String,
    ): CommandOption? {
        val options = when {
            groupName != null -> command.groups
                .find { it.name == groupName }
                ?.children?.find { it.name == subcommandName }
                ?.options

            subcommandName != null -> command.children
                .find { it.name == subcommandName }
                ?.options

            else -> command.options
        }

        return options?.find { it.name == optionName }
    }

}
