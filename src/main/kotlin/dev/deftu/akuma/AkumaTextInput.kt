package dev.deftu.akuma

public class AkumaTextInput(
    public val name: String,
    public val label: String,
    public val style: AkumaTextInputStyle,
    public val placeholder: String?,
    public val value: String?,
    public val isRequired: Boolean,
    public val minLength: Int?,
    public val maxLength: Int?,
)
