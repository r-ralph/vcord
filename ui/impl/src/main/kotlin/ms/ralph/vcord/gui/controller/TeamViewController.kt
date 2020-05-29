package ms.ralph.vcord.gui.controller

import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ChoiceBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import ms.ralph.vcord.core.model.PlayerSlot
import ms.ralph.vcord.gui.TeamViewDataController
import ms.ralph.vcord.gui.converter.AudioOutputDeviceViewDataConverter
import ms.ralph.vcord.gui.converter.GuildViewDataConverter
import ms.ralph.vcord.gui.converter.VoiceChannelViewDataConverter
import ms.ralph.vcord.gui.model.AudioOutputDeviceListViewData
import ms.ralph.vcord.gui.model.AudioOutputDeviceViewData
import ms.ralph.vcord.gui.model.AudioStatusViewData
import ms.ralph.vcord.gui.model.BotStatusViewData
import ms.ralph.vcord.gui.model.GuildListViewData
import ms.ralph.vcord.gui.model.GuildViewData
import ms.ralph.vcord.gui.model.VoiceChannelListViewData
import ms.ralph.vcord.gui.model.VoiceChannelViewData
import ms.ralph.vcord.gui.util.subscribeWithMainThread
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.ResourceBundle

class TeamViewController : Initializable {

    @FXML
    private lateinit var player1: VBox

    @FXML
    private lateinit var player1Controller: PlayerViewController

    @FXML
    private lateinit var player2: VBox

    @FXML
    private lateinit var player2Controller: PlayerViewController

    @FXML
    private lateinit var player3: VBox

    @FXML
    private lateinit var player3Controller: PlayerViewController

    @FXML
    private lateinit var player4: VBox

    @FXML
    private lateinit var player4Controller: PlayerViewController

    @FXML
    private lateinit var botStatusText: Text

    @FXML
    private lateinit var audioStatusText: Text

    @FXML
    private lateinit var audioOutputDeviceChoiceBox: ChoiceBox<AudioOutputDeviceViewData>

    @FXML
    private lateinit var guildChoiceBox: ChoiceBox<GuildViewData>

    @FXML
    private lateinit var voiceChannelChoiceBox: ChoiceBox<VoiceChannelViewData>

    private lateinit var teamViewDataController: TeamViewDataController

    private var isUpdatingAudioOutputDeviceList: Boolean = false
    private var isUpdatingGuildList: Boolean = false
    private var isUpdatingVoiceChannelList: Boolean = false

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        audioOutputDeviceChoiceBox.converter = AudioOutputDeviceViewDataConverter()
        audioOutputDeviceChoiceBox.setOnAction {
            if (isUpdatingAudioOutputDeviceList) {
                return@setOnAction
            }
            val selectedAudioOutputDevice = audioOutputDeviceChoiceBox.value ?: return@setOnAction
            teamViewDataController.updateSelectedAudioOutputDevice(selectedAudioOutputDevice)
        }
        guildChoiceBox.converter = GuildViewDataConverter()
        guildChoiceBox.setOnAction {
            if (isUpdatingGuildList) {
                return@setOnAction
            }
            val selectedGuild = guildChoiceBox.value ?: return@setOnAction
            teamViewDataController.updateSelectedGuild(selectedGuild)
        }
        voiceChannelChoiceBox.converter = VoiceChannelViewDataConverter()
        voiceChannelChoiceBox.setOnAction {
            if (isUpdatingVoiceChannelList) {
                return@setOnAction
            }
            val selectedVoiceChannel = voiceChannelChoiceBox.value ?: return@setOnAction
            teamViewDataController.updateSelectedVoiceChannel(selectedVoiceChannel)
        }
    }

    fun setup(teamViewDataController: TeamViewDataController) {
        this.teamViewDataController = teamViewDataController

        player1Controller.setup(teamViewDataController.getPlayerViewDataController(PlayerSlot.SLOT_1))
        player2Controller.setup(teamViewDataController.getPlayerViewDataController(PlayerSlot.SLOT_2))
        player3Controller.setup(teamViewDataController.getPlayerViewDataController(PlayerSlot.SLOT_3))
        player4Controller.setup(teamViewDataController.getPlayerViewDataController(PlayerSlot.SLOT_4))

        teamViewDataController.audioStatusViewData.subscribeWithMainThread(::updateAudioStatusViewData)
        teamViewDataController.botStatusViewData.subscribeWithMainThread(::updateBotStatusViewData)
        teamViewDataController.audioOutputDeviceListViewData.subscribeWithMainThread(::updateAudioOutputDeviceListViewData)
        teamViewDataController.guildListViewData.subscribeWithMainThread(::updateGuildListViewData)
        teamViewDataController.voiceChannelListViewData.subscribeWithMainThread(::updateVoiceChannelListViewData)
    }

    private fun updateAudioStatusViewData(audioStatusViewData: AudioStatusViewData) {
        logger.debug("Updating audio status (status = ${audioStatusViewData.status})")
        audioStatusText.text = audioStatusViewData.status
    }

    private fun updateBotStatusViewData(botStatusViewData: BotStatusViewData) {
        logger.debug("Updating bot status (status = ${botStatusViewData.status})")
        botStatusText.text = botStatusViewData.status
    }

    private fun updateAudioOutputDeviceListViewData(audioOutputDeviceListViewData: AudioOutputDeviceListViewData) {
        logger.debug("Updating audio output device list (count = ${audioOutputDeviceListViewData.audioOutputDeviceList.size}, selected = ${audioOutputDeviceListViewData.selectedAudioOutputDevice})")
        isUpdatingAudioOutputDeviceList = true
        val items = FXCollections.observableArrayList(audioOutputDeviceListViewData.audioOutputDeviceList)
        audioOutputDeviceChoiceBox.items = items
        audioOutputDeviceChoiceBox.value = audioOutputDeviceListViewData.selectedAudioOutputDevice
        isUpdatingAudioOutputDeviceList = false
    }

    private fun updateGuildListViewData(guildListViewData: GuildListViewData) {
        logger.debug("Updating guild list (count = ${guildListViewData.guildList.size}, selected = ${guildListViewData.selectedGuild})")
        isUpdatingGuildList = true
        val items = FXCollections.observableArrayList(guildListViewData.guildList)
        guildChoiceBox.items = items
        guildChoiceBox.value = guildListViewData.selectedGuild
        isUpdatingGuildList = false
    }

    private fun updateVoiceChannelListViewData(voiceChannelListViewData: VoiceChannelListViewData) {
        logger.debug("Updating voice channel list (count = ${voiceChannelListViewData.voiceChannelList.size}, selected = ${voiceChannelListViewData.selectedVoiceChannel})")
        isUpdatingVoiceChannelList = true
        val items = FXCollections.observableArrayList(voiceChannelListViewData.voiceChannelList)
        voiceChannelChoiceBox.items = items
        voiceChannelChoiceBox.value = voiceChannelListViewData.selectedVoiceChannel
        isUpdatingVoiceChannelList = false
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TeamViewController::class.java)
    }
}
