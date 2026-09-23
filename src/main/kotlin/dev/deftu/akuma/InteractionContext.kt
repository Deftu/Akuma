package dev.deftu.akuma

import java.util.Optional
import kotlin.reflect.KClass

// The shared base every interaction-driven context (CommandContext, ComponentContext,
// ModalContext) implements -- same option accessors, same reply/followUp/ephemeral shape,
// regardless of which kind of Discord interaction produced the context. See the "one
// interaction surface, not four" design principle in the migration plan.
public interface InteractionContext {
    // KClass-based overloads: dispatched virtually by the adapter's own implementation, so e.g.
    // `required<User>("target")` resolves against whatever the actual runtime context is --
    // unlike an extension function, this isn't limited by the static type a DSL's `action`/
    // `handler` block types its lambda receiver against.
    public fun <T : Any> nullable(name: String, type: KClass<T>): T?
    public fun <T : Any> optional(name: String, type: KClass<T>): Optional<T>
    public fun <T : Any> required(name: String, type: KClass<T>): T

    // Escape hatch for types the adapter doesn't natively know how to deserialize.
    public fun <T> nullable(name: String, deserializer: (RawOption) -> T): T?
    public fun <T> optional(name: String, deserializer: (RawOption) -> T): Optional<T & Any>
    public fun <T> required(name: String, deserializer: (RawOption) -> T): T

    // The registry backing `awaitComponent`/`awaitModal` on this interaction's dispatcher --
    // every context an adapter builds shares the one instance the dispatcher owns.
    public val componentRegistry: ComponentRegistry

    public suspend fun reply(content: String, vararg components: ComponentBuilder, ephemeral: Boolean = false)
    public suspend fun editReply(content: String, vararg components: ComponentBuilder)
    public suspend fun followUp(content: String, vararg components: ComponentBuilder, ephemeral: Boolean = false)

    // Components V2 replaces content/embeds outright rather than sitting alongside them, so it
    // gets its own reply/editReply/followUp trio instead of an overload on the ones above.
    public suspend fun replyComponents(vararg components: LayoutComponentBuilder, ephemeral: Boolean = false)
    public suspend fun editReplyComponents(vararg components: LayoutComponentBuilder)
    public suspend fun followUpComponents(vararg components: LayoutComponentBuilder, ephemeral: Boolean = false)

    // Not every interaction can open a modal in response -- a modal submission itself can't
    // (Discord has no "modal from a modal" flow), so `ModalContext` throws here instead of
    // silently doing nothing.
    public suspend fun showModal(modal: ModalBuilder)
}

public inline fun <reified T : Any> InteractionContext.nullable(name: String): T? {
    return nullable(name, T::class)
}

public inline fun <reified T : Any> InteractionContext.optional(name: String): Optional<T> {
    return optional(name, T::class)
}

public inline fun <reified T : Any> InteractionContext.required(name: String): T {
    return required(name, T::class)
}
