package ms.ralph.vcord.gui

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.Observables
import ms.ralph.vcord.core.manager.AudioDataManager
import ms.ralph.vcord.core.manager.MidiDataManager
import ms.ralph.vcord.core.manager.TeamDataManager
import ms.ralph.vcord.core.model.MidiControllerSlot
import ms.ralph.vcord.core.model.PlayerSlot
import ms.ralph.vcord.core.model.UserId
import ms.ralph.vcord.gui.model.UserListViewData
import ms.ralph.vcord.gui.model.UserViewData
import java.util.Optional

class PlayerViewDataController(
    private val playerSlot: PlayerSlot,
    midiControllerSlot: MidiControllerSlot,
    private val teamDataManager: TeamDataManager,
    private val audioDataManager: AudioDataManager,
    midiDataManager: MidiDataManager
) {

    val userListViewData: Observable<UserListViewData> =
        createUserListViewDataObservable(teamDataManager.getSelectedUserId(playerSlot))
            .distinctUntilChanged()
            .replay(1)
            .refCount()

    val speakingStatusViewData: Observable<Boolean> = teamDataManager.getSpeakingStatus(playerSlot)

    val volumeViewData: Observable<Double> = midiDataManager.getVolume(midiControllerSlot)

    fun updateSelectedUser(user: UserViewData) = when (user) {
        is UserViewData.Actual -> teamDataManager.updateSelectedUserId(playerSlot, UserId(user.id))
        UserViewData.Invalid -> teamDataManager.updateSelectedUserId(playerSlot, null)
    }

    fun updateVolume(volume: Double) = audioDataManager.updateVolume(playerSlot, volume)

    private fun createUserListViewDataObservable(
        selectedUserIdObservable: Observable<Optional<UserId>>
    ): Observable<UserListViewData> = Observables.combineLatest(
        teamDataManager.voiceAvailableUserList,
        selectedUserIdObservable
    ) { userList, selectedUserId -> TeamViewDataFactory.createUserListViewData(userList, selectedUserId.orElse(null)) }
}
