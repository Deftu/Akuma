package dev.deftu.akuma.jda

import dev.deftu.akuma.ComponentBuilder
import dev.deftu.akuma.ComponentRegistry
import dev.deftu.akuma.EntitySelectContext
import dev.deftu.akuma.LayoutComponentBuilder
import dev.deftu.akuma.ModalBuilder
import dev.deftu.akuma.RawOption
import dev.deftu.akuma.StringSelectContext
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import java.util.Optional
import kotlin.reflect.KClass

public class JdaStringSelectContext(
    public val event: StringSelectInteractionEvent,
    override val componentRegistry: ComponentRegistry,
) : StringSelectContext {

    override val customId: String get() = event.componentId
    override val values: List<String> get() = event.values

    override fun <T : Any> nullable(name: String, type: KClass<T>): T? = null
    override fun <T : Any> optional(name: String, type: KClass<T>): Optional<T> = Optional.empty()
    override fun <T : Any> required(name: String, type: KClass<T>): T {
        throw IllegalArgumentException("Option $name is required")
    }

    override fun <T> nullable(name: String, deserializer: (RawOption) -> T): T? = null
    override fun <T> optional(name: String, deserializer: (RawOption) -> T): Optional<T & Any> = Optional.empty()
    override fun <T> required(name: String, deserializer: (RawOption) -> T): T {
        throw IllegalArgumentException("Option $name is required")
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

public class JdaEntitySelectContext(
    public val event: EntitySelectInteractionEvent,
    override val componentRegistry: ComponentRegistry,
) : EntitySelectContext {

    override val customId: String get() = event.componentId
    override val values: List<String> get() = event.values.map(IMentionable::getId)

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> selected(type: KClass<T>): List<T> {
        val selected: List<Any> = when (type) {
            User::class -> event.mentions.users
            Role::class -> event.mentions.roles
            GuildChannel::class -> event.mentions.channels
            IMentionable::class -> event.values
            else -> error("No default deserializer for type $type")
        }

        return selected as List<T>
    }

    override fun <T : Any> nullable(name: String, type: KClass<T>): T? = null
    override fun <T : Any> optional(name: String, type: KClass<T>): Optional<T> = Optional.empty()
    override fun <T : Any> required(name: String, type: KClass<T>): T {
        throw IllegalArgumentException("Option $name is required")
    }

    override fun <T> nullable(name: String, deserializer: (RawOption) -> T): T? = null
    override fun <T> optional(name: String, deserializer: (RawOption) -> T): Optional<T & Any> = Optional.empty()
    override fun <T> required(name: String, deserializer: (RawOption) -> T): T {
        throw IllegalArgumentException("Option $name is required")
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
