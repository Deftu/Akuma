package dev.deftu.akuma

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AkumaDispatcherTest {

    @Test
    fun `registering a command by a name already present replaces it instead of duplicating it`() {
        val dispatcher = AkumaDispatcher()
        val first = CommandBuilder("mytag").apply { description = "v1" }.build()
        val second = CommandBuilder("mytag").apply { description = "v2" }.build()

        dispatcher.register(listOf(first))
        dispatcher.register(listOf(second))

        assertEquals(1, dispatcher.commands.size)
        assertSame(second, dispatcher.commands.single())
    }

    @Test
    fun `registering distinct names accumulates rather than overwrites`() {
        val dispatcher = AkumaDispatcher()
        val first = CommandBuilder("one").build()
        val second = CommandBuilder("two").build()

        dispatcher.register(listOf(first))
        dispatcher.register(listOf(second))

        assertEquals(setOf("one", "two"), dispatcher.commands.map { it.name }.toSet())
    }

    @Test
    fun `repeatedly re-registering the same command does not grow the backing list`() {
        val dispatcher = AkumaDispatcher()
        val definition = CommandBuilder("mytag").build()

        repeat(50) {
            dispatcher.register(listOf(definition))
        }

        assertEquals(1, dispatcher.commands.size)
    }

    @Test
    fun `subcommand added through the description overload rejects isNsfw like any other child`() {
        // Regression test: this overload used to set the child's parent from the
        // not-yet-assigned `this@CommandBuilder.parent` instead of `this@CommandBuilder`,
        // leaving the child's parent null and silently skipping the "cannot set X on a
        // child command" guards below.
        assertThrows(IllegalStateException::class.java) {
            CommandBuilder("root").apply {
                subcommand("child", "desc") {
                    isNsfw = true
                }
            }
        }
    }

    @Test
    fun `dispatchComponent routes through the dispatcher's own component registry`() = runTest {
        val dispatcher = AkumaDispatcher()
        val deferred = CompletableDeferred<InteractionContext>()
        val context = object : ButtonContext {
            override val customId: String = "btn:confirm"
            override val componentRegistry: ComponentRegistry get() = error("not used in this test")
            override fun <T : Any> nullable(name: String, type: kotlin.reflect.KClass<T>): T? = error("not used in this test")
            override fun <T : Any> optional(name: String, type: kotlin.reflect.KClass<T>): java.util.Optional<T> = error("not used in this test")
            override fun <T : Any> required(name: String, type: kotlin.reflect.KClass<T>): T = error("not used in this test")
            override fun <T> nullable(name: String, deserializer: (RawOption) -> T): T? = error("not used in this test")
            override fun <T> optional(name: String, deserializer: (RawOption) -> T): java.util.Optional<T & Any> = error("not used in this test")
            override fun <T> required(name: String, deserializer: (RawOption) -> T): T = error("not used in this test")
            override suspend fun reply(content: String, vararg components: ComponentBuilder, ephemeral: Boolean) = error("not used in this test")
            override suspend fun editReply(content: String, vararg components: ComponentBuilder) = error("not used in this test")
            override suspend fun followUp(content: String, vararg components: ComponentBuilder, ephemeral: Boolean) = error("not used in this test")
            override suspend fun showModal(modal: ModalBuilder) = error("not used in this test")
            override suspend fun replyComponents(vararg components: LayoutComponentBuilder, ephemeral: Boolean) = error("not used in this test")
            override suspend fun editReplyComponents(vararg components: LayoutComponentBuilder) = error("not used in this test")
            override suspend fun followUpComponents(vararg components: LayoutComponentBuilder, ephemeral: Boolean) = error("not used in this test")
        }

        dispatcher.componentRegistry.register("btn:confirm", deferred)
        dispatcher.dispatchComponent("btn:confirm", context)

        assertTrue(deferred.isCompleted)
        assertSame(context, deferred.await())
    }

}
