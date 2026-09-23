package dev.deftu.akuma.kord

import dev.deftu.akuma.AkumaButtonStyle
import dev.deftu.akuma.AkumaComponent
import dev.kord.common.entity.ButtonStyle
import dev.kord.rest.builder.component.ButtonBuilder

public fun AkumaButtonStyle.toKord(): ButtonStyle {
    return when (this) {
        AkumaButtonStyle.PRIMARY -> ButtonStyle.Primary
        AkumaButtonStyle.SECONDARY -> ButtonStyle.Secondary
        AkumaButtonStyle.SUCCESS -> ButtonStyle.Success
        AkumaButtonStyle.DANGER -> ButtonStyle.Danger
    }
}

public fun AkumaComponent.Button.toKord(): ButtonBuilder.InteractionButtonBuilder {
    check(label != null || emoji != null) { "Button '$customId' needs a label, an emoji, or both" }

    return ButtonBuilder.InteractionButtonBuilder(style.toKord(), customId).apply {
        label = this@toKord.label
        emoji = this@toKord.emoji?.toKordEmoji()
        disabled = this@toKord.isDisabled
    }
}
