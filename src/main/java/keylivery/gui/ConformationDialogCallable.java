package keylivery.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;
import java.util.concurrent.Callable;

public class ConformationDialogCallable implements Callable<Boolean> {

    private String secret;

    public ConformationDialogCallable(String secret) {
        this.secret = secret;
    }

    @Override
    public Boolean call() throws Exception {
        // Alert Box for sentence conformation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(secret);
        alert.setContentText("Are they identical?");
        ButtonType buttonTypeIdentical = new ButtonType("Identical");
        ButtonType buttonTypeCancel = new ButtonType("Not Identical", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeIdentical, buttonTypeCancel);

        boolean comparisonResult;
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == buttonTypeIdentical;
    }
}
