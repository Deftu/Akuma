package dev.deftu.akuma.kord

import dev.deftu.akuma.ComponentBuilder
import dev.deftu.akuma.ComponentRegistry
import dev.deftu.akuma.LayoutComponentBuilder
import dev.deftu.akuma.ModalBuilder
import dev.deftu.akuma.ModalContext
import dev.deftu.akuma.RawOption
import dev.deftu.akuma.deserializePrimitive
import dev.kord.core.entity.interaction.ModalSubmitInteraction
import java.util.Optional
import kotlin.reflect.KClass

public class KordModalContext(
    public val interaction: ModalSubmitInteraction,
    override val componentRegistry: ComponentRegistry,
) : ModalContext {

    override val customId: String get() = interaction.modalId

    override fun <T : Any> nullable(name: String, type: KClass<T>): T? {
        val value = interaction.textInputs[name]?.value ?: return null
        return KordModalMapping(value).deserializePrimitive(type)
            ?: error("No default deserializer for type $type; pass an explicit deserializer")
    }

    override fun <T : Any> optional(name: String, type: KClass<T>): Optional<T> {
        return Optional.ofNullable(nullable(name, type))
    }

    override fun <T : Any> required(name: String, type: KClass<T>): T {
        return nullable(name, type) ?: throw IllegalArgumentException("Option $name is required")
    }

    override fun <T> nullable(name: String, deserializer: (RawOption) -> T): T? {
        val value = interaction.textInputs[name]?.value ?: return null
        return deserializer(KordModalMapping(value))
    }

    override fun <T> optional(name: String, deserializer: (RawOption) -> T): Optional<T & Any> {
        val value = interaction.textInputs[name]?.value ?: return Optional.empty()
        return Optional.ofNullable(deserializer(KordModalMapping(value)))
    }

    override fun <T> required(name: String, deserializer: (RawOption) -> T): T {
        val value = interaction.textInputs[name]?.value ?: throw IllegalArgumentException("Option $name is required")
        return deserializer(KordModalMapping(value))
    }

    override suspend fun reply(content: String, vararg components: ComponentBuilder, ephemeral: Boolean) {
        interaction.sendReply(content, components, ephemeral)
    }

    override suspend fun editReply(content: String, vararg components: ComponentBuilder) {
        interaction.editOriginalReply(content, components)
    }

    override suspend fun followUp(content: String, vararg components: ComponentBuilder, ephemeral: Boolean) {
        interaction.sendFollowUp(content, components, ephemeral)
    }

    // Discord has no "modal from a modal" flow -- `ModalSubmitInteraction` doesn't implement
    // `ModalParentInteractionBehavior` in Kord, so there's nothing to delegate to here. Same
    // deviation as the JDA adapter's `JdaModalContext.showModal`.
    override suspend fun showModal(modal: ModalBuilder) {
        throw UnsupportedOperationException("A modal submission can't itself open another modal")
    }

    override suspend fun replyComponents(vararg components: LayoutComponentBuilder, ephemeral: Boolean) {
        interaction.sendComponentsReply(components, ephemeral)
    }

    override suspend fun editReplyComponents(vararg components: LayoutComponentBuilder) {
        interaction.editOriginalComponentsReply(components)
    }

    override suspend fun followUpComponents(vararg components: LayoutComponentBuilder, ephemeral: Boolean) {
        interaction.sendComponentsFollowUp(components, ephemeral)
    }

}

public class KordModalMapping(private val raw: String) : RawOption {
    override val asString: String get() = raw
    override val asInt: Int get() = raw.toInt()
    override val asLong: Long get() = raw.toLong()
    override val asDouble: Double get() = raw.toDouble()
    override val asBoolean: Boolean get() = raw.toBoolean()
    override val native: Any get() = raw
}
