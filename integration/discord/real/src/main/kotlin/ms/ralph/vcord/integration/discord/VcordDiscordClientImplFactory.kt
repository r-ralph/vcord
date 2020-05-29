package ms.ralph.vcord.integration.discord

import com.google.auto.service.AutoService
import ms.ralph.vcord.core.manager.AudioDataManager
import ms.ralph.vcord.core.manager.TeamDataManager
import ms.ralph.vcord.integration.audio.VcordAudioClient

@AutoService(VcordDiscordClientFactory::class)
internal class VcordDiscordClientImplFactory : VcordDiscordClientFactory {
    override fun create(
        teamManager: TeamDataManager,
        audioDataManager: AudioDataManager,
        apiKey: String,
        audioClient: VcordAudioClient
    ): VcordDiscordClient = VcordDiscordClientImpl(teamManager, audioDataManager, apiKey, audioClient)
}
