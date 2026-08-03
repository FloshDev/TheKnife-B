package theknife.server.network;

import theknife.server.ConfigurazioneDB;
import java.net.ServerSocket;
import java.io.IOException;

/**
 * Gestisce il ciclo di vita della rete del server: apre il socket di ascolto,
 * accetta le connessioni dei client e le affida ai thread di lavoro.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class Server {
    
    private static final int PORTA_SERVER = 9999;
    private final ConfigurazioneDB config;

    public Server (ConfigurazioneDB config) {
        this.config = config;
    }

    public void avvia() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORTA_SERVER);
            System.out.println("Server in ascolto sulla porta " + PORTA_SERVER);
            // TODO: ciclo accept()
        } catch (IOException e) {
            System.out.println("Impossibile aprire la porta " + PORTA_SERVER + ": " + e.getMessage());
        }
    }

}
