package dev.deftu.akuma.kord

import dev.deftu.akuma.CommandContext
import dev.deftu.akuma.ComponentBuilder
import dev.deftu.akuma.ComponentRegistry
import dev.deftu.akuma.LayoutComponentBuilder
import dev.deftu.akuma.ModalBuilder
import dev.deftu.akuma.RawOption
import dev.deftu.akuma.deserializePrimitive
import dev.kord.core.entity.Attachment
import dev.kord.core.entity.Entity
import dev.kord.core.entity.Role
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.ResolvedChannel
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.core.entity.interaction.InteractionCommand
import java.util.Optional
import kotlin.reflect.KClass

public class KordCommandContext(
    public val interaction: ChatInputCommandInteraction,
    override val componentRegistry: ComponentRegistry,
) : CommandContext {

    private val command: InteractionCommand get() = interaction.command

    override fun <T : Any> nullable(name: String, type: KClass<T>): T? {
        if (name !in command.options) return null
        return deserialize(name, command, type)
    }

    override fun <T : Any> optional(name: String, type: KClass<T>): Optional<T> {
        if (name !in command.options) return Optional.empty()
        return Optional.of(deserialize(name, command, type))
    }

    override fun <T : Any> required(name: String, type: KClass<T>): T {
        if (name !in command.options) throw IllegalArgumentException("Option $name is required")
        return deserialize(name, command, type)
    }

    // A slash command has no context-menu target -- only USER/MESSAGE commands
    // (`KordUserCommandContext`/`KordMessageCommandContext`) provide one.
    override fun <T : Any> target(type: KClass<T>): T {
        throw UnsupportedOperationException("A slash command has no context-menu target")
    }

    override fun <T> nullable(name: String, deserializer: (RawOption) -> T): T? {
        if (name !in command.options) return null
        return deserializer(KordRawOption(name, command))
    }

    override fun <T> optional(name: String, deserializer: (RawOption) -> T): Optional<T & Any> {
        if (name !in command.options) return Optional.empty()
        return Optional.ofNullable(deserializer(KordRawOption(name, command)))
    }

    override fun <T> required(name: String, deserializer: (RawOption) -> T): T {
        if (name !in command.options) throw IllegalArgumentException("Option $name is required")
        return deserializer(KordRawOption(name, command))
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

@Suppress("UNCHECKED_CAST")
internal fun <T : Any> deserialize(name: String, command: InteractionCommand, type: KClass<T>): T {
    val value: Any? = when (type) {
        User::class -> command.users[name]
        ResolvedChannel::class -> command.channels[name]
        Role::class -> command.roles[name]
        Attachment::class -> command.attachments[name]
        Entity::class -> command.mentionables[name]
        else -> KordRawOption(name, command).deserializePrimitive(type)
    }

    return value as? T ?: error("No default deserializer for type $type; pass an explicit deserializer")
}

public class KordRawOption(public val name: String, public val command: InteractionCommand) : RawOption {
    private val value: Any? get() = command.options.getValue(name).value

    override val asString: String get() = value.toString()
    override val asInt: Int get() = (value as Number).toInt()
    override val asLong: Long get() = (value as Number).toLong()
    override val asDouble: Double get() = (value as Number).toDouble()
    override val asBoolean: Boolean get() = value as Boolean
    override val native: Any get() = command.options.getValue(name)
}
