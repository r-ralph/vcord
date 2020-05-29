package ms.ralph.vcord.gui.controller

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TitledPane
import ms.ralph.vcord.gui.VcordViewDataController
import java.net.URL
import java.util.ResourceBundle

class MainViewController : Initializable {

    @FXML
    private lateinit var general: TitledPane

    @FXML
    lateinit var generalController: GeneralViewController
        private set

    @FXML
    private lateinit var alpha: TitledPane

    @FXML
    lateinit var alphaController: TeamViewController
        private set

    @FXML
    private lateinit var bravo: TitledPane

    @FXML
    lateinit var bravoController: TeamViewController
        private set

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        alpha.text = "Alpha"
        bravo.text = "Bravo"

        val viewDataController = VcordViewDataController()
        generalController.setup(viewDataController.generalViewDataController)
        alphaController.setup(viewDataController.alphaTeamViewDataController)
        bravoController.setup(viewDataController.bravoTeamViewDataController)
    }
}
