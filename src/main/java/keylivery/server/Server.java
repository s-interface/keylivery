package keylivery.server;

import com.cryptolib.SecureDataSocket;
import com.cryptolib.UnverifiedException;

import java.io.IOException;

public class Server {

    private SecureDataSocket socket;
    private int port;
    private String connectionString;

    public Server(int port) {
        this.port = port;
    }

    public String create() {
        try {
            socket = new SecureDataSocket(port);
            connectionString = socket.prepareServerWithClientCamera();
            System.out.println("New Server: " + connectionString);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectionString;

    }

    public void start() {
        try {
            socket.setupServerWithClientCamera();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendString(String string) {
        try {
            socket.write(string.getBytes());

        } catch (UnverifiedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void stop() {
        socket.close();
    }
}
