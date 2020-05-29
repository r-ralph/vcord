package ms.ralph.vcord.core.model

enum class BotStatus {
    DEFAULT,
    CONNECTING,
    READY,
    VOICE_CONNECTING,
    VOICE_ESTABLISHED,
    ERROR_NO_API_KEY,
}
