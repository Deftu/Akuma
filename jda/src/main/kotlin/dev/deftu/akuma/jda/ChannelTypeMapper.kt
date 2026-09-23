package dev.deftu.akuma.jda

import dev.deftu.akuma.AkumaChannelType
import net.dv8tion.jda.api.entities.channel.ChannelType
import java.util.EnumSet

public fun AkumaChannelType.toJda(): ChannelType {
    return when (this) {
        AkumaChannelType.TEXT -> ChannelType.TEXT
        AkumaChannelType.PRIVATE -> ChannelType.PRIVATE
        AkumaChannelType.VOICE -> ChannelType.VOICE
        AkumaChannelType.GROUP -> ChannelType.GROUP
        AkumaChannelType.CATEGORY -> ChannelType.CATEGORY
        AkumaChannelType.NEWS -> ChannelType.NEWS
        AkumaChannelType.STAGE -> ChannelType.STAGE
        AkumaChannelType.GUILD_NEWS_THREAD -> ChannelType.GUILD_NEWS_THREAD
        AkumaChannelType.GUILD_PUBLIC_THREAD -> ChannelType.GUILD_PUBLIC_THREAD
        AkumaChannelType.GUILD_PRIVATE_THREAD -> ChannelType.GUILD_PRIVATE_THREAD
        AkumaChannelType.GUILD_DIRECTORY -> ChannelType.GUILD_DIRECTORY
        AkumaChannelType.FORUM -> ChannelType.FORUM
        AkumaChannelType.MEDIA -> ChannelType.MEDIA
    }
}

public fun Set<AkumaChannelType>.toJda(): EnumSet<ChannelType> {
    val mapped = map(AkumaChannelType::toJda)
    return if (mapped.isEmpty()) EnumSet.noneOf(ChannelType::class.java) else EnumSet.copyOf(mapped)
}
