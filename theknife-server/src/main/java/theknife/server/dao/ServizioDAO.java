package theknife.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import theknife.common.dto.ServizioDTO;
import theknife.server.exception.ApplicationException;

/**
 * DAO di accesso alle tabelle <code>Servizio</code> e
 * <code>RistoranteServizio</code> (ponte N:N). Non contiene
 * logica di dominio: legge righe e le impacchetta in {@link ServizioDTO},
 * scrive i dati ricevuti senza validarli.
 * <p>
 * Ogni metodo apre una connessione nuova tramite {@link DatabaseManager}, la
 * usa con <code>try-with-resources</code> e la chiude da solo. Le
 * {@link SQLException} vengono catturate e riavvolte in
 * {@link ApplicationException}.
 *
 * @author Scolaro Gabriele, 760123, VA
 */
public class ServizioDAO {

    private final DatabaseManager db;

    /**
     * Crea il DAO legato al gestore delle connessioni fornito.
     *
     * @param db il gestore delle connessioni al database
     */
    public ServizioDAO (DatabaseManager db) {
        this.db = db;
    }

    /**
     * Restituisce l'elenco completo dei servizi disponibili, ordinati per nome.
     *
     * @return la lista dei servizi, eventualmente vuota
     * @throws ApplicationException se l'accesso al database fallisce
     */
    public List<ServizioDTO> tutti () throws ApplicationException {
        String sql = "SELECT id, nome FROM Servizio ORDER BY nome";
        List<ServizioDTO> servizi = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                servizi.add(new ServizioDTO(rs.getLong("id"), rs.getString("nome")));
            }
            return servizi;
        } catch (SQLException e) {
            throw new ApplicationException("Errore nel recupero dei servizi: " + e.getMessage());
        }
    }

    /**
     * Restituisce i servizi offerti da un ristorante.
     *
     * @param idRistorante l'identificativo del ristorante
     * @return la lista dei servizi del ristorante, eventualmente vuota
     * @throws ApplicationException se l'accesso al database fallisce
     */
    public List<ServizioDTO> trovaPerRistorante (long idRistorante) throws ApplicationException {
        String sql = "SELECT s.id, s.nome FROM Servizio s "
                   + "JOIN RistoranteServizio rs ON s.id = rs.id_servizio "
                   + "WHERE rs.id_ristorante = ? ORDER BY s.nome";
        List<ServizioDTO> servizi = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idRistorante);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    servizi.add(new ServizioDTO(rs.getLong("id"), rs.getString("nome")));
                }
            }
            return servizi;
        } catch (SQLException e) {
            throw new ApplicationException("Errore nel recupero dei servizi del ristorante: "
                + e.getMessage());
        }
    }

    /**
     * Associa a un ristorante l'elenco di servizi indicato. L'operazione
     * sostituisce i servizi preesistenti: prima li rimuove dalla tabella ponte
     * <code>RistoranteServizio</code>, poi inserisce i nuovi collegamenti. Tutto
     * viene eseguito in un'unica transazione.
     *
     * @param idRistorante l'identificativo del ristorante
     * @param servizi      i servizi da associare al ristorante
     * @throws ApplicationException se l'accesso al database fallisce
     */
    public void associa (long idRistorante, List<ServizioDTO> servizi) throws ApplicationException {
        String sqlDelete = "DELETE FROM RistoranteServizio WHERE id_ristorante = ?";
        String sqlInsert = "INSERT INTO RistoranteServizio (id_ristorante, id_servizio) VALUES (?, ?)";
        try (Connection conn = db.getConnection()) {
            try {
                conn.setAutoCommit(false);
                try (PreparedStatement psDel = conn.prepareStatement(sqlDelete)) {
                    psDel.setLong(1, idRistorante);
                    psDel.executeUpdate();
                }
                if (servizi != null) {
                    try (PreparedStatement psIns = conn.prepareStatement(sqlInsert)) {
                        for (ServizioDTO s : servizi) {
                            psIns.setLong(1, idRistorante);
                            psIns.setLong(2, s.getIdServizio());
                            psIns.addBatch();
                        }
                        psIns.executeBatch();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException rb) {
                    // il rollback fallito viene oscurato dall'eccezione originale
                }
                throw e;
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    // il ripristino del flag non altera l'esito dell'operazione
                }
            }
        } catch (SQLException e) {
            throw new ApplicationException("Errore nell'associazione dei servizi al ristorante: "
                + e.getMessage());
        }
    }
}