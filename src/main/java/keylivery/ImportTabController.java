package keylivery;

import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import keylivery.gnupg.GnuPG;
import keylivery.gnupg.GnuPGProcessCaller;
import keylivery.gui.GuiHelper;
import keylivery.server.ClientService;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ImportTabController implements Initializable {

    public Button importKeyButton;
    public TextArea keyBlockTextArea;
    @FXML
    private Button startClientButton;
    @FXML
    private CheckBox dryRunCheckBox;
    private GnuPG gpg;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gpg = new GnuPGProcessCaller();
        importKeyButton.disableProperty().bind(keyBlockTextArea.textProperty().isEmpty());
    }

    public void startClient(ActionEvent actionEvent) {
        Optional<Pair<String, String>> result = getIPAndPortDialog().showAndWait();

        result.ifPresent(ipPortPair -> {
            startClientButton.setDisable(true);
            ClientService clientService = new ClientService(ipPortPair.getKey() + ":" + ipPortPair.getValue());

            clientService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    System.out.println("Client Service success");
                    keyBlockTextArea.setText(clientService.getKeyBlockString());
                    startClientButton.setDisable(false);
                }
            });
            clientService.restart();
        });


    }

    public void importKey(ActionEvent actionEvent) {
        String keyblockStr = keyBlockTextArea.getText();
        if (gpg.importKey(keyblockStr, dryRunCheckBox.isSelected())) {
            GuiHelper.showAlert("Key Import: SUCCESS");
        }
    }

    private Dialog<Pair<String, String>> getIPAndPortDialog() {
        // http://code.makery.ch/blog/javafx-dialogs-official/
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Insert Connection Details");
        dialog.setHeaderText("Please enter IP & Port Number:");

        // Set the button types.
        ButtonType enterDataButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(enterDataButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField ipAddress = new TextField();
        ipAddress.setText("192.168.178.");
        ipAddress.setPromptText("enter IP Address");
        TextField portNumber = new TextField();
        portNumber.setText("4711");
        portNumber.setPromptText("enter port number");

        grid.add(new Label("IP Address:"), 0, 0);
        grid.add(ipAddress, 1, 0);
        grid.add(new Label("Port Number:"), 0, 1);
        grid.add(portNumber, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(enterDataButtonType);
        loginButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        ipAddress.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> ipAddress.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == enterDataButtonType) {
                return new Pair<>(ipAddress.getText(), portNumber.getText());
            }
            return null;
        });

        return dialog;
    }

}
