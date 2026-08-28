package theknife.server.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import theknife.server.ConfigurazioneDB;

/**
 * Fornisce le connessioni JDBC al database dbTK. Costruisce l'URL a partire
 * dalla configurazione letta all'avvio e apre una connessione nuova a ogni
 * richiesta: una Connection non e' thread-safe e i thread del pool operano in
 * parallelo, quindi non puo' essere condivisa. Chi ottiene la connessione la
 * chiude, con try-with-resources.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class DatabaseManager {

    /** Parametri di connessione letti all'avvio del server. */
    private final ConfigurazioneDB config;

    /**
     * Costruisce il gestore sulla configurazione indicata.
     *
     * @param config i parametri di connessione al database
     */
    public DatabaseManager (ConfigurazioneDB config) {
        this.config = config;
    }

    /**
     * Apre una nuova connessione al database.
     *
     * @return una connessione aperta, da chiudere a cura del chiamante
     * @throws SQLException se le credenziali sono errate, l'host non e'
     *         raggiungibile o il database non esiste
     */
    public Connection getConnection() throws SQLException {

        String url = "jdbc:postgresql://" + config.getHost()
            + ":" + ConfigurazioneDB.getPortaDb()
            + "/" + ConfigurazioneDB.getNomeDb();

        return DriverManager.getConnection(url, config.getUtente(), config.getPassword());

    }

}
