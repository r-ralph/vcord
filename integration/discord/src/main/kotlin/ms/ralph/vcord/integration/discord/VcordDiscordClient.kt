package ms.ralph.vcord.integration.discord

import ms.ralph.vcord.core.manager.TeamDataManager

abstract class VcordDiscordClient(
    protected val teamManager: TeamDataManager
) {
    abstract fun init()
    abstract fun shutdown()
}
