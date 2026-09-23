package dev.deftu.akuma

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

// Sends `content` with `components` attached, then suspends until one of them is interacted
// with or `timeout` elapses. The pending registration is always deregistered on the way out --
// on resolve it's already consumed by `ComponentRegistry.dispatch`, on timeout it's removed
// here, so a late click after timeout is simply routed nowhere instead of leaking.
public suspend fun InteractionContext.awaitComponent(
    content: String,
    vararg components: ComponentBuilder,
    ephemeral: Boolean = false,
    timeout: Duration = 30.seconds,
): ComponentContext? {
    val deferred = CompletableDeferred<InteractionContext>()
    val customIds = components.map(ComponentBuilder::customId)

    customIds.forEach { componentRegistry.register(it, deferred) }
    try {
        reply(content, *components, ephemeral = ephemeral)
        return withTimeoutOrNull(timeout) { deferred.await() } as? ComponentContext
    } finally {
        customIds.forEach(componentRegistry::unregister)
    }
}

public suspend fun InteractionContext.awaitModal(
    modal: ModalBuilder,
    timeout: Duration = 5.minutes,
): ModalContext? {
    val deferred = CompletableDeferred<InteractionContext>()

    componentRegistry.register(modal.customId, deferred)
    try {
        showModal(modal)
        return withTimeoutOrNull(timeout) { deferred.await() } as? ModalContext
    } finally {
        componentRegistry.unregister(modal.customId)
    }
}
