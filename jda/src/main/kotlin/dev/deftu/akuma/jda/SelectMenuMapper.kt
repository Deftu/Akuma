package dev.deftu.akuma.jda

import dev.deftu.akuma.AkumaComponent
import dev.deftu.akuma.AkumaEntitySelectType
import net.dv8tion.jda.api.components.selections.EntitySelectMenu
import net.dv8tion.jda.api.components.selections.SelectOption
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import net.dv8tion.jda.api.entities.emoji.Emoji

public fun AkumaComponent.StringSelect.Option.toJda(): SelectOption {
    var option = SelectOption.of(label, value)
    description?.let { option = option.withDescription(it) }
    emoji?.let { option = option.withEmoji(Emoji.fromFormatted(it)) }
    return option.withDefault(isDefault)
}

public fun AkumaComponent.StringSelect.toJda(): StringSelectMenu {
    val builder = StringSelectMenu.create(customId)
    placeholder?.let(builder::setPlaceholder)
    builder.setMinValues(minValues)
    builder.setMaxValues(maxValues)
    builder.addOptions(options.map { it.toJda() })
    return builder.build()
}

// JDA 6.x's `EntitySelectMenu.SelectTarget` has no MENTIONABLE constant -- USER+ROLE together
// reproduces the old "mentionable" behaviour.
public fun AkumaEntitySelectType.toJdaTargets(): Set<EntitySelectMenu.SelectTarget> {
    return when (this) {
        AkumaEntitySelectType.USER -> setOf(EntitySelectMenu.SelectTarget.USER)
        AkumaEntitySelectType.ROLE -> setOf(EntitySelectMenu.SelectTarget.ROLE)
        AkumaEntitySelectType.CHANNEL -> setOf(EntitySelectMenu.SelectTarget.CHANNEL)
        AkumaEntitySelectType.MENTIONABLE -> setOf(EntitySelectMenu.SelectTarget.USER, EntitySelectMenu.SelectTarget.ROLE)
    }
}

public fun AkumaComponent.EntitySelect.toJda(): EntitySelectMenu {
    val targets = entityTypes.flatMap(AkumaEntitySelectType::toJdaTargets).toSet()
    val builder = EntitySelectMenu.create(customId, targets)
    placeholder?.let(builder::setPlaceholder)
    builder.setMinValues(minValues)
    builder.setMaxValues(maxValues)
    if (channelTypes.isNotEmpty()) {
        builder.setChannelTypes(channelTypes.toJda())
    }

    return builder.build()
}
