package dev.deftu.akuma

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.Optional
import kotlin.reflect.KClass

private data class FakeEntity(val id: String)

private class FakeCommandContext(private val values: Map<String, Any>) : CommandContext {
    override fun <T : Any> nullable(name: String, type: KClass<T>): T? {
        val value = values[name] ?: return null
        return type.java.cast(value)
    }

    override fun <T : Any> optional(name: String, type: KClass<T>): Optional<T> {
        return Optional.ofNullable(nullable(name, type))
    }

    override fun <T : Any> required(name: String, type: KClass<T>): T {
        return nullable(name, type) ?: throw IllegalArgumentException("Option $name is required")
    }

    override fun <T : Any> target(type: KClass<T>): T {
        return type.java.cast(values.getValue("\$target"))
    }

    override fun <T> nullable(name: String, deserializer: (RawOption) -> T): T? = error("not used in this test")
    override fun <T> optional(name: String, deserializer: (RawOption) -> T): Optional<T & Any> = error("not used in this test")
    override fun <T> required(name: String, deserializer: (RawOption) -> T): T = error("not used in this test")

    override val componentRegistry: ComponentRegistry get() = error("not used in this test")
    override suspend fun reply(content: String, vararg components: ComponentBuilder, ephemeral: Boolean) = error("not used in this test")
    override suspend fun editReply(content: String, vararg components: ComponentBuilder) = error("not used in this test")
    override suspend fun followUp(content: String, vararg components: ComponentBuilder, ephemeral: Boolean) = error("not used in this test")
    override suspend fun showModal(modal: ModalBuilder) = error("not used in this test")
    override suspend fun replyComponents(vararg components: LayoutComponentBuilder, ephemeral: Boolean) = error("not used in this test")
    override suspend fun editReplyComponents(vararg components: LayoutComponentBuilder) = error("not used in this test")
    override suspend fun followUpComponents(vararg components: LayoutComponentBuilder, ephemeral: Boolean) = error("not used in this test")
}

class CommandContextTest {

    @Test
    fun `zero-config required resolves through the concrete implementation even when statically typed as CommandContext`() {
        val entity = FakeEntity("42")

        // `context` is statically typed as the generic `CommandContext` interface here -- the
        // same static type `this` has inside a `CommandBuilder.action { }` block, regardless of
        // which adapter built it. Unlike an extension function (resolved by static type at
        // compile time), `required<T>` forwards to a real virtual method, so it still reaches
        // FakeCommandContext's own dispatch for a type core knows nothing about.
        val context: CommandContext = FakeCommandContext(mapOf("target" to entity))

        assertEquals(entity, context.required<FakeEntity>("target"))
    }

    @Test
    fun `required throws when the option is missing`() {
        val context: CommandContext = FakeCommandContext(emptyMap())

        assertThrows(IllegalArgumentException::class.java) {
            context.required<FakeEntity>("target")
        }
    }

    @Test
    fun `zero-config target resolves through the concrete implementation even when statically typed as CommandContext`() {
        val entity = FakeEntity("target-42")

        // Same virtual-dispatch reasoning as `required<T>` above, but for the context-menu
        // target a USER/MESSAGE command's adapter implementation provides.
        val context: CommandContext = FakeCommandContext(mapOf("\$target" to entity))

        assertEquals(entity, context.target<FakeEntity>())
    }

}
