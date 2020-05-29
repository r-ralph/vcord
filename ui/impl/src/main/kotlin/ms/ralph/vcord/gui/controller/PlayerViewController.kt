package ms.ralph.vcord.gui.controller

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.Slider
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.scene.shape.Circle
import javafx.scene.text.Text
import ms.ralph.vcord.gui.PlayerViewDataController
import ms.ralph.vcord.gui.model.UserViewData
import ms.ralph.vcord.gui.util.subscribeWithMainThread
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.ResourceBundle

class PlayerViewController : Initializable {

    @FXML
    private lateinit var volumeSlider: Slider

    @FXML
    private lateinit var playerIconContainer: StackPane

    @FXML
    private lateinit var playerIcon: ImageView

    @FXML
    private lateinit var addPlayerOverlay: StackPane

    @FXML
    private lateinit var playerNameText: Text

    @FXML
    private lateinit var indicator: StackPane

    private lateinit var playerViewDataController: PlayerViewDataController

    private val menu = ContextMenu()

    private var isUpdatingVolumeSlider: Boolean = false

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        playerIcon.clip = Circle(playerIcon.fitWidth / 2.0, playerIcon.fitWidth / 2.0, playerIcon.fitWidth / 2.0)

        playerIconContainer.setOnMouseClicked {
            menu.show(playerIconContainer, it.screenX, it.screenY)
        }
        volumeSlider.valueProperty().addListener { _, _, newValue ->
            if (!isUpdatingVolumeSlider) {
                playerViewDataController.updateVolume(newValue.toDouble())
            }
        }
    }

    fun setup(playerViewDataController: PlayerViewDataController) {
        this.playerViewDataController = playerViewDataController

        playerViewDataController.userListViewData.subscribeWithMainThread {
            updateUserList(it.userList, it.selectedUser)
        }
        playerViewDataController.speakingStatusViewData.subscribeWithMainThread(::updateSpeakingStatus)
        playerViewDataController.volumeViewData.subscribeWithMainThread(::updateVolume)
    }

    private fun updateUserList(userList: List<UserViewData>, selectedUser: UserViewData) {
        logger.debug("Updating player user list (count = ${userList.size}, selected = ${selectedUser})")
        menu.items.clear()
        menu.items.addAll(userList.map(::createMenuItem))
        playerNameText.text = selectedUser.getDisplayText()
        addPlayerOverlay.isVisible = selectedUser is UserViewData.Invalid
        playerIcon.image = when (selectedUser) {
            is UserViewData.Actual -> Image(selectedUser.iconUrl, true)
            UserViewData.Invalid -> null
        }
    }

    private fun updateSpeakingStatus(speaking: Boolean) {
        indicator.isVisible = speaking
    }

    private fun updateVolume(volume: Double) {
        isUpdatingVolumeSlider = true
        volumeSlider.value = volume.clamp(0.0, 2.0)
        isUpdatingVolumeSlider = false
    }

    private fun createMenuItem(user: UserViewData): MenuItem = MenuItem(user.getDisplayText()).apply {
        setOnAction {
            playerViewDataController.updateSelectedUser(user)
        }
    }

    private fun Double.clamp(min: Double, max: Double): Double = when {
        this < min -> min
        this > max -> max
        else -> this
    }

    private fun UserViewData.getDisplayText(): String = when (this) {
        is UserViewData.Actual -> name
        UserViewData.Invalid -> ""
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PlayerViewController::class.java)
    }
}
