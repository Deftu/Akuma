package dev.deftu.akuma

public class ModalBuilder(public val customId: String, public var title: String) {
    private val inputs = mutableListOf<TextInputBuilder>()

    public fun textInput(name: String, label: String, block: TextInputBuilder.() -> Unit = {}) {
        inputs.add(TextInputBuilder(name, label).apply(block))
    }

    public fun build(): ModalDefinition {
        return ModalDefinition(customId, title, inputs.map(TextInputBuilder::build))
    }
}
