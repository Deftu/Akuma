package dev.deftu.akuma

public interface AutoCompleteContext {
    public val focusedName: String
    public val focusedValue: String

    public suspend fun replyChoices(choices: List<AkumaChoice>)
}
