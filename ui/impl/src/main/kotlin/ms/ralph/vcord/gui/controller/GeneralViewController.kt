package ms.ralph.vcord.gui.controller

import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ChoiceBox
import javafx.scene.text.Text
import ms.ralph.vcord.gui.GeneralViewDataController
import ms.ralph.vcord.gui.converter.MidiInputDeviceViewDataConverter
import ms.ralph.vcord.gui.model.MidiInputDeviceListViewData
import ms.ralph.vcord.gui.model.MidiInputDeviceViewData
import ms.ralph.vcord.gui.model.MidiStatusViewData
import ms.ralph.vcord.gui.util.subscribeWithMainThread
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.ResourceBundle

class GeneralViewController : Initializable {

    @FXML
    private lateinit var midiStatusText: Text

    @FXML
    private lateinit var midiInputDeviceChoiceBox: ChoiceBox<MidiInputDeviceViewData>

    private lateinit var generalViewDataController: GeneralViewDataController

    private var isUpdatingMidiInputDeviceList: Boolean = false

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        midiInputDeviceChoiceBox.converter = MidiInputDeviceViewDataConverter()

        midiInputDeviceChoiceBox.setOnAction {
            if (isUpdatingMidiInputDeviceList) {
                return@setOnAction
            }
            val selectedMidiInputDevice = midiInputDeviceChoiceBox.value ?: return@setOnAction
            generalViewDataController.updateSelectedMidiInputDevice(selectedMidiInputDevice)
        }
    }

    fun setup(generalViewDataController: GeneralViewDataController) {
        this.generalViewDataController = generalViewDataController

        generalViewDataController.midiStatusViewData.subscribeWithMainThread(::updateMidiStatusViewData)
        generalViewDataController.midiInputDeviceListViewData
            .subscribeWithMainThread(::updateMidiInputDeviceListViewData)
    }

    private fun updateMidiStatusViewData(midiStatusViewData: MidiStatusViewData) {
        logger.debug("Updating midi status (status = ${midiStatusViewData.status})")
        midiStatusText.text = midiStatusViewData.status
    }


    private fun updateMidiInputDeviceListViewData(midiInputDeviceListViewData: MidiInputDeviceListViewData) {
        logger.debug(
            "Updating midi input device list (count = ${midiInputDeviceListViewData.midiInputDeviceList.size}," +
                " selected = ${midiInputDeviceListViewData.selectedMidiInputDevice})"
        )
        isUpdatingMidiInputDeviceList = true
        val items = FXCollections.observableArrayList(midiInputDeviceListViewData.midiInputDeviceList)
        midiInputDeviceChoiceBox.items = items
        midiInputDeviceChoiceBox.value = midiInputDeviceListViewData.selectedMidiInputDevice
        isUpdatingMidiInputDeviceList = false
    }

    companion object {
        private val logger = LoggerFactory.getLogger(GeneralViewController::class.java)
    }
}
