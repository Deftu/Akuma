package dev.deftu.akuma.jda

import dev.deftu.akuma.AkumaButtonStyle
import dev.deftu.akuma.AkumaComponent
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.buttons.ButtonStyle
import net.dv8tion.jda.api.entities.emoji.Emoji

public fun AkumaButtonStyle.toJda(): ButtonStyle {
    return when (this) {
        AkumaButtonStyle.PRIMARY -> ButtonStyle.PRIMARY
        AkumaButtonStyle.SECONDARY -> ButtonStyle.SECONDARY
        AkumaButtonStyle.SUCCESS -> ButtonStyle.SUCCESS
        AkumaButtonStyle.DANGER -> ButtonStyle.DANGER
    }
}

public fun AkumaComponent.Button.toJda(): Button {
    val jdaLabel = label
    val jdaEmoji = emoji?.let(Emoji::fromFormatted)
    val built = when {
        jdaLabel != null && jdaEmoji != null -> Button.of(style.toJda(), customId, jdaLabel, jdaEmoji)
        jdaLabel != null -> Button.of(style.toJda(), customId, jdaLabel)
        jdaEmoji != null -> Button.of(style.toJda(), customId, jdaEmoji)
        else -> error("Button '$customId' needs a label, an emoji, or both")
    }

    return if (isDisabled) built.asDisabled() else built
}
