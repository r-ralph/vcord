package ms.ralph.vcord.gui

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import ms.ralph.vcord.gui.controller.MainViewController

class VcordWindow : Application() {
    override fun start(primaryStage: Stage) {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/fxml/main.fxml"))
        primaryStage.title = "Vcord"
        primaryStage.scene = Scene(fxmlLoader.load<Pane>())
        primaryStage.sizeToScene()
        primaryStage.isResizable = false
        primaryStage.show()
    }

    companion object {
        fun launch() {
            launch(VcordWindow::class.java)
        }
    }
}
