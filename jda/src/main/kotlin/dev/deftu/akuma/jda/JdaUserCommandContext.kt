package dev.deftu.akuma.jda

import dev.deftu.akuma.CommandContext
import dev.deftu.akuma.ComponentBuilder
import dev.deftu.akuma.ComponentRegistry
import dev.deftu.akuma.LayoutComponentBuilder
import dev.deftu.akuma.ModalBuilder
import dev.deftu.akuma.RawOption
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import java.util.Optional
import kotlin.reflect.KClass

public class JdaUserCommandContext(
    public val event: UserContextInteractionEvent,
    override val componentRegistry: ComponentRegistry,
) : CommandContext {

    // A user-context command has no named options -- only the target user below.
    override fun <T : Any> nullable(name: String, type: KClass<T>): T? {
        throw UnsupportedOperationException("A user-context command has no named options")
    }

    override fun <T : Any> optional(name: String, type: KClass<T>): Optional<T> {
        throw UnsupportedOperationException("A user-context command has no named options")
    }

    override fun <T : Any> required(name: String, type: KClass<T>): T {
        throw UnsupportedOperationException("A user-context command has no named options")
    }

    override fun <T> nullable(name: String, deserializer: (RawOption) -> T): T? {
        throw UnsupportedOperationException("A user-context command has no named options")
    }

    override fun <T> optional(name: String, deserializer: (RawOption) -> T): Optional<T & Any> {
        throw UnsupportedOperationException("A user-context command has no named options")
    }

    override fun <T> required(name: String, deserializer: (RawOption) -> T): T {
        throw UnsupportedOperationException("A user-context command has no named options")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> target(type: KClass<T>): T {
        return when (type) {
            User::class -> event.target as T
            else -> error("No target of type $type; user-context commands only provide User")
        }
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

    override suspend fun showModal(modal: ModalBuilder) {
        openModal(event, modal.build())
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
