package ms.ralph.vcord.integration.discord

import ms.ralph.vcord.core.manager.TeamDataManager
import ms.ralph.vcord.core.model.Guild
import ms.ralph.vcord.core.model.GuildId
import ms.ralph.vcord.core.model.BotStatus
import ms.ralph.vcord.core.model.User
import ms.ralph.vcord.core.model.UserId
import ms.ralph.vcord.core.model.VoiceChannel
import ms.ralph.vcord.core.model.VoiceChannelId

internal class MockVcordDiscordClient(teamManager: TeamDataManager) : VcordDiscordClient(teamManager) {
    override fun init() {
        teamManager.updateAvailableGuildList(
            listOf(
                Guild(
                    GuildId("g1"),
                    "Guild1",
                    listOf(
                        VoiceChannel(
                            VoiceChannelId("g1-vc1"),
                            "VC1",
                            listOf()
                        ),
                        VoiceChannel(
                            VoiceChannelId("g1-vc2"),
                            "VC2",
                            listOf(
                                User(
                                    UserId("p1"),
                                    "User1",
                                    "https://pbs.twimg.com/profile_images/880072142750064640/WtSsvTLx_normal.jpg"
                                ),
                                User(
                                    UserId("p2"),
                                    "User2",
                                    "https://pbs.twimg.com/profile_images/880072142750064640/WtSsvTLx_normal.jpg"
                                )
                            )
                        )
                    )
                ),
                Guild(GuildId("id2"), "Guild2", emptyList())
            )
        )
        teamManager.updateBotStatus(BotStatus.READY)
    }

    override fun shutdown() = Unit
}
