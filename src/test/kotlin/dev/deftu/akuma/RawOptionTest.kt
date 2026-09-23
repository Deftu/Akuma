package dev.deftu.akuma

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

private class FakeRawOption(override val asString: String = "hello") : RawOption {
    override val asInt: Int = 7
    override val asLong: Long = 8L
    override val asDouble: Double = 9.5
    override val asBoolean: Boolean = true
    override val native: Any = Unit
}

private enum class Difficulty(override val optionValue: String) : AkumaOptionEnum {
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard"),
}

private enum class PlainEnum { FOO, BAR }

class RawOptionTest {

    private val option = FakeRawOption()

    @Test
    fun `dispatches to the matching primitive accessor`() {
        assertEquals("hello", option.deserializePrimitive(String::class))
        assertEquals(7, option.deserializePrimitive(Int::class))
        assertEquals(8L, option.deserializePrimitive(Long::class))
        assertEquals(9.5, option.deserializePrimitive(Double::class))
        assertEquals(true, option.deserializePrimitive(Boolean::class))
    }

    @Test
    fun `returns null for a type it doesn't know about, instead of throwing`() {
        assertNull(option.deserializePrimitive(FakeRawOption::class))
    }

    @Test
    fun `resolves an AkumaOptionEnum by its declared optionValue`() {
        val option = FakeRawOption(asString = "medium")
        assertEquals(Difficulty.MEDIUM, option.deserializePrimitive(Difficulty::class))
    }

    @Test
    fun `does not fall back to the constant name for an AkumaOptionEnum`() {
        val option = FakeRawOption(asString = "MEDIUM")
        assertNull(option.deserializePrimitive(Difficulty::class))
    }

    @Test
    fun `returns null for a string that matches no declared optionValue`() {
        val option = FakeRawOption(asString = "impossible")
        assertNull(option.deserializePrimitive(Difficulty::class))
    }

    @Test
    fun `returns null for a plain enum that doesn't implement AkumaOptionEnum`() {
        val option = FakeRawOption(asString = "FOO")
        assertNull(option.deserializePrimitive(PlainEnum::class))
    }

}
