package dev.deftu.akuma

import kotlin.reflect.KClass

public sealed interface SelectContext : ComponentContext {
    public val values: List<String>
}

public interface StringSelectContext : SelectContext

public interface EntitySelectContext : SelectContext {
    // KClass-based, same virtual-dispatch trick as `InteractionContext.required(name, type)` --
    // lets the adapter downcast to its own entity types (User, Role, GuildChannel, ...).
    public fun <T : Any> selected(type: KClass<T>): List<T>
}

public inline fun <reified T : Any> EntitySelectContext.selected(): List<T> {
    return selected(T::class)
}
