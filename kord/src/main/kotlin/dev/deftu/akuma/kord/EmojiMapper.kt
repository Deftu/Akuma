package dev.deftu.akuma.kord

import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalBoolean

// Kord has no built-in "parse a formatted emoji string" helper (unlike JDA's `Emoji.fromFormatted`),
// so `AkumaComponent.Button.emoji`/`AkumaComponent.StringSelect.Option.emoji` (both a freeform
// formatted string, matching the convention Phase 3/4 established for the JDA adapter) is parsed
// here: `<a:name:id>`/`<:name:id>` for a custom emoji, anything else treated as a literal Unicode
// emoji.
private val CUSTOM_EMOJI_FORMAT = Regex("^<(a)?:(\\w+):(\\d+)>$")

public fun String.toKordEmoji(): DiscordPartialEmoji {
    val match = CUSTOM_EMOJI_FORMAT.matchEntire(this)
        ?: return DiscordPartialEmoji(id = null, name = this, animated = OptionalBoolean.Missing)

    val (animated, name, id) = match.destructured
    return DiscordPartialEmoji(
        id = Snowflake(id),
        name = name,
        animated = OptionalBoolean.Value(animated == "a"),
    )
}
