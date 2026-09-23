package dev.deftu.akuma.jda

import dev.deftu.akuma.AkumaChoice
import dev.deftu.akuma.AkumaCommandType
import dev.deftu.akuma.CommandDefinition
import dev.deftu.akuma.CommandGroupDefinition
import dev.deftu.akuma.CommandOption
import net.dv8tion.jda.api.interactions.InteractionContextType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData

// USER/MESSAGE commands map to `Commands.user`/`Commands.message`, which return the shared
// `CommandData` supertype -- it has no `addOptions`/`addSubcommands` at all, so a context-menu
// command can't carry the slash-only surface even by mistake. The name/NSFW/permissions/contexts
// setters below live on `CommandData` itself, so they apply uniformly to all three command types.
public fun CommandDefinition.toJdaData(): CommandData {
    val data: CommandData = when (type) {
        AkumaCommandType.SLASH -> Commands.slash(name, description ?: "No description provided").apply {
            setDescriptionLocalizations(this@toJdaData.descriptionLocalizations.toJdaLocalizations())
            sortedOptions.map(CommandOption::toJdaData).forEach(::addOptions)
            children.map(CommandDefinition::toJdaSubData).forEach(::addSubcommands)
            groups.map(CommandGroupDefinition::toJdaData).forEach(::addSubcommandGroups)
        }

        AkumaCommandType.USER -> Commands.user(name)
        AkumaCommandType.MESSAGE -> Commands.message(name)
    }

    data.isNSFW = isNsfw
    data.defaultPermissions = defaultGuildPermissions.toJda()
    data.setContexts(if (isGuildOnly) setOf(InteractionContextType.GUILD) else InteractionContextType.ALL)
    data.setNameLocalizations(nameLocalizations.toJdaLocalizations())

    return data
}

public fun CommandDefinition.toJdaSubData(): SubcommandData {
    val data = SubcommandData(name, description ?: "No description provided")
    data.setNameLocalizations(nameLocalizations.toJdaLocalizations())
    data.setDescriptionLocalizations(descriptionLocalizations.toJdaLocalizations())

    sortedOptions.map(CommandOption::toJdaData).forEach(data::addOptions)

    return data
}

public fun CommandGroupDefinition.toJdaData(): SubcommandGroupData {
    val data = SubcommandGroupData(name, description ?: "No description provided")
    data.setNameLocalizations(nameLocalizations.toJdaLocalizations())
    data.setDescriptionLocalizations(descriptionLocalizations.toJdaLocalizations())

    children.map(CommandDefinition::toJdaSubData).forEach(data::addSubcommands)

    return data
}

public fun CommandOption.toJdaData(): OptionData {
    val data = OptionData(type.toJda(), name, description ?: "No description provided", isRequired)
    data.setNameLocalizations(nameLocalizations.toJdaLocalizations())
    data.setDescriptionLocalizations(descriptionLocalizations.toJdaLocalizations())

    if (this is CommandOption.AutoCompletingCommandOption) {
        data.isAutoComplete = isAutoComplete
        if (choices.isNotEmpty()) {
            // Throws if `isAutoComplete` is also true -- Discord doesn't allow an option to
            // have both static choices and autocomplete, and JDA enforces that here.
            data.addChoices(choices.map(AkumaChoice::toJda))
        }
    }

    when (this) {
        is CommandOption.StringCommandOption -> {
            minLength?.let(data::setMinLength)
            maxLength?.let(data::setMaxLength)
        }

        is CommandOption.NumberCommandOption -> {
            minValue?.let(data::setMinValue)
            maxValue?.let(data::setMaxValue)
        }

        is CommandOption.ChannelCommandOption -> {
            data.setChannelTypes(channelTypes.toJda())
        }

        else -> Unit
    }

    return data
}
