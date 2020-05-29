package ms.ralph.vcord.gui.converter

import javafx.util.StringConverter
import ms.ralph.vcord.gui.model.AudioOutputDeviceViewData

class AudioOutputDeviceViewDataConverter : StringConverter<AudioOutputDeviceViewData>() {
    override fun toString(guild: AudioOutputDeviceViewData?): String = when (guild) {
        is AudioOutputDeviceViewData.Actual -> guild.name
        AudioOutputDeviceViewData.Invalid,
        null -> ""
    }

    override fun fromString(string: String?): AudioOutputDeviceViewData = throw NotImplementedError()
}
