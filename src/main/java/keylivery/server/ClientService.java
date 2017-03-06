package keylivery.server;

import com.cryptolib.SecureDataSocket;
import com.cryptolib.SecureDataSocketException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import keylivery.AppPreferences;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

public class ClientService extends Service {

    private SecureDataSocket socket;
    private int portNum;
    private String keyBlockString;
    private String secret;
    private String connectionDetails;

    public ClientService(String connectionDetails) {
        this.connectionDetails = connectionDetails;
        this.keyBlockString = "";
        this.portNum = AppPreferences.getInstance().getInt(AppPreferences.Preference.PORT_INT);
        this.socket = new SecureDataSocket(portNum);
    }

    @Override
    protected Task createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("Client: RUNNING");
                secret = socket.setupClientNoCamera(connectionDetails);
                System.out.println("secret: " + secret);
                // only for testing
                socket.comparedPhrases(true);
                byte[] test = socket.read();
                keyBlockString = new String(test, "UTF-8");
                socket.close();

                return null;
            }
        };
    }

    public String getKeyBlockString() {
        return keyBlockString;
    }

    // run only from JAVAFX thread!
    public void runClient() {
        System.out.println("Client: RUNNING");
        try {
            secret = socket.setupClientNoCamera(connectionDetails);
            System.out.println("secret: " + secret);

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
            comparisonResult = result.get() == buttonTypeIdentical;

            socket.comparedPhrases(comparisonResult);
            if (comparisonResult) {
                byte[] test = socket.read();
                keyBlockString = new String(test, "UTF-8");
            }
            socket.close();
        } catch (SecureDataSocketException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
