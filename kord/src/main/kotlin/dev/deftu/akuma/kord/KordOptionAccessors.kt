package dev.deftu.akuma.kord

import dev.deftu.akuma.RawOption
import dev.kord.core.entity.Attachment
import dev.kord.core.entity.Entity
import dev.kord.core.entity.Role
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.ResolvedChannel

// Entity-typed option accessors, for the explicit-deserializer escape hatch, e.g.
// `required("target") { it.asKordUser }`. `required<User>("target")` (no lambda) already works
// too -- `KordCommandContext` deserializes these by type directly -- these exist for callers who
// want the explicit lambda form, or who are working with a plain `RawOption`.
public val RawOption.asKordUser: User get() = (this as KordRawOption).run { command.users.getValue(name) }
public val RawOption.asKordChannel: ResolvedChannel get() = (this as KordRawOption).run { command.channels.getValue(name) }
public val RawOption.asKordRole: Role get() = (this as KordRawOption).run { command.roles.getValue(name) }
public val RawOption.asKordAttachment: Attachment get() = (this as KordRawOption).run { command.attachments.getValue(name) }
public val RawOption.asKordMentionable: Entity get() = (this as KordRawOption).run { command.mentionables.getValue(name) }
