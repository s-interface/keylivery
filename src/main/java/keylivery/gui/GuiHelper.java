package keylivery.gui;

import javafx.scene.control.Alert;

public class GuiHelper {

    public static void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("test alert");
        alert.setHeaderText("Information Alert");
        alert.setContentText(content);
        alert.show();
    }
}
