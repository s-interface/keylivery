package keylivery.server;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import keylivery.AppPreferences;

public class ServerService extends Service {

    private Server server;
    private String keyBlockString;
    private String connectionString;

    public ServerService(String keyBlockString) {
        int portNum = AppPreferences.getInstance().getInt(AppPreferences.Preference.PORT_INT);
        this.server = new Server(portNum);
        this.keyBlockString = keyBlockString;
        this.connectionString = server.create();
    }

    public String getConnectionString() {
        return connectionString;
    }

    @Override
    protected Task createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                updateMessage("Running: Server Thread");

                server.start();
                server.sendString(keyBlockString);
                server.stop();

                return null;
            }
        };
    }

    @Override
    public boolean cancel() {
        // need something like .isClosed() --> SecureDataSocket Lib
        if (true) {
            server.stop();
        }
        return super.cancel();
    }
}
