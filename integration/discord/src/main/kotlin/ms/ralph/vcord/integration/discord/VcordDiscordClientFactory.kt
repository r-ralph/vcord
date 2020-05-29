package ms.ralph.vcord.integration.discord

import ms.ralph.vcord.core.manager.AudioDataManager
import ms.ralph.vcord.core.manager.TeamDataManager
import ms.ralph.vcord.integration.audio.VcordAudioClient

interface VcordDiscordClientFactory {
    fun create(
        teamManager: TeamDataManager,
        audioDataManager: AudioDataManager,
        apiKey: String,
        audioClient: VcordAudioClient
    ): VcordDiscordClient
}
