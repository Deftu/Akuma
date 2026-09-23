package dev.deftu.akuma

public enum class AkumaLocale(public val code: String) {
    ARABIC("ar"),
    BULGARIAN("bg"),
    CHINESE_CHINA("zh-CN"),
    CHINESE_TAIWAN("zh-TW"),
    CROATIAN("hr"),
    CZECH("cs"),
    DANISH("da"),
    DUTCH("nl"),
    ENGLISH_UK("en-GB"),
    ENGLISH_US("en-US"),
    FINNISH("fi"),
    FRENCH("fr"),
    GERMAN("de"),
    GREEK("el"),
    HEBREW("he"),
    HINDI("hi"),
    HUNGARIAN("hu"),
    INDONESIAN("id"),
    ITALIAN("it"),
    JAPANESE("ja"),
    KOREAN("ko"),
    LITHUANIAN("lt"),
    NORWEGIAN("no"),
    POLISH("pl"),
    PORTUGUESE_BRAZILIAN("pt-BR"),
    ROMANIAN_ROMANIA("ro"),
    RUSSIAN("ru"),
    SPANISH("es-ES"),
    SPANISH_LATAM("es-419"),
    SWEDISH("sv-SE"),
    THAI("th"),
    TURKISH("tr"),
    UKRAINIAN("uk"),
    VIETNAMESE("vi-VN"),
    UNKNOWN("unknown");

    public companion object {
        public fun from(code: String): AkumaLocale {
            return entries.find { it.code == code } ?: UNKNOWN
        }
    }
}
