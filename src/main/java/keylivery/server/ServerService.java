package keylivery.server;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServerService extends Service {

    private Server server;
    private String keyBlockString;
    private String connectionString;

    public ServerService(String keyBlockString) {
        this.server = new Server(4711);
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
        try {
            if (true) {
                server.stop();
            }
        } catch (NullPointerException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return super.cancel();
    }
}
