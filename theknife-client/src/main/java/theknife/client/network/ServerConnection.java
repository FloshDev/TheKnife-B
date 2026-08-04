package theknife.client.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import theknife.common.protocol.Request;
import theknife.common.protocol.Response;

/**
 * Gestisce la connessione socket verso il server: apre lo stream, invia le
 * Request e riceve le Response. Singleton: un'unica istanza condivisa per
 * tutta la durata dell'applicazione client, inizializzata una volta
 * all'avvio e mai chiusa durante logout (solo il sessionToken viene svuotato).
 *
 * @author Barlera Marco, 760000, VA
 */

public class ServerConnection {
    private static ServerConnection instance = null;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String sessionToken;

    private ServerConnection() {}

    public static ServerConnection getInstance() {
        if (instance == null) {
            instance = new ServerConnection();
        }
        return instance;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    public void disconnect() throws IOException {
        out.close();
        in.close();
        socket.close();
    }

    public Response inviaRichiesta(Request request) throws IOException, ClassNotFoundException {
        out.writeObject(request);
        out.flush();
        return (Response) in.readObject();
    }
    
}
