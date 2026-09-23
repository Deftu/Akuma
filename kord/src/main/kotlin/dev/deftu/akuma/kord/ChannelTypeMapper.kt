package dev.deftu.akuma.kord

import dev.deftu.akuma.AkumaChannelType
import dev.kord.common.entity.ChannelType

public fun AkumaChannelType.toKord(): ChannelType {
    return when (this) {
        AkumaChannelType.TEXT -> ChannelType.GuildText
        AkumaChannelType.PRIVATE -> ChannelType.DM
        AkumaChannelType.VOICE -> ChannelType.GuildVoice
        AkumaChannelType.GROUP -> ChannelType.GroupDM
        AkumaChannelType.CATEGORY -> ChannelType.GuildCategory
        AkumaChannelType.NEWS -> ChannelType.GuildNews
        AkumaChannelType.STAGE -> ChannelType.GuildStageVoice
        AkumaChannelType.GUILD_NEWS_THREAD -> ChannelType.PublicNewsThread
        AkumaChannelType.GUILD_PUBLIC_THREAD -> ChannelType.PublicGuildThread
        AkumaChannelType.GUILD_PRIVATE_THREAD -> ChannelType.PrivateThread
        AkumaChannelType.GUILD_DIRECTORY -> ChannelType.GuildDirectory
        AkumaChannelType.FORUM -> ChannelType.GuildForum
        AkumaChannelType.MEDIA -> ChannelType.GuildMedia
    }
}

public fun Set<AkumaChannelType>.toKord(): List<ChannelType> {
    return map(AkumaChannelType::toKord)
}
