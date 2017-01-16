package keylivery;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import keylivery.qrcode.QRCanvas;
import keylivery.qrcode.QREncoder;

public class Controller {

    @FXML
    private QRCanvas actionCanvas;
    @FXML
    private TextField inputTextField;

    public void handleSubmitButtonAction(ActionEvent actionEvent) {

        String codeText = inputTextField.getText();
        if (codeText.equals("")) {
            codeText = "Ganz langer Text mit gaaaanz viel Buchstaben für ein gaaaanz großen QR Code mit gaaaaanz viel Informationen. abcdefghijklmnopqrstuvw";
        }

//        String codeText = "https://github.com/open-keychain/";
//        String codeText = "Ganz langer Text mit gaaaanz viel Buchstaben für ein gaaaanz großen QR Code mit gaaaaanz viel Informationen. abcdefghijklmnopqrstuvw";

        System.out.print(codeText);
        QREncoder encoder = new QREncoder(codeText,250);

        actionCanvas.init(encoder.encode());
        actionCanvas.drawQR();

    }
}
