package keylivery.server;

import com.cryptolib.SecureDataSocket;
import com.cryptolib.SecureDataSocketException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import keylivery.AppPreferences;

public class ServerService extends Service<String> {

    private String keyBlockString;
    private String connectionString;
    private SecureDataSocket socket;
    private int portNum;

    public ServerService(String keyBlockString) {
        this.keyBlockString = keyBlockString;
        this.portNum = AppPreferences.getInstance().getInt(AppPreferences.Preference.PORT_INT);
        this.socket = new SecureDataSocket(portNum);
        this.connectionString = createConnectionString();
    }

    public String getConnectionString() {
        return connectionString;
    }

    @Override
    protected Task createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // debug output:
                // System.out.println("SERVER: RUNNING");
                // updateMessage("Running: Server Thread");

                try {
                    socket.setupServerWithClientCamera();
                    socket.write(keyBlockString.getBytes());
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
        // maybe need something like .isClosed() --> SecureDataSocket Lib
        if (true) {
            socket.close();
            System.out.println("SERVER: Socket closed");
        }
        return super.cancel();
    }

    private String createConnectionString() {
        try {
            connectionString = socket.prepareServerWithClientCamera();
            // debug console out:
            String ipNum = connectionString.split(":",2)[0];
            System.out.println("Prepare Server at: " + ipNum + ":" +portNum);
        } catch (SecureDataSocketException e) {
            e.printStackTrace();
        }
        return connectionString;

    }
}
