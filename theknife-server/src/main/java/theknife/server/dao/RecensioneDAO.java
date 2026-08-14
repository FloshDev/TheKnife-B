package theknife.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import theknife.common.dto.AggiungiRecensioneDTO;
import theknife.common.dto.ModificaRecensioneDTO;
import theknife.common.dto.RecensioneDTO;
import theknife.common.dto.RispondiRecensioneDTO;
import theknife.server.exception.ApplicationException;
import theknife.server.exception.DataAccessException;

/**
 * DAO di accesso alla tabella <code>Recensioni</code>. 
 * Non contiene logica di dominio: legge righe e le impacchetta in
 * {@link RecensioneDTO}, scrive i dati ricevuti senza validarli.
 * <p>
 * <code>ModificaRecensioneDTO</code>,
 * <code>elimina</code> e <code>rispondi</code> non trasportano l'id
 * dell'utente/gestore, quindi i controlli di autorizzazione (ownership) non
 * avvengono qui ma nel livello service. Il DAO agisce solo per id di
 * recensione.
 * <p>
 * Ogni metodo apre una connessione nuova tramite {@link DatabaseManager}, la
 * usa con <code>try-with-resources</code> e la chiude da solo. Le
 * {@link SQLException} vengono catturate e riavvolte in
 * {@link DataAccessException}.
 *
 * @author Scolaro Gabriele, 760123, VA
 */

public class RecensioneDAO {

    private final DatabaseManager db;

    /**
     * Crea il DAO legato al gestore delle connessioni fornito.
     *
     * @param db il gestore delle connessioni al database
     */
    public RecensioneDAO (DatabaseManager db) {
        this.db = db;
    }

    /**
     * Recupera tutte le recensioni di un ristorante, dalla piu' recente.
     *
     * @param idRistorante l'identificativo del ristorante
     * @return la lista delle recensioni, eventualmente vuota
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public List<RecensioneDTO> trovaPerRistorante (long idRistorante) throws DataAccessException {
        String sql = "SELECT id_recensione, id_ristorante, id_cliente, titolo, testo, stelle, "
                   + "data_pubblicazione, risposta, data_risposta "
                   + "FROM Recensioni "
                   + "WHERE id_ristorante = ? "
                   + "ORDER BY data_pubblicazione DESC";
        List<RecensioneDTO> recensioni = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idRistorante);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    recensioni.add(toDTO(rs));
                }
            }
            return recensioni;
        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero delle recensioni del ristorante: "
                + e.getMessage());
        }
    }

    /**
     * Recupera le recensioni ricevute dai ristoranti di un gestore (i suoi
     * ristoranti), dalla piu' recente.
     *
     * @param idGestore l'identificativo del gestore
     * @return la lista delle recensioni, eventualmente vuota
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public List<RecensioneDTO> trovaPerGestore (long idGestore) throws DataAccessException {
        String sql = "SELECT rec.id_recensione, rec.id_ristorante, rec.id_cliente, rec.titolo, "
                   + "rec.testo, rec.stelle, rec.data_pubblicazione, rec.risposta, rec.data_risposta "
                   + "FROM Recensioni rec "
                   + "JOIN RistorantiTheKnife r ON rec.id_ristorante = r.id_ristorante "
                   + "WHERE r.id_gestore = ? "
                   + "ORDER BY rec.data_pubblicazione DESC";
        List<RecensioneDTO> recensioni = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idGestore);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    recensioni.add(toDTO(rs));
                }
            }
            return recensioni;
        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero delle recensioni del gestore: "
                + e.getMessage());
        }
    }

    /**
     * Recupera una singola recensione per identificativo.
     *
     * @param idRecensione l'identificativo della recensione
     * @return la recensione trovata, oppure <code>null</code> se assente
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public RecensioneDTO trovaPerId (long idRecensione) throws DataAccessException {
        String sql = "SELECT id_recensione, id_ristorante, id_cliente, titolo, testo, stelle, "
                   + "data_pubblicazione, risposta, data_risposta "
                   + "FROM Recensioni WHERE id_recensione = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idRecensione);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? toDTO(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero della recensione: " + e.getMessage());
        }
    }

    /**
     * Inserisce una nuova recensione da parte di un utente (cliente).
     *
     * @param dati     i dati della recensione da inserire
     * @param idUtente l'identificativo del cliente che recensisce
     * @return l'identificativo generato per la nuova recensione
     * @throws DataAccessException se l'accesso al database fallisce
     * @throws ApplicationException se il cliente ha gia' recensito il
     *         ristorante (vincolo <code>UNIQUE (id_cliente, id_ristorante)</code>
     *         violato)
     */
    public long inserisci (AggiungiRecensioneDTO dati, long idUtente)
            throws DataAccessException, ApplicationException {
        String sql = "INSERT INTO Recensioni (id_ristorante, id_cliente, titolo, testo, stelle) "
                   + "VALUES (?, ?, ?, ?, ?) RETURNING id_recensione";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, dati.getIdRistorante());
            ps.setLong(2, idUtente);
            ps.setString(3, dati.getTitolo());
            ps.setString(4, dati.getTesto());
            ps.setInt(5, dati.getStelle());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_recensione");
                }
                throw new DataAccessException("Inserimento recensione senza id generato.");
            }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new ApplicationException("Hai gia' recensito questo ristorante.");
            }
            throw new DataAccessException("Errore nell'inserimento della recensione: "
                + e.getMessage());
        }
    }

    /**
     * Aggiorna il titolo, il testo e le stelle di una recensione. Il controllo
     * di autorizzazione (che la recensione appartenga all'utente) avviene nel
     * livello service.
     *
     * @param dati i nuovi dati della recensione
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public void aggiorna (ModificaRecensioneDTO dati) throws DataAccessException {
        String sql = "UPDATE Recensioni SET titolo = ?, testo = ?, stelle = ?, "
                   + "data_pubblicazione = CURRENT_TIMESTAMP "
                   + "WHERE id_recensione = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dati.getNuovoTitolo());
            ps.setString(2, dati.getNuovoTesto());
            ps.setInt(3, dati.getNuoveStelle());
            ps.setLong(4, dati.getIdRecensione());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Errore nella modifica della recensione: "
                + e.getMessage());
        }
    }

    /**
     * Elimina una recensione per identificativo. Il controllo di
     * autorizzazione avviene nel livello service.
     *
     * @param idRecensione l'identificativo della recensione da eliminare
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public void elimina (long idRecensione) throws DataAccessException {
        String sql = "DELETE FROM Recensioni WHERE id_recensione = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idRecensione);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Errore nell'eliminazione della recensione: "
                + e.getMessage());
        }
    }

    /**
     * Aggiunge la risposta del gestore a una recensione (massimo una risposta,
     * garantito dal vincolo <code>risposta IS NULL</code> nella query). Il
     * controllo di autorizzazione avviene nel livello service.
     *
     * @param dati la recensione da rispondere e il testo della risposta
     * @throws DataAccessException se l'accesso al database fallisce
     * @throws ApplicationException se la recensione ha gia' una risposta
     */
    public void rispondi (RispondiRecensioneDTO dati)
            throws DataAccessException, ApplicationException {
        String sql = "UPDATE Recensioni SET risposta = ?, data_risposta = CURRENT_TIMESTAMP "
                   + "WHERE id_recensione = ? AND risposta IS NULL";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dati.getRisposta());
            ps.setLong(2, dati.getIdRecensione());
            int righeAggiornate = ps.executeUpdate();
            if (righeAggiornate == 0) {
                throw new ApplicationException("Recensione gia' risposta.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nella risposta alla recensione: "
                + e.getMessage());
        }
    }

    /**
     * Trasforma la riga corrente del result set in un {@link RecensioneDTO}.
     *
     * @param rs il result set con la riga gia' posizionata
     * @return il DTO corrispondente alla riga
     * @throws SQLException se la lettura dei campi fallisce
     */
    private RecensioneDTO toDTO (ResultSet rs) throws SQLException {
        LocalDateTime dataPubblicazione = rs.getTimestamp("data_pubblicazione") != null
                ? rs.getTimestamp("data_pubblicazione").toLocalDateTime()
                : null;
        LocalDateTime dataRisposta = rs.getTimestamp("data_risposta") != null
                ? rs.getTimestamp("data_risposta").toLocalDateTime()
                : null;
        return new RecensioneDTO(
            rs.getLong("id_recensione"),
            rs.getLong("id_ristorante"),
            rs.getString("titolo"),
            rs.getString("testo"),
            rs.getInt("stelle"),
            rs.getLong("id_cliente"),
            dataPubblicazione,
            rs.getString("risposta"),
            dataRisposta);
    }
}