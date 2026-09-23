package dev.deftu.akuma.kord

import dev.deftu.akuma.AkumaComponent
import dev.deftu.akuma.AkumaEntitySelectType
import dev.kord.rest.builder.component.ChannelSelectBuilder
import dev.kord.rest.builder.component.MentionableSelectBuilder
import dev.kord.rest.builder.component.RoleSelectBuilder
import dev.kord.rest.builder.component.SelectMenuBuilder
import dev.kord.rest.builder.component.SelectOptionBuilder
import dev.kord.rest.builder.component.StringSelectBuilder
import dev.kord.rest.builder.component.UserSelectBuilder

public fun AkumaComponent.StringSelect.Option.toKord(): SelectOptionBuilder {
    return SelectOptionBuilder(label, value).apply {
        description = this@toKord.description
        emoji = this@toKord.emoji?.toKordEmoji()
        default = isDefault
    }
}

public fun AkumaComponent.StringSelect.toKord(): StringSelectBuilder {
    return StringSelectBuilder(customId).apply {
        placeholder = this@toKord.placeholder
        allowedValues = minValues..maxValues
        options = this@toKord.options.map { it.toKord() }.toMutableList()
    }
}

// Kord has one builder per Discord select-menu component type -- unlike JDA's `EntitySelectMenu`,
// which accepts an arbitrary `Set<SelectTarget>`, there's no single Kord builder that combines
// targets. `{USER, ROLE}` is the one combination Discord itself represents as a single component
// (the mentionable select), matching the JDA adapter's own MENTIONABLE-expansion behaviour; any
// other multi-type combination has no wire representation to map onto, so it's rejected here
// instead of silently dropping types.
public fun AkumaComponent.EntitySelect.toKord(): SelectMenuBuilder {
    val builder: SelectMenuBuilder = when (entityTypes) {
        setOf(AkumaEntitySelectType.USER) -> UserSelectBuilder(customId)
        setOf(AkumaEntitySelectType.ROLE) -> RoleSelectBuilder(customId)
        setOf(AkumaEntitySelectType.CHANNEL) -> ChannelSelectBuilder(customId).apply {
            if (this@toKord.channelTypes.isNotEmpty()) {
                this.channelTypes = this@toKord.channelTypes.toKord().toMutableList()
            }
        }

        setOf(AkumaEntitySelectType.MENTIONABLE), setOf(AkumaEntitySelectType.USER, AkumaEntitySelectType.ROLE) ->
            MentionableSelectBuilder(customId)

        else -> error("Entity select '$customId' has no Discord component type for entityTypes=$entityTypes")
    }

    builder.placeholder = placeholder
    builder.allowedValues = minValues..maxValues
    return builder
}
