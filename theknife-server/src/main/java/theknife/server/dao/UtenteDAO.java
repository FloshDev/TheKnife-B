
package theknife.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import theknife.common.dto.RegistrazioneDTO;
import theknife.common.dto.UtenteDTO;
import theknife.common.enums.Ruolo;
import theknife.server.exception.DataAccessException;

/**
 * DAO di accesso alla tabella <code>Utenti</code>. Incapsula le query di
 * lettura e scrittura degli utenti. Non contiene alcuna logica di dominio: riceve dati gia' validi
 * e li scrive, legge righe e le impacchetta in <code>UtenteDTO</code>.
 * <p>
 * Ogni metodo apre una connessione nuova tramite {@link DatabaseManager},
 * la usa con <code>try-with-resources</code> e la chiude da solo. Qualsiasi
 * {@link SQLException} viene catturata e riavvolta in
 * {@link DataAccessException}, come previsto dal layering del server.
 *
 * @author Scolaro Gabriele, 760123, VA
 */

public class UtenteDAO {

    /**
     * Il gestore delle connessioni al database: ogni metodo chiede
     * una connessione nuova al momento dell'operazione.
     */
    private final DatabaseManager db;

    /**
     * Crea il DAO legato al gestore delle connessioni fornito.
     *
     * @param db il gestore delle connessioni al database
     */
    public UtenteDAO (DatabaseManager db) {
        this.db = db;
    }

    /**
     * Recupera l'utente a partire dal suo username.
     *
     * @param username lo username da cercare
     * @return l'utente trovato, oppure <code>null</code> se assente
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public UtenteDTO trovaPerUsername (String username) throws DataAccessException {
        String sql = "SELECT id, username, nome, cognome, email, data_nascita, domicilio, ruolo "
                   + "FROM Utenti WHERE username = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? toDTO(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero dell'utente: " + e.getMessage());
        }
    }

    /**
     * Recupera l'utente a partire dal suo identificativo.
     *
     * @param idUtente l'identificativo dell'utente
     * @return l'utente trovato, oppure <code>null</code> se assente
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public UtenteDTO trovaPerId (long idUtente) throws DataAccessException {
        String sql = "SELECT id, username, nome, cognome, email, data_nascita, domicilio, ruolo "
                   + "FROM Utenti WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? toDTO(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero dell'utente: " + e.getMessage());
        }
    }

    /**
     * Recupera soltanto l'hash della password dell'utente. L'hash non viene
     * mai inserito in {@link UtenteDTO}: quel DTO viaggia sul socket verso il
     * client e una password (anche hashata) non deve uscire dal server.
     *
     * @param username lo username dell'utente
     * @return l'hash della password, oppure <code>null</code> se l'utente non
     *         esiste
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public String trovaHashPassword (String username) throws DataAccessException {
        String sql = "SELECT password FROM Utenti WHERE username = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("password") : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero dell'hash della password: "
                + e.getMessage());
        }
    }

    /**
     * Inserisce un nuovo utente nel database.
     *
     * @param dati        i dati di registrazione dell'utente
     * @param hashPassword l'hash (BCrypt) della password, gia' calcolato dal
     *                     livello service
     * @return l'identificativo generato per il nuovo utente
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public long inserisci (RegistrazioneDTO dati, String hashPassword) throws DataAccessException {
        String sql = "INSERT INTO Utenti (username, password, nome, cognome, email, "
                   + "data_nascita, domicilio, ruolo) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dati.getUsername());
            ps.setString(2, hashPassword);
            ps.setString(3, dati.getNome());
            ps.setString(4, dati.getCognome());
            ps.setString(5, dati.getEmail());
            if (dati.getDataNascita() != null) {
                ps.setDate(6, java.sql.Date.valueOf(dati.getDataNascita()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            ps.setString(7, dati.getDomicilio());
            ps.setString(8, dati.getRuolo() != null ? dati.getRuolo().name() : null);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
                throw new DataAccessException("Inserimento utente senza id generato.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nell'inserimento dell'utente: " + e.getMessage());
        }
    }

    /**
     * Verifica se uno username e' gia' in uso.
     *
     * @param username lo username da controllare
     * @return <code>true</code> se lo username esiste gia', altrimenti
     *         <code>false</code>
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public boolean usernameEsiste (String username) throws DataAccessException {
        String sql = "SELECT 1 FROM Utenti WHERE username = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nella verifica dello username: "
                + e.getMessage());
        }
    }

    /**
     * Trasforma la riga corrente del result set in un {@link UtenteDTO}.
     *
     * @param rs il result set con la riga gia' posizionata
     * @return il DTO corrispondente alla riga
     * @throws SQLException se la lettura dei campi fallisce
     */
    private UtenteDTO toDTO (ResultSet rs) throws SQLException {
        LocalDate dataNascita = rs.getDate("data_nascita") != null
                ? rs.getDate("data_nascita").toLocalDate()
                : null;
        String ruolo = rs.getString("ruolo");
        return new UtenteDTO(
            rs.getLong("id"),
            rs.getString("username"),
            rs.getString("nome"),
            rs.getString("cognome"),
            rs.getString("email"),
            ruolo != null ? Ruolo.valueOf(ruolo) : null,
            dataNascita,
            rs.getString("domicilio"));
    }
}