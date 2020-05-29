package ms.ralph.vcord.gui.model

sealed class UserViewData {
    data class Actual(
        val id: String,
        val name: String,
        val iconUrl: String
    ) : UserViewData()

    object Invalid : UserViewData()
}
