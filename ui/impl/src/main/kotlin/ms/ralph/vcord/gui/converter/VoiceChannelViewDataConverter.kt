package ms.ralph.vcord.gui.converter

import javafx.util.StringConverter
import ms.ralph.vcord.gui.model.VoiceChannelViewData

class VoiceChannelViewDataConverter : StringConverter<VoiceChannelViewData>() {
    override fun toString(voiceChannel: VoiceChannelViewData?): String = when (voiceChannel) {
        is VoiceChannelViewData.Actual -> voiceChannel.name
        VoiceChannelViewData.Invalid,
        null -> ""
    }

    override fun fromString(string: String?): VoiceChannelViewData = throw NotImplementedError()
}
