# Akuma

A framework-agnostic Kotlin DSL for Discord bot interactions: slash/user/message
commands, autocomplete, buttons, string/entity selects, modals, and Components V2.

Akuma splits into a core module with zero Discord-library imports, plus one adapter
per Discord framework. Every adapter exposes the same DSL and the same context
surface (`required`/`optional`/`nullable`, `reply`/`editReply`/`followUp`,
`ephemeral: Boolean = false` everywhere), so a command written against
`akuma-jda` reads the same as one written against `akuma-kord` — only the
imports change.

## Modules

| Artifact | What it is | Depends on |
|---|---|---|
| `dev.deftu:akuma` | Core DSL and data model, no Discord library | — |
| `dev.deftu:akuma-jda` | JDA adapter | JDA 6.7.0 |
| `dev.deftu:akuma-kord` | Kord adapter | Kord 0.18.1 |

## Installation

```kotlin
repositories {
    maven("https://maven.deftu.dev/releases/")
}

dependencies {
    // pick one adapter — it pulls in core transitively
    implementation("dev.deftu:akuma-jda:0.10.0")
    // implementation("dev.deftu:akuma-kord:0.10.0")
}
```

## Quick start

### JDA

```kotlin
import dev.deftu.akuma.jda.commands
import net.dv8tion.jda.api.JDABuilder

fun main() {
    val jda = JDABuilder.createDefault(System.getenv("DISCORD_TOKEN")).build()

    jda.commands {
        command("ping", "Replies with pong") {
            action {
                reply("pong")
            }
        }
    }
}
```

### Kord

```kotlin
import dev.deftu.akuma.kord.commands
import dev.kord.core.Kord

suspend fun main() {
    val kord = Kord(System.getenv("DISCORD_TOKEN"))

    kord.commands {
        command("ping", "Replies with pong") {
            action {
                reply("pong")
            }
        }
    }

    kord.login()
}
```

`jda.commands { }` / `kord.commands { }` register the commands with Discord and
wire up the dispatcher in one call. Register to a single guild instead of
globally with the overload that takes a guild ID (`Long` for JDA, `Snowflake`
for Kord).

## Options and autocomplete

```kotlin
command("echo", "Echoes text back") {
    string("text", "Text to echo") {
        required()
    }

    action {
        reply(required<String>("text"))
    }
}
```

Autocomplete is opt-in per option and gets its own suspend callback:

```kotlin
string("fruit", "Pick a fruit") {
    autocomplete { ctx ->
        val matches = listOf("apple", "banana", "cherry")
            .filter { it.startsWith(ctx.focusedValue) }
            .map { AkumaChoice(it, it) }

        ctx.replyChoices(matches)
    }
}
```

Discord doesn't allow an option to have both static `choice(...)`/`choices(...)`
entries and `autocomplete { }` — Akuma rejects that combination at registration
time in both adapters.

## Buttons and selects

There's no standalone button/select listener — components are handled through
`awaitComponent`, which sends the message and suspends until one of the
attached components is used (or a timeout elapses):

```kotlin
action {
    val result = awaitComponent(
        "Are you sure?",
        ComponentBuilder.Button("confirm", "Confirm").apply { style = AkumaButtonStyle.SUCCESS },
        ComponentBuilder.Button("cancel", "Cancel").apply { style = AkumaButtonStyle.DANGER },
    )

    when (result?.customId) {
        "confirm" -> result.reply("Confirmed")
        "cancel" -> result.reply("Cancelled")
        else -> editReply("Timed out")
    }
}
```

`ComponentBuilder.StringSelect` and `ComponentBuilder.EntitySelect` work the
same way — pass one to `awaitComponent` and read `.values` (or
`selected<T>()` for entity selects) off the resulting context.

## Modals

```kotlin
action {
    val modal = ModalBuilder("feedback", "Send feedback").apply {
        textInput("message", "Your message")
    }

    val submission = awaitModal(modal)
    submission?.reply("Thanks: ${submission.required<String>("message")}")
}
```

## Components V2

Components V2 replaces message content/embeds outright, so it gets its own
reply methods (`replyComponents`/`editReplyComponents`/`followUpComponents`)
instead of overloading the content-based ones:

```kotlin
action {
    replyComponents(
        ContainerBuilder().apply {
            textDisplay("**Status**: all systems operational")
        },
    )
}
```

## User/message context-menu commands

```kotlin
userCommand("Avatar") {
    action {
        val user = target<User>()
        reply("${user.name}'s avatar")
    }
}
```

`User` here is the adapter's own type (`net.dv8tion.jda.api.entities.User` for
JDA, `dev.kord.core.entity.User` for Kord) — `target<T>()` resolves against
whichever adapter built the context. Use `messageCommand(name) { }` the same
way for message context-menu commands.

## Contributing

Issues and PRs welcome. See `AGENTS.md` for repo conventions if you're sending
a PR.

## License

[LGPL-3.0](LICENSE)
