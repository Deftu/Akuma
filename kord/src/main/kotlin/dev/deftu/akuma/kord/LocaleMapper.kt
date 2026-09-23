package dev.deftu.akuma.kord

import dev.deftu.akuma.AkumaLocale
import dev.kord.common.Locale as KordLocale

public fun AkumaLocale.toKord(): KordLocale {
    return KordLocale.fromString(code)
}

public fun KordLocale.toAkuma(): AkumaLocale {
    return AkumaLocale.from(toString())
}

public fun Map<AkumaLocale, String>.toKordLocalizations(): MutableMap<KordLocale, String> {
    return mapKeys { (locale, _) -> locale.toKord() }.toMutableMap()
}
