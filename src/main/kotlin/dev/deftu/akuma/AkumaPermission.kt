package dev.deftu.akuma

// Bit offsets match Discord's permission bitfield (see
// https://discord.com/developers/docs/topics/permissions). MANAGE_ROLES and
// MANAGE_PERMISSIONS share offset 28 -- same underlying bit, different name
// depending on whether it's read at guild or channel scope.
public enum class AkumaPermission(public val offset: Int) {
    CREATE_INSTANT_INVITE(0),
    KICK_MEMBERS(1),
    BAN_MEMBERS(2),
    ADMINISTRATOR(3),
    MANAGE_CHANNEL(4),
    MANAGE_SERVER(5),
    MESSAGE_ADD_REACTION(6),
    VIEW_AUDIT_LOGS(7),
    PRIORITY_SPEAKER(8),
    VOICE_STREAM(9),
    VIEW_CHANNEL(10),
    MESSAGE_SEND(11),
    MESSAGE_TTS(12),
    MESSAGE_MANAGE(13),
    MESSAGE_EMBED_LINKS(14),
    MESSAGE_ATTACH_FILES(15),
    MESSAGE_HISTORY(16),
    MESSAGE_MENTION_EVERYONE(17),
    MESSAGE_EXT_EMOJI(18),
    VIEW_GUILD_INSIGHTS(19),
    VOICE_CONNECT(20),
    VOICE_SPEAK(21),
    VOICE_MUTE_OTHERS(22),
    VOICE_DEAF_OTHERS(23),
    VOICE_MOVE_OTHERS(24),
    VOICE_USE_VAD(25),
    NICKNAME_CHANGE(26),
    NICKNAME_MANAGE(27),
    MANAGE_ROLES(28),
    MANAGE_PERMISSIONS(28),
    MANAGE_WEBHOOKS(29),
    MANAGE_GUILD_EXPRESSIONS(30),
    USE_APPLICATION_COMMANDS(31),
    REQUEST_TO_SPEAK(32),
    MANAGE_EVENTS(33),
    MANAGE_THREADS(34),
    CREATE_PUBLIC_THREADS(35),
    CREATE_PRIVATE_THREADS(36),
    MESSAGE_EXT_STICKER(37),
    MESSAGE_SEND_IN_THREADS(38),
    USE_EMBEDDED_ACTIVITIES(39),
    MODERATE_MEMBERS(40),
    VIEW_CREATOR_MONETIZATION_ANALYTICS(41),
    VOICE_USE_SOUNDBOARD(42),
    CREATE_GUILD_EXPRESSIONS(43),
    CREATE_SCHEDULED_EVENTS(44),
    VOICE_USE_EXTERNAL_SOUNDS(45),
    MESSAGE_ATTACH_VOICE_MESSAGE(46),
    VOICE_SET_STATUS(48),
    MESSAGE_SEND_POLLS(49),
    USE_EXTERNAL_APPLICATIONS(50),
    PIN_MESSAGES(51),
    BYPASS_SLOWMODE(52);

    public val rawValue: Long
        get() = 1L shl offset
}

public class AkumaDefaultPermissions private constructor(public val rawValue: Long?) {
    public companion object {
        public val ENABLED: AkumaDefaultPermissions = AkumaDefaultPermissions(null)
        public val DISABLED: AkumaDefaultPermissions = AkumaDefaultPermissions(0L)

        public fun enabledFor(raw: Long): AkumaDefaultPermissions {
            return AkumaDefaultPermissions(raw)
        }

        public fun enabledFor(permissions: Collection<AkumaPermission>): AkumaDefaultPermissions {
            return enabledFor(permissions.fold(0L) { acc, permission -> acc or permission.rawValue })
        }

        public fun enabledFor(vararg permissions: AkumaPermission): AkumaDefaultPermissions {
            return enabledFor(permissions.toList())
        }
    }
}
