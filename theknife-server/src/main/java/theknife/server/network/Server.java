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
import java.sql.Connection;
import java.sql.SQLException;
import theknife.server.dao.DatabaseManager;

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
            DatabaseManager db = collegaDatabase();
            if (db == null) {
                return;
            }
            
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

    private DatabaseManager collegaDatabase() {
        ConfigurazioneDB configAttiva = config;

        for (int tentativo = 1; tentativo <= 3; tentativo++) {
            DatabaseManager candidato = new DatabaseManager(configAttiva);

            try (Connection conn = candidato.getConnection()) {
                System.out.println("Connessione al database riuscita.");
                return candidato;
            } catch (SQLException e) {
                System.out.println(spiega(e));
                if (tentativo < 3) {
                    System.out.println("Tentativo " +  (tentativo + 1) + " di 3.");
                    configAttiva = ConfigurazioneDB.leggiDaTerminale();
                }
            }
        }

        System.out.println("Connessione al database non riuscita dopo 3 tentativi. Server non avviato.");
        return null;
    }

    private String spiega(SQLException e) {
        String stato = e.getSQLState();
        
        if ("28P01".equals(stato)) {
            return "Password errata per l'utente indicato.";
        }

        if ("28000".equals(stato)) {
            return "Utente non autorizzato a connettersi.";
        }

        if ("3D000".equals(stato)) {
            return "Il database " + ConfigurazioneDB.getNomeDb() + " non esiste.";
        }

        if ("08001".equals(stato)) {
            return "Host non raggiungibile. Verificare se PostgreSQL è avviato.";
        }

        return "Errore di connessione: " + e.getMessage();
    }

}
