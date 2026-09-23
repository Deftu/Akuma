package dev.deftu.akuma.jda

import dev.deftu.akuma.OptionKind
import net.dv8tion.jda.api.interactions.commands.OptionType

public fun OptionKind.toJda(): OptionType {
    return when (this) {
        OptionKind.STRING -> OptionType.STRING
        OptionKind.INTEGER -> OptionType.INTEGER
        OptionKind.BOOLEAN -> OptionType.BOOLEAN
        OptionKind.USER -> OptionType.USER
        OptionKind.CHANNEL -> OptionType.CHANNEL
        OptionKind.ROLE -> OptionType.ROLE
        OptionKind.MENTIONABLE -> OptionType.MENTIONABLE
        OptionKind.NUMBER -> OptionType.NUMBER
        OptionKind.ATTACHMENT -> OptionType.ATTACHMENT
    }
}
