package dev.deftu.akuma.jda

import dev.deftu.akuma.AkumaChoice
import dev.deftu.akuma.AutoCompleteContext
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent

public class JdaAutoCompleteContext(public val event: CommandAutoCompleteInteractionEvent) : AutoCompleteContext {
    override val focusedName: String get() = event.focusedOption.name
    override val focusedValue: String get() = event.focusedOption.value

    override suspend fun replyChoices(choices: List<AkumaChoice>) {
        event.replyChoices(choices.map(AkumaChoice::toJda)).submit().await()
    }
}
