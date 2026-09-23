package dev.deftu.akuma.kord

import dev.deftu.akuma.CommandContext
import dev.deftu.akuma.ComponentBuilder
import dev.deftu.akuma.ComponentRegistry
import dev.deftu.akuma.LayoutComponentBuilder
import dev.deftu.akuma.ModalBuilder
import dev.deftu.akuma.RawOption
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.MessageCommandInteraction
import java.util.Optional
import kotlin.reflect.KClass

public class KordMessageCommandContext(
    public val interaction: MessageCommandInteraction,
    override val componentRegistry: ComponentRegistry,
) : CommandContext {

    // A message-context command has no named options -- only the target message below.
    override fun <T : Any> nullable(name: String, type: KClass<T>): T? {
        throw UnsupportedOperationException("A message-context command has no named options")
    }

    override fun <T : Any> optional(name: String, type: KClass<T>): Optional<T> {
        throw UnsupportedOperationException("A message-context command has no named options")
    }

    override fun <T : Any> required(name: String, type: KClass<T>): T {
        throw UnsupportedOperationException("A message-context command has no named options")
    }

    override fun <T> nullable(name: String, deserializer: (RawOption) -> T): T? {
        throw UnsupportedOperationException("A message-context command has no named options")
    }

    override fun <T> optional(name: String, deserializer: (RawOption) -> T): Optional<T & Any> {
        throw UnsupportedOperationException("A message-context command has no named options")
    }

    override fun <T> required(name: String, deserializer: (RawOption) -> T): T {
        throw UnsupportedOperationException("A message-context command has no named options")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> target(type: KClass<T>): T {
        return when (type) {
            Message::class -> interaction.messages.getValue(interaction.targetId) as T
            else -> error("No target of type $type; message-context commands only provide Message")
        }
    }

    override suspend fun reply(content: String, vararg components: ComponentBuilder, ephemeral: Boolean) {
        interaction.sendReply(content, components, ephemeral)
    }

    override suspend fun editReply(content: String, vararg components: ComponentBuilder) {
        interaction.editOriginalReply(content, components)
    }

    override suspend fun followUp(content: String, vararg components: ComponentBuilder, ephemeral: Boolean) {
        interaction.sendFollowUp(content, components, ephemeral)
    }

    override suspend fun showModal(modal: ModalBuilder) {
        interaction.openModal(modal.build())
    }

    override suspend fun replyComponents(vararg components: LayoutComponentBuilder, ephemeral: Boolean) {
        interaction.sendComponentsReply(components, ephemeral)
    }

    override suspend fun editReplyComponents(vararg components: LayoutComponentBuilder) {
        interaction.editOriginalComponentsReply(components)
    }

    override suspend fun followUpComponents(vararg components: LayoutComponentBuilder, ephemeral: Boolean) {
        interaction.sendComponentsFollowUp(components, ephemeral)
    }

}
