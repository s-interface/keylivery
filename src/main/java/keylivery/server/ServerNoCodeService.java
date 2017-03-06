package keylivery.server;

import com.cryptolib.SecureDataSocket;
import com.cryptolib.SecureDataSocketException;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import keylivery.AppPreferences;
import keylivery.gui.ConformationDialogCallable;

import java.util.concurrent.FutureTask;

public class ServerNoCodeService extends Service {

    private String keyBlockString;
    private String connectionString;
    private SecureDataSocket socket;
    private int portNum;
    private String ipAddress;
    private String secret;

    public ServerNoCodeService(String keyBlockString) {
        this.keyBlockString = keyBlockString;
        this.portNum = AppPreferences.getInstance().getInt(AppPreferences.Preference.PORT_INT);
        this.ipAddress = SecureDataSocket.getIPAddress(true);
        this.socket = new SecureDataSocket(portNum);
    }

    @Override
    protected Task createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("SERVER: RUNNING");
                updateMessage("Running: Server Thread");

                try {
                    secret = socket.setupServerNoClientCamera();
                    System.out.println("Secret: " + secret);
                    FutureTask<Boolean> conformation = new FutureTask<>(new ConformationDialogCallable(secret));
                    Platform.runLater(conformation);
                    boolean comparisonResult = conformation.get();
                    System.out.println("Secret identical: " + comparisonResult);
                    socket.comparedPhrases(comparisonResult);
                    if (comparisonResult) {
                        socket.write(keyBlockString.getBytes());
                    }
                    socket.close();
                } catch (SecureDataSocketException e) {
                    if (!e.getMessage().contains("Socket closed")) {
                        e.printStackTrace();
                    }
                }

                return null;
            }
        };
    }

    @Override
    public boolean cancel() {
        // need something like .isClosed() --> SecureDataSocket Lib
        if (true) {
            socket.close();
            System.out.println("SERVER: Socket closed");
        }
        return super.cancel();
    }

    public int getPortNum() {
        return portNum;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
