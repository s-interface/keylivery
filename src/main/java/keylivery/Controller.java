package keylivery;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import keylivery.qrcode.QRCanvas;
import keylivery.qrcode.QREncoder;
import keylivery.server.KeyString;
import keylivery.server.Server;

public class Controller {

    Boolean buttonBlocked = false;
    String codeText = "";

    @FXML
    private QRCanvas actionCanvas;
    @FXML
    private TextField inputTextField;
    @FXML
    private Label lblInfo;

    private Service<Void> serverThread;

    public void handleSubmitButtonAction(ActionEvent actionEvent) {

        String codeText = inputTextField.getText();
        if (codeText.equals("")) {
            codeText = "Ganz langer Text mit gaaaanz viel Buchstaben für ein gaaaanz großen QR Code mit gaaaaanz viel Informationen. abcdefghijklmnopqrstuvw";
        }

//        String codeText = "https://github.com/open-keychain/";
//        String codeText = "Ganz langer Text mit gaaaanz viel Buchstaben für ein gaaaanz großen QR Code mit gaaaaanz viel Informationen. abcdefghijklmnopqrstuvw";

        System.out.println(codeText);
        QREncoder encoder = new QREncoder(codeText,250);

        actionCanvas.init(encoder.encode());
        actionCanvas.drawQR();

    }

    public void buttonStartAction(ActionEvent actionEvent) {
        if (buttonBlocked) {
            System.out.println("Button blocked");
            return;
        }
        buttonBlocked = true;
        System.out.println("START pressed");

        Server server = new Server(4711);
        codeText = server.create();

        QREncoder encoder = new QREncoder(codeText, 250);

        actionCanvas.init(encoder.encode());
        actionCanvas.drawQR();

        serverThread = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {

                        updateMessage("Running: Server Thread");

                        server.start();
                        String key = KeyString.key;
                        server.sendString(key);
                        server.stop();

                        return null;
                    }
                };
            }
        };

        serverThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("SERVER DONE!");
                lblInfo.textProperty().unbind();
                lblInfo.setText("Success: Server Thread");
                buttonBlocked = false;
                actionCanvas.clear();
            }
        });

        lblInfo.textProperty().bind(serverThread.messageProperty());

        serverThread.restart();


    }

    public void buttonCopyClipboard(ActionEvent actionEvent) {
        System.out.println("copy to clipboard: " + codeText);
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(codeText);
        clipboard.setContent(content);
    }
}
