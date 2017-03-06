package keylivery;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import keylivery.gnupg.GnuPG;
import keylivery.gnupg.GnuPGKeyID;
import keylivery.gnupg.GnuPGProcessCaller;
import keylivery.gui.GuiHelper;
import keylivery.qrcode.QRCanvas;
import keylivery.qrcode.QREncoder;
import keylivery.server.ServerNoCodeService;
import keylivery.server.ServerService;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


public class ExportTabController implements Initializable {

    private GnuPGKeyID selectedKey;
    private ServerService serverThread;
    private String codeText = "";
    private GnuPG gpg;

    @FXML
    private Button exportWithoutQRCodeButton;
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
        GnuPGKeyID[] keys = gpg.listKeys();
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
        if (selectedKey == null) {
            GuiHelper.showAlert("No Key selected!");
            return;
        }
        showQRCode.setDisable(true);
        selectKeyButton.setDisable(true);

        String keyString = gpg.exportKeyAsString(selectedKey);
        serverThread = new ServerService(keyString);

        codeText = ((ServerService) serverThread).getConnectionString();
        QREncoder encoder = new QREncoder(codeText, 250);
        actionQRCanvas.init(encoder.encode());
        actionQRCanvas.drawQR();
        cancelQR.setVisible(true);

        serverThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("SERVER: DONE!");
                actionQRCanvas.clear();
                showQRCode.setDisable(false);
                selectKeyButton.setDisable(false);
                cancelQR.setVisible(false);
            }
        });

        serverThread.setOnCancelled(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                actionQRCanvas.clear();
                showQRCode.setDisable(false);
                selectKeyButton.setDisable(false);
                cancelQR.setVisible(false);
            }
        });

        serverThread.restart();
    }

    public void cancelQRCodeButton(ActionEvent actionEvent) {
        if (serverThread.isRunning()) {
            //close server socket
            serverThread.cancel();
        }
    }

    public void copyToClipboard(ActionEvent actionEvent) {
        System.out.println("copy to clipboard: " + codeText);
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(codeText);
        clipboard.setContent(content);
    }

    public void exportWithoutQRCode(ActionEvent actionEvent) {
        if (selectedKey == null) {
            GuiHelper.showAlert("No Key selected!");
            return;
        }
        exportWithoutQRCodeButton.setDisable(true);
        showQRCode.setDisable(true);
        selectKeyButton.setDisable(true);

        String keyString = gpg.exportKeyAsString(selectedKey);
        ServerNoCodeService serverNoCode = new ServerNoCodeService(keyString);
        System.out.println(serverNoCode.getIpAddress());
        System.out.println(serverNoCode.getPortNum());

        serverNoCode.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("SERVER: DONE!");
                showQRCode.setDisable(false);
                selectKeyButton.setDisable(false);
                exportWithoutQRCodeButton.setDisable(false);
            }
        });

        serverNoCode.setOnCancelled(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                showQRCode.setDisable(false);
                selectKeyButton.setDisable(false);
                exportWithoutQRCodeButton.setDisable(false);
            }
        });

        serverNoCode.restart();
    }
}
