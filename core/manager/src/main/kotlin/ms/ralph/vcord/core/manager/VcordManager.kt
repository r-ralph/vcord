package ms.ralph.vcord.core.manager

import ms.ralph.vcord.core.model.MidiControllerSlot

object VcordManager {
    val alphaAudioDataManager: AudioDataManager = AudioDataManager(
        "alpha",
        listOf(
            MidiControllerSlot.SLOT_1,
            MidiControllerSlot.SLOT_2,
            MidiControllerSlot.SLOT_3,
            MidiControllerSlot.SLOT_4
        )
    )
    val bravoAudioDataManager: AudioDataManager = AudioDataManager(
        "bravo",
        listOf(
            MidiControllerSlot.SLOT_5,
            MidiControllerSlot.SLOT_6,
            MidiControllerSlot.SLOT_7,
            MidiControllerSlot.SLOT_8
        )
    )
    val alphaTeamDataManager: TeamDataManager = TeamDataManager("alpha")
    val bravoTeamDataManager: TeamDataManager = TeamDataManager("bravo")
    val midiDataManager: MidiDataManager = MidiDataManager()
}
