package dev.deftu.akuma.kord

import dev.deftu.akuma.ButtonContext
import dev.deftu.akuma.ComponentBuilder
import dev.deftu.akuma.ComponentRegistry
import dev.deftu.akuma.LayoutComponentBuilder
import dev.deftu.akuma.ModalBuilder
import dev.deftu.akuma.RawOption
import dev.kord.core.entity.interaction.ButtonInteraction
import java.util.Optional
import kotlin.reflect.KClass

// Buttons carry no options -- `required`/`optional`/`nullable` are only here to satisfy
// `InteractionContext`'s shared shape, and behave as if the option were never provided.
public class KordButtonContext(
    public val interaction: ButtonInteraction,
    override val componentRegistry: ComponentRegistry,
) : ButtonContext {

    override val customId: String get() = interaction.componentId

    override fun <T : Any> nullable(name: String, type: KClass<T>): T? = null
    override fun <T : Any> optional(name: String, type: KClass<T>): Optional<T> = Optional.empty()
    override fun <T : Any> required(name: String, type: KClass<T>): T {
        throw IllegalArgumentException("Option $name is required")
    }

    override fun <T> nullable(name: String, deserializer: (RawOption) -> T): T? = null
    override fun <T> optional(name: String, deserializer: (RawOption) -> T): Optional<T & Any> = Optional.empty()
    override fun <T> required(name: String, deserializer: (RawOption) -> T): T {
        throw IllegalArgumentException("Option $name is required")
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

    override suspend fun showModal(modal: ModalBuilder) {
        interaction.openModal(modal.build())
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
