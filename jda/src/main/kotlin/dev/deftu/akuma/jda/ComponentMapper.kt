package dev.deftu.akuma.jda

import dev.deftu.akuma.AkumaComponent
import dev.deftu.akuma.ComponentBuilder
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent

internal fun AkumaComponent.toJdaChild(): ActionRowChildComponent {
    return when (this) {
        is AkumaComponent.Button -> toJda()
        is AkumaComponent.StringSelect -> toJda()
        is AkumaComponent.EntitySelect -> toJda()
    }
}

// `ActionRow.partitionOf` packs buttons up to 5 per row and gives each select menu its own row.
internal fun Array<out ComponentBuilder>.toActionRows(): List<ActionRow> {
    if (isEmpty()) return emptyList()
    return ActionRow.partitionOf(map { it.build().toJdaChild() })
}
