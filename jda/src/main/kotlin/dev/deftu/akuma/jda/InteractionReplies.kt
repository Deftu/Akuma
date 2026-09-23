package dev.deftu.akuma.jda

import dev.deftu.akuma.ComponentBuilder
import dev.deftu.akuma.LayoutComponentBuilder
import dev.deftu.akuma.ModalDefinition
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback

// Shared reply/followUp/showModal plumbing for every Jda*Context -- SlashCommandInteractionEvent,
// ButtonInteractionEvent, StringSelectInteractionEvent, EntitySelectInteractionEvent and
// ModalInteractionEvent all implement `IReplyCallback`, so this stays adapter-internal instead of
// being duplicated per context type.
internal suspend fun <T : IReplyCallback> sendReply(
    event: T,
    content: String,
    components: Array<out ComponentBuilder>,
    ephemeral: Boolean,
) {
    val action = event.reply(content).setEphemeral(ephemeral)
    val rows = components.toActionRows()
    if (rows.isNotEmpty()) {
        action.setComponents(rows)
    }

    action.submit().await()
}

// Only patches components when some were passed -- an empty vararg leaves whatever's already
// on the original response alone, matching JDA's own partial-edit semantics.
internal suspend fun <T : IReplyCallback> editOriginalReply(
    event: T,
    content: String,
    components: Array<out ComponentBuilder>,
) {
    val action = event.hook.editOriginal(content)
    val rows = components.toActionRows()
    if (rows.isNotEmpty()) {
        action.setComponents(rows)
    }

    action.submit().await()
}

internal suspend fun <T : IReplyCallback> sendFollowUp(
    event: T,
    content: String,
    components: Array<out ComponentBuilder>,
    ephemeral: Boolean,
) {
    val hook = event.hook
    hook.setEphemeral(ephemeral)

    val action = hook.sendMessage(content)
    val rows = components.toActionRows()
    if (rows.isNotEmpty()) {
        action.setComponents(rows)
    }

    action.submit().await()
}

internal suspend fun <T : IModalCallback> openModal(event: T, modal: ModalDefinition) {
    event.replyModal(modal.toJda()).submit().await()
}

// Components V2 replaces content/embeds outright, so these don't take a `content` string --
// `useComponentsV2(true)` is explicit rather than relying on JDA's global default flag.
internal suspend fun <T : IReplyCallback> sendComponentsReply(
    event: T,
    components: Array<out LayoutComponentBuilder>,
    ephemeral: Boolean,
) {
    event.deferReply()
        .setEphemeral(ephemeral)
        .useComponentsV2(true)
        .setComponents(components.toTopLevelComponents())
        .submit()
        .await()
}

internal suspend fun <T : IReplyCallback> editOriginalComponentsReply(
    event: T,
    components: Array<out LayoutComponentBuilder>,
) {
    event.hook.editOriginalComponents(components.toTopLevelComponents())
        .useComponentsV2(true)
        .submit()
        .await()
}

internal suspend fun <T : IReplyCallback> sendComponentsFollowUp(
    event: T,
    components: Array<out LayoutComponentBuilder>,
    ephemeral: Boolean,
) {
    val hook = event.hook
    hook.setEphemeral(ephemeral)
    hook.sendMessageComponents(components.toTopLevelComponents())
        .useComponentsV2(true)
        .submit()
        .await()
}
