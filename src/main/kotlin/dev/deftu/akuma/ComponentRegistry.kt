package dev.deftu.akuma

import kotlinx.coroutines.CompletableDeferred

// Components and modals aren't registered with Discord like commands -- they're routed by
// custom ID on callback only. This holds the one-shot pending interactions `awaitComponent`/
// `awaitModal` register, keyed by the custom ID(s) of what was sent.
public class ComponentRegistry {

    private val pending = mutableMapOf<String, CompletableDeferred<InteractionContext>>()

    public fun register(customId: String, deferred: CompletableDeferred<InteractionContext>) {
        pending[customId] = deferred
    }

    public fun unregister(customId: String) {
        pending.remove(customId)
    }

    // Returns false when nothing was pending for this ID -- e.g. a component with no matching
    // `await*` call, or one that already resolved/timed out.
    public fun dispatch(customId: String, context: InteractionContext): Boolean {
        val deferred = pending.remove(customId) ?: return false
        deferred.complete(context)
        return true
    }

}
