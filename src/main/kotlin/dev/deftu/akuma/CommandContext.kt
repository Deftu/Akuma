package dev.deftu.akuma

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import java.util.Optional

public class CommandContext(public val event: SlashCommandInteractionEvent) {

    public companion object {

        @Suppress("IMPLICIT_CAST_TO_ANY")
        public inline fun <reified T> defaultDeserializer(option: OptionMapping): T {
            return when (T::class) {
                String::class -> option.asString
                Int::class -> option.asInt
                Long::class -> option.asLong
                Double::class -> option.asDouble
                Boolean::class -> option.asBoolean
                User::class -> option.asUser
                GuildChannel::class -> option.asChannel
                Role::class -> option.asRole
                Message.Attachment::class -> option.asAttachment
                else -> error("Unsupported option type: ${option.type} for type ${T::class}")
            } as T
        }

    }

    public inline fun <reified T> nullable(
        name: String,
        deserializer: (OptionMapping) -> T = ::defaultDeserializer
    ): T? {
        val option = event.getOption(name) ?: return null
        return deserializer(option)
    }

    public inline fun <reified T> optional(
        name: String,
        deserializer: (OptionMapping) -> T = ::defaultDeserializer
    ): Optional<T & Any> {
        val option = event.getOption(name) ?: return Optional.empty()
        return Optional.ofNullable(deserializer(option))
    }

    @Throws(IllegalArgumentException::class)
    public inline fun <reified T> required(
        name: String,
        deserializer: (OptionMapping) -> T = ::defaultDeserializer
    ): T {
        val option = event.getOption(name) ?: throw IllegalArgumentException("Option $name is required")
        return deserializer(option)
    }

}
