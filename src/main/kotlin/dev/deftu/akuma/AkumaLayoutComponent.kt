package dev.deftu.akuma

// Mirrors Discord's own Components V2 constraint types (MessageTopLevelComponent,
// ContainerChildComponent, SectionContentComponent, SectionAccessoryComponent) so an invalid tree
// (e.g. a MediaGallery as a section's accessory) fails to compile instead of failing at Discord's
// API boundary. `AkumaComponent.Button` also implements `AkumaSectionAccessory` -- a section's
// accessory can be either a thumbnail or an interactive button.
public sealed interface AkumaTopLevelComponent
public sealed interface AkumaContainerChild : AkumaTopLevelComponent
public sealed interface AkumaSectionContent
public sealed interface AkumaSectionAccessory

public sealed class AkumaLayoutComponent {
    public class Container(
        public val components: List<AkumaContainerChild>,
        public val accentColor: Int?,
        public val isSpoiler: Boolean,
    ) : AkumaLayoutComponent(), AkumaTopLevelComponent

    public class Section(
        public val content: List<AkumaSectionContent>,
        public val accessory: AkumaSectionAccessory,
    ) : AkumaLayoutComponent(), AkumaContainerChild

    public class TextDisplay(
        public val content: String,
    ) : AkumaLayoutComponent(), AkumaContainerChild, AkumaSectionContent

    public class MediaGallery(
        public val items: List<Item>,
    ) : AkumaLayoutComponent(), AkumaContainerChild {
        public class Item(
            public val url: String,
            public val description: String?,
            public val isSpoiler: Boolean,
        )
    }

    public class Separator(
        public val isDivider: Boolean,
        public val spacing: AkumaSeparatorSpacing,
    ) : AkumaLayoutComponent(), AkumaContainerChild

    public class Thumbnail(
        public val url: String,
        public val description: String?,
        public val isSpoiler: Boolean,
    ) : AkumaLayoutComponent(), AkumaSectionAccessory
}
