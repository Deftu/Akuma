package dev.deftu.akuma

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.Optional

private class RegistryFakeInteractionContext : InteractionContext {
    override val componentRegistry: ComponentRegistry get() = error("not used in this test")
    override fun <T : Any> nullable(name: String, type: kotlin.reflect.KClass<T>): T? = error("not used in this test")
    override fun <T : Any> optional(name: String, type: kotlin.reflect.KClass<T>): Optional<T> = error("not used in this test")
    override fun <T : Any> required(name: String, type: kotlin.reflect.KClass<T>): T = error("not used in this test")
    override fun <T> nullable(name: String, deserializer: (RawOption) -> T): T? = error("not used in this test")
    override fun <T> optional(name: String, deserializer: (RawOption) -> T): Optional<T & Any> = error("not used in this test")
    override fun <T> required(name: String, deserializer: (RawOption) -> T): T = error("not used in this test")
    override suspend fun reply(content: String, vararg components: ComponentBuilder, ephemeral: Boolean) = error("not used in this test")
    override suspend fun editReply(content: String, vararg components: ComponentBuilder) = error("not used in this test")
    override suspend fun followUp(content: String, vararg components: ComponentBuilder, ephemeral: Boolean) = error("not used in this test")
    override suspend fun showModal(modal: ModalBuilder) = error("not used in this test")
    override suspend fun replyComponents(vararg components: LayoutComponentBuilder, ephemeral: Boolean) = error("not used in this test")
    override suspend fun editReplyComponents(vararg components: LayoutComponentBuilder) = error("not used in this test")
    override suspend fun followUpComponents(vararg components: LayoutComponentBuilder, ephemeral: Boolean) = error("not used in this test")
}

class ComponentRegistryTest {

    @Test
    fun `dispatch completes and removes the matching pending entry`() = runTest {
        val registry = ComponentRegistry()
        val deferred = CompletableDeferred<InteractionContext>()
        val context = RegistryFakeInteractionContext()

        registry.register("btn:confirm", deferred)

        assertTrue(registry.dispatch("btn:confirm", context))
        assertTrue(deferred.isCompleted)
        assertEquals(context, deferred.await())

        // A second dispatch for the same id finds nothing -- it was consumed above.
        assertFalse(registry.dispatch("btn:confirm", context))
    }

    @Test
    fun `dispatch for an unregistered id is a no-op`() {
        val registry = ComponentRegistry()
        assertFalse(registry.dispatch("nothing:here", RegistryFakeInteractionContext()))
    }

    @Test
    fun `unregister removes a pending entry without completing it`() {
        val registry = ComponentRegistry()
        val deferred = CompletableDeferred<InteractionContext>()

        registry.register("modal:feedback", deferred)
        registry.unregister("modal:feedback")

        assertFalse(registry.dispatch("modal:feedback", RegistryFakeInteractionContext()))
        assertFalse(deferred.isCompleted)
    }

}
