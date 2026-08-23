package theknife.server.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import theknife.server.ConfigurazioneDB;
import theknife.server.dao.DatabaseManager;
import theknife.server.dao.PreferitoDAO;
import theknife.server.dao.RecensioneDAO;
import theknife.server.dao.RistoranteDAO;
import theknife.server.dao.ServizioDAO;
import theknife.server.dao.UtenteDAO;
import theknife.server.external.GeocodingClient;
import theknife.server.external.LocalizzazioneIpClient;
import theknife.server.handler.CommandDispatcher;
import theknife.server.handler.CommandFactory;
import theknife.server.service.CucinaTranslationService;
import theknife.server.service.RecensioneService;
import theknife.server.service.RistoranteService;
import theknife.server.service.SessionManager;
import theknife.server.service.UtenteService;

/**
 * Gestisce il ciclo di vita della rete del server: verifica la connessione al
 * database, costruisce l'intera catena DAO - service - Command, apre il socket
 * di ascolto e affida ogni client a un thread del pool.
 * <p>
 * Il cablaggio avviene qui e in nessun altro punto: DAO, service e factory
 * sono costruiti una volta sola all'avvio e condivisi da tutti i thread
 * client. Sono tutti privi di stato mutabile - lo stato di sessione vive nel
 * {@link SessionManager}, che e' thread-safe - quindi la condivisione non
 * richiede sincronizzazione.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class Server {

    /** Numero massimo di client serviti contemporaneamente. */
    private static final int THREAD_MASSIMI = 20;

    /** Tentativi concessi per la connessione al database prima di rinunciare. */
    private static final int TENTATIVI_DB = 3;

    /** Parametri di connessione al database, letti da terminale all'avvio. */
    private final ConfigurazioneDB config;

    /** Porta su cui il server accetta le connessioni dei client. */
    private final int porta;

    /**
     * Costruisce il server sui parametri indicati, senza aprire nulla: la
     * connessione al database e il socket di ascolto nascono in
     * {@link #avvia()}.
     *
     * @param config i parametri di connessione al database
     * @param porta  la porta di ascolto del server
     */
    public Server (ConfigurazioneDB config, int porta) {
        this.config = config;
        this.porta = porta;
    }

    /**
     * Avvia il server: connette il database, costruisce i componenti applicativi
     * e resta in ascolto finche' il processo non viene terminato. Se il
     * database non risponde il socket non viene nemmeno aperto, cosi' i client
     * ricevono un rifiuto immediato invece di errori su ogni richiesta.
     */
    public void avvia() {
        DatabaseManager db = collegaDatabase();
        if (db == null) {
            return;
        }

        CommandDispatcher dispatcher = costruisciApplicazione(db);

        ExecutorService pool = Executors.newFixedThreadPool(THREAD_MASSIMI);

        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            System.out.println("Server in ascolto sulla porta " + porta);

            while (true) {
                Socket socket = serverSocket.accept();
                pool.execute(new ClientHandler(socket, dispatcher));
            }

        } catch (IOException e) {
            System.out.println("Impossibile aprire la porta " + porta + ": " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    /**
     * Costruisce la catena applicativa completa a partire dalla connessione al
     * database: un DAO per tabella, i tre service, la factory dei Command e il
     * dispatcher che li instrada.
     *
     * @param db il gestore di connessioni gia' verificato
     * @return il dispatcher pronto a servire le richieste
     */
    private CommandDispatcher costruisciApplicazione(DatabaseManager db) {
        RistoranteDAO ristoranteDAO = new RistoranteDAO(db);
        PreferitoDAO preferitoDAO = new PreferitoDAO(db);
        RecensioneDAO recensioneDAO = new RecensioneDAO(db);
        ServizioDAO servizioDAO = new ServizioDAO(db);
        UtenteDAO utenteDAO = new UtenteDAO(db);

        GeocodingClient geocoding = new GeocodingClient();
        LocalizzazioneIpClient localizzazione = new LocalizzazioneIpClient();
        CucinaTranslationService cucinaTranslation = new CucinaTranslationService();

        SessionManager sessionManager = new SessionManager();

        RistoranteService ristoranteService = new RistoranteService(
                ristoranteDAO, preferitoDAO, servizioDAO, geocoding, localizzazione, cucinaTranslation);
        RecensioneService recensioneService = new RecensioneService(recensioneDAO);
        UtenteService utenteService = new UtenteService(utenteDAO, sessionManager);

        CommandFactory commandFactory = new CommandFactory(
                ristoranteService, recensioneService, utenteService);

        return new CommandDispatcher(commandFactory, sessionManager);
    }

    /**
     * Verifica la connessione al database prima di aprire il socket, con al
     * massimo tre tentativi: a ogni fallimento spiega la causa e richiede le
     * credenziali da terminale.
     *
     * @return il gestore di connessioni verificato, oppure <code>null</code> se
     *         tutti i tentativi falliscono
     */
    private DatabaseManager collegaDatabase() {
        ConfigurazioneDB configAttiva = config;

        for (int tentativo = 1; tentativo <= TENTATIVI_DB; tentativo++) {
            DatabaseManager candidato = new DatabaseManager(configAttiva);

            try (Connection conn = candidato.getConnection()) {
                System.out.println("Connessione al database riuscita.");
                return candidato;
            } catch (SQLException e) {
                System.out.println(spiega(e));
                if (tentativo < TENTATIVI_DB) {
                    System.out.println("Tentativo " +  (tentativo + 1) + " di " + TENTATIVI_DB + ".");
                    configAttiva = ConfigurazioneDB.leggiDaTerminale();
                }
            }
        }

        System.out.println("Connessione al database non riuscita dopo "
            + TENTATIVI_DB + " tentativi. Server non avviato.");
        return null;
    }

    /**
     * Traduce l'errore SQL in un messaggio che indica cosa correggere.
     *
     * @param e l'eccezione sollevata dal driver
     * @return la spiegazione da mostrare a chi avvia il server
     */
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
