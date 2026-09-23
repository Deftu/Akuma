package dev.deftu.akuma

// Common type for the vararg accepted by `InteractionContext.replyComponents`/`editReplyComponents`/
// `followUpComponents` -- implemented by every builder whose result can sit at the root of a
// Components V2 message (Container, Section, TextDisplay, MediaGallery, Separator). `ThumbnailBuilder`
// deliberately doesn't implement this -- a thumbnail is only ever a section's accessory, never a
// top-level component.
public sealed interface LayoutComponentBuilder {
    public fun build(): AkumaTopLevelComponent
}
