package ms.ralph.vcord.gui.model

sealed class GuildViewData {
    data class Actual(
        val id: String,
        val name: String
    ) : GuildViewData()

    object Invalid : GuildViewData()
}
