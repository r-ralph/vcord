package ms.ralph.vcord.gui.model

data class AudioOutputDeviceListViewData(
    val audioOutputDeviceList: List<AudioOutputDeviceViewData>,
    val selectedAudioOutputDevice: AudioOutputDeviceViewData
)
