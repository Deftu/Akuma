package dev.deftu.akuma.jda

import dev.deftu.akuma.RawOption
import net.dv8tion.jda.api.interactions.modals.ModalMapping

public class JdaModalMapping(public val mapping: ModalMapping) : RawOption {
    override val asString: String get() = mapping.asString
    override val asInt: Int get() = mapping.asString.toInt()
    override val asLong: Long get() = mapping.asString.toLong()
    override val asDouble: Double get() = mapping.asString.toDouble()
    override val asBoolean: Boolean get() = mapping.asBoolean
    override val native: Any get() = mapping
}
