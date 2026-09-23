package dev.deftu.akuma.kord

import dev.deftu.akuma.AkumaCommandType
import dev.deftu.akuma.CommandDefinition
import dev.deftu.akuma.CommandGroupDefinition
import dev.deftu.akuma.CommandOption
import dev.deftu.akuma.OptionKind
import dev.kord.rest.builder.interaction.ApplicationCommandCreateBuilder
import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import dev.kord.rest.builder.interaction.GlobalMultiApplicationCommandBuilder
import dev.kord.rest.builder.interaction.GuildMultiApplicationCommandBuilder
import dev.kord.rest.builder.interaction.RootInputChatBuilder
import dev.kord.rest.builder.interaction.SubCommandBuilder
import dev.kord.rest.builder.interaction.attachment
import dev.kord.rest.builder.interaction.boolean
import dev.kord.rest.builder.interaction.channel
import dev.kord.rest.builder.interaction.group
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.interaction.mentionable
import dev.kord.rest.builder.interaction.number
import dev.kord.rest.builder.interaction.role
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.interaction.subCommand
import dev.kord.rest.builder.interaction.user

private const val NO_DESCRIPTION = "No description provided"

// Kord's `*CreateBuilderImpl` classes (the ones a `CommandDefinition -> data object` mapper would
// naturally construct, mirroring the JDA adapter's `CommandData`/`OptionData`) are Kotlin-internal
// to kord-rest -- only the DSL entry points (`input`/`user`/`message`/`string`/`subCommand`/...)
// are public. So unlike `akuma-jda`'s `CommandDataMapper`, this one builds commands by calling
// into a `MultiApplicationCommandBuilder` rather than returning a standalone data object per
// `CommandDefinition`.
public fun GlobalMultiApplicationCommandBuilder.addCommand(definition: CommandDefinition) {
    when (definition.type) {
        AkumaCommandType.SLASH -> input(definition.name, definition.description ?: NO_DESCRIPTION) {
            descriptionLocalizations = definition.descriptionLocalizations.toKordLocalizations()
            addOptions(definition)
            applyCommon(definition)
            if (definition.isGuildOnly) dmPermission = false
        }

        AkumaCommandType.USER -> user(definition.name) {
            applyCommon(definition)
            if (definition.isGuildOnly) dmPermission = false
        }

        AkumaCommandType.MESSAGE -> message(definition.name) {
            applyCommon(definition)
            if (definition.isGuildOnly) dmPermission = false
        }
    }
}

public fun GuildMultiApplicationCommandBuilder.addCommand(definition: CommandDefinition) {
    when (definition.type) {
        AkumaCommandType.SLASH -> input(definition.name, definition.description ?: NO_DESCRIPTION) {
            descriptionLocalizations = definition.descriptionLocalizations.toKordLocalizations()
            addOptions(definition)
            applyCommon(definition)
        }

        AkumaCommandType.USER -> user(definition.name) { applyCommon(definition) }
        AkumaCommandType.MESSAGE -> message(definition.name) { applyCommon(definition) }
    }
}

private fun ApplicationCommandCreateBuilder.applyCommon(definition: CommandDefinition) {
    nsfw = definition.isNsfw
    defaultMemberPermissions = definition.defaultGuildPermissions.toKord()
    nameLocalizations = definition.nameLocalizations.toKordLocalizations()
}

private fun RootInputChatBuilder.addOptions(definition: CommandDefinition) {
    definition.sortedOptions.forEach { addOption(it) }
    definition.children.forEach { child ->
        subCommand(child.name, child.description ?: NO_DESCRIPTION) { fill(child) }
    }
    definition.groups.forEach { group -> addGroup(group) }
}

private fun RootInputChatBuilder.addGroup(group: CommandGroupDefinition) {
    group(group.name, group.description ?: NO_DESCRIPTION) {
        group.children.forEach { child ->
            subCommand(child.name, child.description ?: NO_DESCRIPTION) { fill(child) }
        }

        nameLocalizations = group.nameLocalizations.toKordLocalizations()
        descriptionLocalizations = group.descriptionLocalizations.toKordLocalizations()
    }
}

private fun SubCommandBuilder.fill(child: CommandDefinition) {
    child.sortedOptions.forEach { addOption(it) }
    nameLocalizations = child.nameLocalizations.toKordLocalizations()
    descriptionLocalizations = child.descriptionLocalizations.toKordLocalizations()
}

// Kord doesn't reject an option carrying both static choices and autocomplete itself (unlike JDA,
// which throws at `OptionData.addChoices` time) -- checked client-side here instead, so both
// adapters reject the same invalid definition the same way.
private fun BaseInputChatBuilder.addOption(option: CommandOption) {
    if (option is CommandOption.AutoCompletingCommandOption && option.isAutoComplete && option.choices.isNotEmpty()) {
        error("An option cannot have both autocomplete and static choices")
    }

    val description = option.description ?: NO_DESCRIPTION
    when (option) {
        is CommandOption.StringCommandOption -> string(option.name, description) {
            option.minLength?.let { minLength = it }
            option.maxLength?.let { maxLength = it }
            option.choices.forEach { choice(it.name, it.value) {} }
            fillCommon(option)
        }

        is CommandOption.NumberCommandOption -> when (option.type) {
            OptionKind.INTEGER -> integer(option.name, description) {
                option.minValue?.let { minValue = it.toLong() }
                option.maxValue?.let { maxValue = it.toLong() }
                option.choices.forEach { choice(it.name, it.value.toLong()) {} }
                fillCommon(option)
            }

            OptionKind.NUMBER -> number(option.name, description) {
                option.minValue?.let { minValue = it }
                option.maxValue?.let { maxValue = it }
                option.choices.forEach { choice(it.name, it.value.toDouble()) {} }
                fillCommon(option)
            }

            else -> error("Unsupported number option kind: ${option.type}")
        }

        is CommandOption.ChannelCommandOption -> channel(option.name, description) {
            if (option.channelTypes.isNotEmpty()) {
                channelTypes = option.channelTypes.toKord().toMutableList()
            }

            fillCommon(option)
        }

        else -> when (option.type) {
            OptionKind.BOOLEAN -> boolean(option.name, description) { fillCommon(option) }
            OptionKind.USER -> user(option.name, description) { fillCommon(option) }
            OptionKind.ROLE -> role(option.name, description) { fillCommon(option) }
            OptionKind.MENTIONABLE -> mentionable(option.name, description) { fillCommon(option) }
            OptionKind.ATTACHMENT -> attachment(option.name, description) { fillCommon(option) }
            else -> error("Unsupported basic option kind: ${option.type}")
        }
    }
}

private fun dev.kord.rest.builder.interaction.OptionsBuilder.fillCommon(option: CommandOption) {
    required = option.isRequired
    nameLocalizations = option.nameLocalizations.toKordLocalizations()
    descriptionLocalizations = option.descriptionLocalizations.toKordLocalizations()
    if (option is CommandOption.AutoCompletingCommandOption) {
        autocomplete = option.isAutoComplete
    }
}
