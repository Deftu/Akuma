package dev.deftu.akuma.jda

import dev.deftu.akuma.ComponentBuilder
import dev.deftu.akuma.ComponentRegistry
import dev.deftu.akuma.LayoutComponentBuilder
import dev.deftu.akuma.ModalBuilder
import dev.deftu.akuma.ModalContext
import dev.deftu.akuma.RawOption
import dev.deftu.akuma.deserializePrimitive
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import java.util.Optional
import kotlin.reflect.KClass

public class JdaModalContext(
    public val event: ModalInteractionEvent,
    override val componentRegistry: ComponentRegistry,
) : ModalContext {

    override val customId: String get() = event.modalId

    override fun <T : Any> nullable(name: String, type: KClass<T>): T? {
        val value = event.getValue(name) ?: return null
        return JdaModalMapping(value).deserializePrimitive(type)
            ?: error("No default deserializer for type $type; pass an explicit deserializer")
    }

    override fun <T : Any> optional(name: String, type: KClass<T>): Optional<T> {
        return Optional.ofNullable(nullable(name, type))
    }

    override fun <T : Any> required(name: String, type: KClass<T>): T {
        return nullable(name, type) ?: throw IllegalArgumentException("Option $name is required")
    }

    override fun <T> nullable(name: String, deserializer: (RawOption) -> T): T? {
        val value = event.getValue(name) ?: return null
        return deserializer(JdaModalMapping(value))
    }

    override fun <T> optional(name: String, deserializer: (RawOption) -> T): Optional<T & Any> {
        val value = event.getValue(name) ?: return Optional.empty()
        return Optional.ofNullable(deserializer(JdaModalMapping(value)))
    }

    override fun <T> required(name: String, deserializer: (RawOption) -> T): T {
        val value = event.getValue(name) ?: throw IllegalArgumentException("Option $name is required")
        return deserializer(JdaModalMapping(value))
    }

    override suspend fun reply(content: String, vararg components: ComponentBuilder, ephemeral: Boolean) {
        sendReply(event, content, components, ephemeral)
    }

    override suspend fun editReply(content: String, vararg components: ComponentBuilder) {
        editOriginalReply(event, content, components)
    }

    override suspend fun followUp(content: String, vararg components: ComponentBuilder, ephemeral: Boolean) {
        sendFollowUp(event, content, components, ephemeral)
    }

    // Discord has no "modal from a modal" flow -- `ModalInteraction` doesn't implement
    // `IModalCallback` in JDA, so there's nothing to delegate to here.
    override suspend fun showModal(modal: ModalBuilder) {
        throw UnsupportedOperationException("A modal submission can't itself open another modal")
    }

    override suspend fun replyComponents(vararg components: LayoutComponentBuilder, ephemeral: Boolean) {
        sendComponentsReply(event, components, ephemeral)
    }

    override suspend fun editReplyComponents(vararg components: LayoutComponentBuilder) {
        editOriginalComponentsReply(event, components)
    }

    override suspend fun followUpComponents(vararg components: LayoutComponentBuilder, ephemeral: Boolean) {
        sendComponentsFollowUp(event, components, ephemeral)
    }

}
