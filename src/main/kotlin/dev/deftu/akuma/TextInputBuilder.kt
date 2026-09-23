package dev.deftu.akuma

public class TextInputBuilder(public val name: String, public var label: String) {
    public var style: AkumaTextInputStyle = AkumaTextInputStyle.SHORT
    public var placeholder: String? = null
    public var value: String? = null
    public var isRequired: Boolean = true
    public var minLength: Int? = null
    public var maxLength: Int? = null

    public fun build(): AkumaTextInput {
        return AkumaTextInput(name, label, style, placeholder, value, isRequired, minLength, maxLength)
    }
}
