package dev.deftu.akuma

// Implemented by a consumer's own enum to make it resolvable through
// `required`/`optional`/`nullable` with zero config -- `optionValue` is what's compared against
// the raw Discord option value (typically the same string passed to `choice(name, value)` when
// building the option), not the enum constant's own name.
public interface AkumaOptionEnum {
    public val optionValue: String
}

// Discord's cap on both static choices per option and suggestions per autocomplete response.
// Internal, not private -- referenced from the public inline `enumeration`, which needs it visible
// at the call site's compile time.
@PublishedApi
internal const val MAX_DISCORD_CHOICES: Int = 25

// Wires T's constants into the option at registration time, picking the mechanism Discord allows
// for the given size: static choices (name = constant's own name, value = `optionValue`) under
// the 25-entry cap, since that's zero-latency and needs no handler; otherwise a generated
// autocomplete that filters constants by `optionValue` against what's typed so far. The two are
// mutually exclusive by construction -- Discord rejects an option carrying both -- so callers
// never have to pick one themselves.
public inline fun <reified T> OptionBuilder.StringOption.enumeration(): OptionBuilder.StringOption
    where T : Enum<T>, T : AkumaOptionEnum = apply {
    val constants = enumValues<T>()
    if (constants.size < MAX_DISCORD_CHOICES) {
        for (constant in constants) {
            choice(constant.name, constant.optionValue)
        }
    } else {
        autocomplete { context ->
            val matches = constants
                .filter { it.optionValue.contains(context.focusedValue, ignoreCase = true) }
                .take(MAX_DISCORD_CHOICES)
                .map { AkumaChoice(it.name, it.optionValue) }

            context.replyChoices(matches)
        }
    }
}
