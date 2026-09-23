package dev.deftu.akuma.jda

import dev.deftu.akuma.CommandContext
import dev.deftu.akuma.ComponentBuilder
import dev.deftu.akuma.ComponentRegistry
import dev.deftu.akuma.LayoutComponentBuilder
import dev.deftu.akuma.ModalBuilder
import dev.deftu.akuma.RawOption
import dev.deftu.akuma.deserializePrimitive
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import java.util.Optional
import kotlin.reflect.KClass

public class JdaCommandContext(
    public val event: SlashCommandInteractionEvent,
    override val componentRegistry: ComponentRegistry,
) : CommandContext {

    override fun <T : Any> nullable(name: String, type: KClass<T>): T? {
        val option = event.getOption(name) ?: return null
        return deserialize(JdaRawOption(option), type)
    }

    override fun <T : Any> optional(name: String, type: KClass<T>): Optional<T> {
        val option = event.getOption(name) ?: return Optional.empty()
        return Optional.of(deserialize(JdaRawOption(option), type))
    }

    override fun <T : Any> required(name: String, type: KClass<T>): T {
        val option = event.getOption(name) ?: throw IllegalArgumentException("Option $name is required")
        return deserialize(JdaRawOption(option), type)
    }

    // A slash command has no context-menu target -- only USER/MESSAGE commands
    // (`JdaUserCommandContext`/`JdaMessageCommandContext`) provide one.
    override fun <T : Any> target(type: KClass<T>): T {
        throw UnsupportedOperationException("A slash command has no context-menu target")
    }

    override fun <T> nullable(name: String, deserializer: (RawOption) -> T): T? {
        val option = event.getOption(name) ?: return null
        return deserializer(JdaRawOption(option))
    }

    override fun <T> optional(name: String, deserializer: (RawOption) -> T): Optional<T & Any> {
        val option = event.getOption(name) ?: return Optional.empty()
        return Optional.ofNullable(deserializer(JdaRawOption(option)))
    }

    override fun <T> required(name: String, deserializer: (RawOption) -> T): T {
        val option = event.getOption(name) ?: throw IllegalArgumentException("Option $name is required")
        return deserializer(JdaRawOption(option))
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

@Suppress("UNCHECKED_CAST")
private fun <T : Any> deserialize(option: JdaRawOption, type: KClass<T>): T {
    val value: Any? = when (type) {
        User::class -> option.asJdaUser
        GuildChannel::class -> option.asJdaChannel
        Role::class -> option.asJdaRole
        Message.Attachment::class -> option.asJdaAttachment
        IMentionable::class -> option.asJdaMentionable
        else -> option.deserializePrimitive(type)
    }

    return value as? T ?: error("No default deserializer for type $type; pass an explicit deserializer")
}

public class JdaRawOption(public val mapping: OptionMapping) : RawOption {
    override val asString: String get() = mapping.asString
    override val asInt: Int get() = mapping.asInt
    override val asLong: Long get() = mapping.asLong
    override val asDouble: Double get() = mapping.asDouble
    override val asBoolean: Boolean get() = mapping.asBoolean
    override val native: Any get() = mapping
}
