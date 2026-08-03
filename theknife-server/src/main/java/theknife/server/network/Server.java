package theknife.server.network;

import theknife.server.ConfigurazioneDB;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.IOException;
import theknife.server.service.RistoranteService;
import theknife.server.service.SessionManager;
import theknife.server.handler.CommandFactory;
import theknife.server.handler.CommandDispatcher;

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
            
            RistoranteService ristoranteService = new RistoranteService();
            SessionManager sessionManager = new SessionManager();
            CommandFactory commandFactory = new CommandFactory(ristoranteService);
            CommandDispatcher dispatcher = new CommandDispatcher(commandFactory, sessionManager);

            ExecutorService pool = Executors.newFixedThreadPool(20);

            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, dispatcher);
                pool.execute(handler);
            }
        
        } catch (IOException e) {
            System.out.println("Impossibile aprire la porta " + PORTA_SERVER + ": " + e.getMessage());
        }
    }

}
