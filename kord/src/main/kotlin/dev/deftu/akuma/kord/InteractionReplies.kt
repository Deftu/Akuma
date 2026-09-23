package dev.deftu.akuma.kord

import dev.deftu.akuma.ComponentBuilder
import dev.deftu.akuma.LayoutComponentBuilder
import dev.deftu.akuma.ModalDefinition
import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import dev.kord.core.behavior.interaction.ActionInteractionBehavior
import dev.kord.core.behavior.interaction.ModalParentInteractionBehavior

// Every `Interaction` carries its own applicationId/token for its whole lifetime, so the REST
// service's interaction endpoints (`createInteractionResponse`/`modifyInteractionResponse`/
// `createFollowupMessage`) can always be called directly -- the Kord equivalent of JDA's
// `event.hook`, which likewise doesn't require having captured an earlier reply's return value.
// This goes through `kord.rest.interaction` rather than the higher-level `ActionInteractionBehavior`
// convenience extensions (`respondPublic`/`editOriginalResponse`/...) because several of those --
// notably anything that edits the original response without already knowing whether it was public
// or ephemeral, which this context never tracks -- are Kotlin-internal to kord-core.

internal suspend fun ActionInteractionBehavior.sendReply(
    content: String,
    components: Array<out ComponentBuilder>,
    ephemeral: Boolean,
) {
    val rows = components.toActionRows()
    kord.rest.interaction.createInteractionResponse(applicationId, token, ephemeral) {
        this.content = content
        if (rows.isNotEmpty()) this.components = rows.toMutableList()
    }
}

internal suspend fun ActionInteractionBehavior.editOriginalReply(
    content: String,
    components: Array<out ComponentBuilder>,
) {
    val rows = components.toActionRows()
    kord.rest.interaction.modifyInteractionResponse(applicationId, token) {
        this.content = content
        if (rows.isNotEmpty()) this.components = rows.toMutableList()
    }
}

internal suspend fun ActionInteractionBehavior.sendFollowUp(
    content: String,
    components: Array<out ComponentBuilder>,
    ephemeral: Boolean,
) {
    val rows = components.toActionRows()
    kord.rest.interaction.createFollowupMessage(applicationId, token, ephemeral) {
        this.content = content
        if (rows.isNotEmpty()) this.components = rows.toMutableList()
    }
}

internal suspend fun ModalParentInteractionBehavior.openModal(modal: ModalDefinition) {
    kord.rest.interaction.createModalInteractionResponse(applicationId, token, modal.customId, modal.title) {
        modal.applyTo(this)
    }
}

// Components V2 replaces content/embeds outright, so these don't take a `content` string --
// `MessageFlag.IsComponentsV2` is set explicitly rather than relying on any auto-detection, same
// as the JDA adapter's explicit `useComponentsV2(true)`.
internal suspend fun ActionInteractionBehavior.sendComponentsReply(
    components: Array<out LayoutComponentBuilder>,
    ephemeral: Boolean,
) {
    val topLevel = components.toKordTopLevelComponents()
    kord.rest.interaction.createInteractionResponse(applicationId, token, ephemeral) {
        flags = MessageFlags(MessageFlag.IsComponentsV2)
        this.components = topLevel.toMutableList()
    }
}

internal suspend fun ActionInteractionBehavior.editOriginalComponentsReply(components: Array<out LayoutComponentBuilder>) {
    val topLevel = components.toKordTopLevelComponents()
    kord.rest.interaction.modifyInteractionResponse(applicationId, token) {
        flags = MessageFlags(MessageFlag.IsComponentsV2)
        this.components = topLevel.toMutableList()
    }
}

internal suspend fun ActionInteractionBehavior.sendComponentsFollowUp(
    components: Array<out LayoutComponentBuilder>,
    ephemeral: Boolean,
) {
    val topLevel = components.toKordTopLevelComponents()
    kord.rest.interaction.createFollowupMessage(applicationId, token, ephemeral) {
        flags = MessageFlags(MessageFlag.IsComponentsV2)
        this.components = topLevel.toMutableList()
    }
}
