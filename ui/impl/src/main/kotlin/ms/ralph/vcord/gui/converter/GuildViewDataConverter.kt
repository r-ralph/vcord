package ms.ralph.vcord.gui.converter

import javafx.util.StringConverter
import ms.ralph.vcord.gui.model.GuildViewData

class GuildViewDataConverter : StringConverter<GuildViewData>() {
    override fun toString(guild: GuildViewData?): String = when (guild) {
        is GuildViewData.Actual -> guild.name
        GuildViewData.Invalid,
        null -> ""
    }

    override fun fromString(string: String?): GuildViewData = throw NotImplementedError()
}
