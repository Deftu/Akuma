package dev.deftu.akuma.jda

import dev.deftu.akuma.AkumaLocale
import net.dv8tion.jda.api.interactions.DiscordLocale

public fun AkumaLocale.toJda(): DiscordLocale {
    return DiscordLocale.from(code)
}

public fun DiscordLocale.toAkuma(): AkumaLocale {
    return AkumaLocale.from(locale)
}

public fun Map<AkumaLocale, String>.toJdaLocalizations(): Map<DiscordLocale, String> {
    return mapKeys { (locale, _) -> locale.toJda() }
}
