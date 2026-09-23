package dev.deftu.akuma

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

private enum class OptionBuilderTestDifficulty(override val optionValue: String) : AkumaOptionEnum {
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard"),
}

// 27 constants -- one over the 25-entry cutoff `enumeration` switches to autocomplete at.
private enum class OptionBuilderTestManyOptions(override val optionValue: String) : AkumaOptionEnum {
    A("a"), B("b"), C("c"), D("d"), E("e"), F("f"), G("g"), H("h"), I("i"), J("j"),
    K("k"), L("l"), M("m"), N("n"), O("o"), P("p"), Q("q"), R("r"), S("s"), T("t"),
    U("u"), V("v"), W("w"), X("x"), Y("y"), Z("z"), EXTRA("extra"),
}

private class FakeAutoCompleteContext(
    override val focusedName: String,
    override val focusedValue: String,
) : AutoCompleteContext {
    var repliedChoices: List<AkumaChoice>? = null

    override suspend fun replyChoices(choices: List<AkumaChoice>) {
        repliedChoices = choices
    }
}

class OptionBuilderTest {

    @Test
    fun `enumeration uses static choices when the enum has fewer than 25 entries`() {
        val option = OptionBuilder.StringOption("difficulty", "How hard?").apply {
            enumeration<OptionBuilderTestDifficulty>()
        }.build()

        assertEquals(
            listOf("EASY" to "easy", "MEDIUM" to "medium", "HARD" to "hard"),
            option.choices.map { it.name to it.value },
        )
        assertFalse(option.isAutoComplete)
        assertNull(option.autoComplete)
    }

    @Test
    fun `enumeration falls back to autocomplete when the enum has 25 or more entries`() {
        val option = OptionBuilder.StringOption("letter", "Pick a letter").apply {
            enumeration<OptionBuilderTestManyOptions>()
        }.build()

        assertTrue(option.choices.isEmpty())
        assertTrue(option.isAutoComplete)
        assertNotNull(option.autoComplete)
    }

    @Test
    fun `the generated autocomplete filters by optionValue, case-insensitively`() = runBlocking {
        val option = OptionBuilder.StringOption("letter", "Pick a letter").apply {
            enumeration<OptionBuilderTestManyOptions>()
        }.build()

        val context = FakeAutoCompleteContext(focusedName = "letter", focusedValue = "A")
        option.autoComplete!!.invoke(context)

        assertEquals(
            listOf("A" to "a", "EXTRA" to "extra"),
            context.repliedChoices?.map { it.name to it.value },
        )
    }

    @Test
    fun `the generated autocomplete caps suggestions at 25`() = runBlocking {
        val option = OptionBuilder.StringOption("letter", "Pick a letter").apply {
            enumeration<OptionBuilderTestManyOptions>()
        }.build()

        val context = FakeAutoCompleteContext(focusedName = "letter", focusedValue = "")
        option.autoComplete!!.invoke(context)

        assertEquals(25, context.repliedChoices?.size)
    }

}
