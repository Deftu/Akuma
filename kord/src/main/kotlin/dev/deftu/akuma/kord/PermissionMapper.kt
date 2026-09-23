package dev.deftu.akuma.kord

import dev.deftu.akuma.AkumaDefaultPermissions
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions

// `null` means "no restriction" (Discord's default) -- mirrors `DefaultMemberPermissions.ENABLED`
// on the JDA side, which is likewise not represented as a concrete permission bitset.
// `Permissions(DiscordBitSet)` is Kotlin-internal to kord-rest, so this goes through the public
// `Permissions(Iterable<Permission>)` factory instead, resolving each set bit via
// `Permission.fromShift` (the same lookup Kord's own `ComponentType`/`MessageFlag` constants use).
public fun AkumaDefaultPermissions.toKord(): Permissions? {
    val raw = rawValue ?: return null
    val permissions = (0 until 64)
        .filter { raw and (1L shl it) != 0L }
        .map { Permission.fromShift(it) }

    return Permissions(permissions)
}
