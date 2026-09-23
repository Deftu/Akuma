package dev.deftu.akuma.kord

import dev.deftu.akuma.AkumaComponent
import dev.deftu.akuma.ComponentBuilder
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.component.ActionRowComponentBuilder
import dev.kord.rest.builder.component.ButtonBuilder
import dev.kord.rest.builder.component.SelectMenuBuilder

internal fun AkumaComponent.toKordChild(): ActionRowComponentBuilder {
    return when (this) {
        is AkumaComponent.Button -> toKord()
        is AkumaComponent.StringSelect -> toKord()
        is AkumaComponent.EntitySelect -> toKord()
    }
}

// Mirrors the JDA adapter's row packing (`ActionRow.partitionOf`) since Kord has no equivalent
// built in: buttons pack up to five per row, each select menu gets its own row.
internal fun Array<out ComponentBuilder>.toActionRows(): List<ActionRowBuilder> {
    if (isEmpty()) return emptyList()

    val rows = mutableListOf<ActionRowBuilder>()
    var currentButtonRow: ActionRowBuilder? = null

    for (component in map { it.build().toKordChild() }) {
        when (component) {
            is ButtonBuilder -> {
                var row = currentButtonRow
                if (row == null || row.components.size >= 5) {
                    row = ActionRowBuilder()
                    rows.add(row)
                    currentButtonRow = row
                }

                row.components.add(component)
            }

            is SelectMenuBuilder -> {
                rows.add(ActionRowBuilder().apply { components.add(component) })
                currentButtonRow = null
            }

            else -> error("Unsupported action row component: $component")
        }
    }

    return rows
}
