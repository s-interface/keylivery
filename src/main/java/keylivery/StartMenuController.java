package keylivery;

import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import keylivery.gnupg.GnuPG;
import keylivery.gnupg.GnuPGKeyID;
import keylivery.gnupg.GnuPGProcessCaller;
import keylivery.qrcode.QRCanvas;
import keylivery.qrcode.QREncoder;
import keylivery.server.ServerService;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


public class StartMenuController implements Initializable {

    private GnuPGKeyID selectedKey;
    private Service<Void> serverThread;
    private String codeText = "";
    private GnuPG gpg;

    @FXML
    private Label keyLabel;
    @FXML
    private Button showQRCode;
    @FXML
    private Button selectKeyButton;
    @FXML
    private Button cancelQR;
    @FXML
    private QRCanvas actionQRCanvas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gpg = new GnuPGProcessCaller();
    }

    public void selectKeyButton(ActionEvent actionEvent) {
        GnuPGKeyID[] keys = new GnuPGKeyID[0];
        try {
            keys = gpg.listKeys();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> keyChoices = new ArrayList<>();
        for (GnuPGKeyID key : keys
                ) {
            keyChoices.add(key.getCreationDate() + " / " + key.getUserID());
            System.out.println(key.getCreationDate() + " / " + key.getUserID() + " / " + key.getKeyID());
        }

        ChoiceDialog<GnuPGKeyID> dialog = new ChoiceDialog<>(null, keys);
        dialog.setTitle("Key Select Dialog");
        dialog.setHeaderText("Here you may choose your private key for export");
        dialog.setContentText("Select key:");

        Optional<GnuPGKeyID> result = dialog.showAndWait();

        if (result.isPresent()) {
            selectedKey = result.get();
            keyLabel.setText(selectedKey.getUserID());
        }
    }

    public void showQRCodeButton(ActionEvent actionEvent) {
        System.out.println("BUTTON PRESS");
        if (selectedKey == null) {
            showAlert("No Key selected!");
            return;
        }
        showQRCode.setDisable(true);
        selectKeyButton.setDisable(true);

        System.out.println("START pressed");

        String keyString = getKeyString(selectedKey);

        serverThread = new ServerService(keyString);

        codeText = ((ServerService) serverThread).getConnectionString();

        QREncoder encoder = new QREncoder(codeText, 250);

        actionQRCanvas.init(encoder.encode());
        actionQRCanvas.drawQR();
        cancelQR.setVisible(true);


        serverThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("SERVER DONE!");
//                lblInfo.textProperty().unbind();
//                lblInfo.setText("Success: Server Thread");
                actionQRCanvas.clear();
                showQRCode.setDisable(false);
                selectKeyButton.setDisable(false);
                cancelQR.setVisible(false);
            }
        });

        serverThread.setOnCancelled(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("SERVER WAS CANCELED!");
                actionQRCanvas.clear();
                showQRCode.setDisable(false);
                selectKeyButton.setDisable(false);
                cancelQR.setVisible(false);
            }
        });

//        lblInfo.textProperty().bind(serverThread.messageProperty());

        serverThread.restart();
    }

    private String getKeyString(GnuPGKeyID selectedKey) {
        String keyBlock = null;
        try {
            keyBlock = gpg.exportKeyAsString(selectedKey.getKeyID());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keyBlock;
    }

    public void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("test alert");
        alert.setHeaderText("Information Alert");
        alert.setContentText(content);
        alert.show();
    }

    public void cancelQRCodeButton(ActionEvent actionEvent) {
        if (serverThread.isRunning()) {
            System.out.println("CANCEL serverThread:" + serverThread.cancel());
        }
    }

    public void copyToClipboard(ActionEvent actionEvent) {
        System.out.println("copy to clipboard: " + codeText);
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(codeText);
        clipboard.setContent(content);
    }
}
