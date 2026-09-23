package dev.deftu.akuma.kord

import dev.deftu.akuma.OptionKind
import dev.kord.common.entity.ApplicationCommandOptionType

public fun OptionKind.toKord(): ApplicationCommandOptionType {
    return when (this) {
        OptionKind.STRING -> ApplicationCommandOptionType.String
        OptionKind.INTEGER -> ApplicationCommandOptionType.Integer
        OptionKind.BOOLEAN -> ApplicationCommandOptionType.Boolean
        OptionKind.USER -> ApplicationCommandOptionType.User
        OptionKind.CHANNEL -> ApplicationCommandOptionType.Channel
        OptionKind.ROLE -> ApplicationCommandOptionType.Role
        OptionKind.MENTIONABLE -> ApplicationCommandOptionType.Mentionable
        OptionKind.NUMBER -> ApplicationCommandOptionType.Number
        OptionKind.ATTACHMENT -> ApplicationCommandOptionType.Attachment
    }
}
