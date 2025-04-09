package dev.deftu.akuma.textualizer

import dev.deftu.akuma.CommandBuilder
import dev.deftu.textualizer.Language
import dev.deftu.textualizer.Localization
import net.dv8tion.jda.api.interactions.DiscordLocale

public fun <T : Language<T>> CommandBuilder.applyLocalizedNameTranslations(
    localization: Localization<T>,
    languages: Collection<T>,
    converter: (T) -> DiscordLocale,
    key: String
) {
    nameLocalizations.clear()

    for (language in languages) {
        val locale = converter(language)
        val context = localization.with(language)
        nameLocalizations[locale] = context.get(key)
    }
}

public fun <T : Language<T>> CommandBuilder.applyLocalizedDescriptionTranslations(
    localization: Localization<T>,
    languages: Collection<T>,
    converter: (T) -> DiscordLocale,
    key: String
) {
    descriptionLocalizations.clear()

    for (language in languages) {
        val locale = converter(language)
        val context = localization.with(language)
        descriptionLocalizations[locale] = context.get(key)
    }
}

public fun <T : Language<T>> CommandBuilder.applyLocalizedTranslations(
    localization: Localization<T>,
    languages: Collection<T>,
    converter: (T) -> DiscordLocale,
    nameKey: String,
    descriptionKey: String
) {
    applyLocalizedNameTranslations(localization, languages, converter, nameKey)
    applyLocalizedDescriptionTranslations(localization, languages, converter, descriptionKey)
}
