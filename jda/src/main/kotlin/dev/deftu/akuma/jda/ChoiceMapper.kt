package dev.deftu.akuma.jda

import dev.deftu.akuma.AkumaChoice
import net.dv8tion.jda.api.interactions.commands.Command

public fun AkumaChoice.toJda(): Command.Choice {
    return Command.Choice(name, value)
}
