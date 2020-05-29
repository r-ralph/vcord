package ms.ralph.vcord.gui.converter

import javafx.util.StringConverter
import ms.ralph.vcord.gui.model.MidiInputDeviceViewData

class MidiInputDeviceViewDataConverter : StringConverter<MidiInputDeviceViewData>() {
    override fun toString(guild: MidiInputDeviceViewData?): String = when (guild) {
        is MidiInputDeviceViewData.Actual -> guild.name
        MidiInputDeviceViewData.Invalid,
        null -> ""
    }

    override fun fromString(string: String?): MidiInputDeviceViewData = throw NotImplementedError()
}
