package dev.deftu.akuma.jda

import dev.deftu.akuma.RawOption
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel
import net.dv8tion.jda.api.interactions.commands.OptionMapping

// Entity-typed option accessors, for the explicit-deserializer escape hatch, e.g.
// `required("target") { it.asJdaUser }`. `required<User>("target")` (no lambda) already works
// too -- `JdaCommandContext` deserializes these by type directly -- these exist for callers who
// want the explicit lambda form, or who are working with a plain `RawOption`.
public val RawOption.asJdaUser: User get() = (native as OptionMapping).asUser
public val RawOption.asJdaChannel: GuildChannel get() = (native as OptionMapping).asChannel
public val RawOption.asJdaRole: Role get() = (native as OptionMapping).asRole
public val RawOption.asJdaAttachment: Message.Attachment get() = (native as OptionMapping).asAttachment
public val RawOption.asJdaMentionable: IMentionable get() = (native as OptionMapping).asMentionable
