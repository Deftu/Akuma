package dev.deftu.akuma.kord

import dev.deftu.akuma.ComponentBuilder
import dev.deftu.akuma.ComponentRegistry
import dev.deftu.akuma.EntitySelectContext
import dev.deftu.akuma.LayoutComponentBuilder
import dev.deftu.akuma.ModalBuilder
import dev.deftu.akuma.RawOption
import dev.deftu.akuma.StringSelectContext
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Entity
import dev.kord.core.entity.Role
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.ResolvedChannel
import dev.kord.core.entity.interaction.SelectMenuInteraction
import java.util.Optional
import kotlin.reflect.KClass

public class KordStringSelectContext(
    public val interaction: SelectMenuInteraction,
    override val componentRegistry: ComponentRegistry,
) : StringSelectContext {

    override val customId: String get() = interaction.componentId
    override val values: List<String> get() = interaction.values

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

public class KordEntitySelectContext(
    public val interaction: SelectMenuInteraction,
    override val componentRegistry: ComponentRegistry,
) : EntitySelectContext {

    override val customId: String get() = interaction.componentId
    override val values: List<String> get() = interaction.values

    // Kord resolves entity-select values through `resolvedObjects` (data already carried on the
    // interaction payload), same as `event.mentions` on the JDA side -- no extra REST call.
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> selected(type: KClass<T>): List<T> {
        val ids = values.map(::Snowflake)
        val resolved = interaction.resolvedObjects
        val selected: List<Any> = when (type) {
            User::class -> ids.mapNotNull { resolved?.users?.get(it) }
            Role::class -> ids.mapNotNull { resolved?.roles?.get(it) }
            ResolvedChannel::class -> ids.mapNotNull { resolved?.channels?.get(it) }
            Entity::class -> ids.mapNotNull { resolved?.users?.get(it) ?: resolved?.roles?.get(it) }
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
