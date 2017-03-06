package keylivery.server;

import com.cryptolib.SecureDataSocket;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import keylivery.AppPreferences;
import keylivery.gui.ConformationDialogCallable;

import java.util.concurrent.FutureTask;

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

                FutureTask<Boolean> conformation = new FutureTask<>(new ConformationDialogCallable(secret));
                Platform.runLater(conformation);
                boolean comparisonResult = conformation.get();

                socket.comparedPhrases(comparisonResult);
                if (comparisonResult) {
                    byte[] test = socket.read();
                    keyBlockString = new String(test, "UTF-8");
                }
                socket.close();

                return null;
            }
        };
    }

    public String getKeyBlockString() {
        return keyBlockString;
    }
}
