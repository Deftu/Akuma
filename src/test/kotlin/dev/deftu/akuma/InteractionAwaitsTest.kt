package dev.deftu.akuma

import kotlinx.coroutines.async
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.Optional
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.milliseconds

private open class FakeInteractionContext(override val componentRegistry: ComponentRegistry) : InteractionContext {
    override fun <T : Any> nullable(name: String, type: KClass<T>): T? = error("not used in this test")
    override fun <T : Any> optional(name: String, type: KClass<T>): Optional<T> = error("not used in this test")
    override fun <T : Any> required(name: String, type: KClass<T>): T = error("not used in this test")
    override fun <T> nullable(name: String, deserializer: (RawOption) -> T): T? = error("not used in this test")
    override fun <T> optional(name: String, deserializer: (RawOption) -> T): Optional<T & Any> = error("not used in this test")
    override fun <T> required(name: String, deserializer: (RawOption) -> T): T = error("not used in this test")

    val repliedWith = mutableListOf<String>()
    val shownModals = mutableListOf<ModalBuilder>()

    override suspend fun reply(content: String, vararg components: ComponentBuilder, ephemeral: Boolean) {
        repliedWith.add(content)
    }

    override suspend fun editReply(content: String, vararg components: ComponentBuilder): Unit = error("not used in this test")
    override suspend fun followUp(content: String, vararg components: ComponentBuilder, ephemeral: Boolean): Unit = error("not used in this test")

    override suspend fun showModal(modal: ModalBuilder) {
        shownModals.add(modal)
    }

    override suspend fun replyComponents(vararg components: LayoutComponentBuilder, ephemeral: Boolean): Unit = error("not used in this test")
    override suspend fun editReplyComponents(vararg components: LayoutComponentBuilder): Unit = error("not used in this test")
    override suspend fun followUpComponents(vararg components: LayoutComponentBuilder, ephemeral: Boolean): Unit = error("not used in this test")
}

private class FakeButtonContext(
    registry: ComponentRegistry,
    override val customId: String,
) : FakeInteractionContext(registry), ButtonContext

private class FakeModalContext(
    registry: ComponentRegistry,
    override val customId: String,
) : FakeInteractionContext(registry), ModalContext

class InteractionAwaitsTest {

    @Test
    fun `awaitComponent sends the message and resolves once its id is dispatched`() = runTest {
        val registry = ComponentRegistry()
        val context = FakeInteractionContext(registry)
        val button = ComponentBuilder.Button("btn:confirm", "Confirm")

        val result = async { context.awaitComponent("Are you sure?", button) }
        runCurrent()

        assertEquals(listOf("Are you sure?"), context.repliedWith)

        val clicked = FakeButtonContext(registry, "btn:confirm")
        registry.dispatch("btn:confirm", clicked)

        assertEquals(clicked, result.await())
        // Resolved -- the pending entry is gone, so a stray second click finds nothing.
        assertFalse(registry.dispatch("btn:confirm", clicked))
    }

    @Test
    fun `awaitComponent returns null and deregisters on timeout`() = runTest {
        val registry = ComponentRegistry()
        val context = FakeInteractionContext(registry)
        val button = ComponentBuilder.Button("btn:confirm", "Confirm")

        val result = context.awaitComponent("Are you sure?", button, timeout = 10.milliseconds)

        assertNull(result)
        assertFalse(registry.dispatch("btn:confirm", FakeButtonContext(registry, "btn:confirm")))
    }

    @Test
    fun `awaitModal shows the modal and resolves once it's submitted`() = runTest {
        val registry = ComponentRegistry()
        val context = FakeInteractionContext(registry)
        val modal = ModalBuilder("modal:feedback", "Send feedback")

        val result = async { context.awaitModal(modal) }
        runCurrent()

        assertEquals(listOf(modal), context.shownModals)

        val submitted = FakeModalContext(registry, "modal:feedback")
        registry.dispatch("modal:feedback", submitted)

        assertEquals(submitted, result.await())
    }

    @Test
    fun `awaitModal returns null and deregisters on timeout`() = runTest {
        val registry = ComponentRegistry()
        val context = FakeInteractionContext(registry)
        val modal = ModalBuilder("modal:feedback", "Send feedback")

        val result = context.awaitModal(modal, timeout = 10.milliseconds)

        assertNull(result)
        assertFalse(registry.dispatch("modal:feedback", FakeModalContext(registry, "modal:feedback")))
    }

}
