package ms.ralph.vcord.gui.model

sealed class AudioOutputDeviceViewData {
    data class Actual(
        val id: String,
        val name: String
    ) : AudioOutputDeviceViewData()

    object Invalid : AudioOutputDeviceViewData()
}
