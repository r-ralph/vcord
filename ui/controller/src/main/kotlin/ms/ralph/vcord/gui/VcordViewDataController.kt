package ms.ralph.vcord.gui

import ms.ralph.vcord.core.manager.VcordManager
import ms.ralph.vcord.core.model.MidiControllerSlot
import ms.ralph.vcord.core.model.PlayerSlot

class VcordViewDataController {

    val generalViewDataController: GeneralViewDataController =
        GeneralViewDataController(VcordManager.midiDataManager)

    val alphaTeamViewDataController: TeamViewDataController =
        TeamViewDataController(
            VcordManager.alphaTeamDataManager,
            VcordManager.alphaAudioDataManager,
            mapOf(
                PlayerSlot.SLOT_1 to PlayerViewDataController(
                    PlayerSlot.SLOT_1,
                    MidiControllerSlot.SLOT_1,
                    VcordManager.alphaTeamDataManager,
                    VcordManager.alphaAudioDataManager,
                    VcordManager.midiDataManager
                ),
                PlayerSlot.SLOT_2 to PlayerViewDataController(
                    PlayerSlot.SLOT_2,
                    MidiControllerSlot.SLOT_2,
                    VcordManager.alphaTeamDataManager,
                    VcordManager.alphaAudioDataManager,
                    VcordManager.midiDataManager
                ),
                PlayerSlot.SLOT_3 to PlayerViewDataController(
                    PlayerSlot.SLOT_3,
                    MidiControllerSlot.SLOT_3,
                    VcordManager.alphaTeamDataManager,
                    VcordManager.alphaAudioDataManager,
                    VcordManager.midiDataManager
                ),
                PlayerSlot.SLOT_4 to PlayerViewDataController(
                    PlayerSlot.SLOT_4,
                    MidiControllerSlot.SLOT_4,
                    VcordManager.alphaTeamDataManager,
                    VcordManager.alphaAudioDataManager,
                    VcordManager.midiDataManager
                )
            )
        )

    val bravoTeamViewDataController: TeamViewDataController =
        TeamViewDataController(
            VcordManager.bravoTeamDataManager,
            VcordManager.bravoAudioDataManager,
            mapOf(
                PlayerSlot.SLOT_1 to PlayerViewDataController(
                    PlayerSlot.SLOT_1,
                    MidiControllerSlot.SLOT_5,
                    VcordManager.bravoTeamDataManager,
                    VcordManager.bravoAudioDataManager,
                    VcordManager.midiDataManager
                ),
                PlayerSlot.SLOT_2 to PlayerViewDataController(
                    PlayerSlot.SLOT_2,
                    MidiControllerSlot.SLOT_6,
                    VcordManager.bravoTeamDataManager,
                    VcordManager.bravoAudioDataManager,
                    VcordManager.midiDataManager
                ),
                PlayerSlot.SLOT_3 to PlayerViewDataController(
                    PlayerSlot.SLOT_3,
                    MidiControllerSlot.SLOT_7,
                    VcordManager.bravoTeamDataManager,
                    VcordManager.bravoAudioDataManager,
                    VcordManager.midiDataManager
                ),
                PlayerSlot.SLOT_4 to PlayerViewDataController(
                    PlayerSlot.SLOT_4,
                    MidiControllerSlot.SLOT_8,
                    VcordManager.bravoTeamDataManager,
                    VcordManager.bravoAudioDataManager,
                    VcordManager.midiDataManager
                )
            )
        )
}
