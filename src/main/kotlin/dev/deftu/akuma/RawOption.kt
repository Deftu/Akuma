package dev.deftu.akuma

import kotlin.reflect.KClass

// A framework-native option value, wrapped just enough to deserialize the primitive Discord
// option kinds (plus [AkumaOptionEnum] consumer enums, resolved from the option's string value)
// without every adapter reinventing that logic. Entity types (users, roles, channels,
// attachments, mentionables) aren't representable here -- they're JDA/Kord/etc. classes, not
// something core can model -- so adapters handle those themselves in their
// `CommandContext.required/optional/nullable(name, type: KClass<T>)` implementation, falling
// back to [deserializePrimitive] for everything else.
public interface RawOption {
    public val asString: String
    public val asInt: Int
    public val asLong: Long
    public val asDouble: Double
    public val asBoolean: Boolean

    // The adapter's own option-mapping type (e.g. JDA's `OptionMapping`), for adapter-specific
    // deserializers to downcast.
    public val native: Any
}

// Returns null, rather than throwing, when `type` isn't one of the primitives (or an
// [AkumaOptionEnum]) handled here -- that's the adapter's cue to fall back to its own
// entity-type handling before giving up.
@Suppress("UNCHECKED_CAST")
public fun <T : Any> RawOption.deserializePrimitive(type: KClass<T>): T? {
    val value: Any? = when (type) {
        String::class -> asString
        Int::class -> asInt
        Long::class -> asLong
        Double::class -> asDouble
        Boolean::class -> asBoolean
        else -> if (AkumaOptionEnum::class.java.isAssignableFrom(type.java)) deserializeEnum(type) else null
    }

    return value as T?
}

// Matches the option's string value against each constant's declared `optionValue` -- an enum
// only resolves this way if it opts in by implementing [AkumaOptionEnum]; there's no fallback to
// the constant's own name, since that guess doesn't hold once the Discord-facing value diverges
// from Kotlin naming conventions (casing, spaces, etc).
private fun <T : Any> RawOption.deserializeEnum(type: KClass<T>): T? {
    val constants = type.java.enumConstants ?: return null
    return constants.firstOrNull { (it as AkumaOptionEnum).optionValue == asString }
}
