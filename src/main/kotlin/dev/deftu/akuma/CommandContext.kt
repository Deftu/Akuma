package dev.deftu.akuma

import kotlin.reflect.KClass

public interface CommandContext : InteractionContext {
    // Only meaningful for USER/MESSAGE context-menu commands -- a slash command's
    // implementation throws, same as `ModalContext.showModal` does for a submission it can't
    // support. Resolved by KClass, same virtual-dispatch trick `required`/`optional`/`nullable`
    // use, so `target<User>()` reaches the right adapter override even though this interface is
    // statically all a `CommandBuilder.action { }` block ever sees.
    public fun <T : Any> target(type: KClass<T>): T
}

public inline fun <reified T : Any> CommandContext.target(): T {
    return target(T::class)
}
