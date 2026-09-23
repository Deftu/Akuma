package dev.deftu.akuma.jda

import dev.deftu.akuma.AkumaDefaultPermissions
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions

public fun AkumaDefaultPermissions.toJda(): DefaultMemberPermissions {
    val raw = rawValue ?: return DefaultMemberPermissions.ENABLED
    return DefaultMemberPermissions.enabledFor(raw)
}
