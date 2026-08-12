package theknife.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import theknife.common.dto.RistoranteDTO;
import theknife.server.exception.DataAccessException;

/**
 * DAO di accesso alla tabella ponte <code>Preferiti</code> (associazione
 * cliente-ristorante). Non contiene logica di dominio: scrive e legge le
 * associazioni senza validarle.
 * <p>
 * La tabella <code>Preferiti</code> ha chiave primaria composta
 * <code>(id_cliente, id_ristorante)</code>: aggiunta e rimozione agiscono
 * sulla coppia, mentre la lettura restituisce i ristoranti preferiti del
 * cliente aggregandoli in {@link RistoranteDTO}.
 * <p>
 * Ogni metodo apre una connessione nuova tramite {@link DatabaseManager}, la
 * usa con <code>try-with-resources</code> e la chiude da solo. Le
 * {@link SQLException} vengono catturate e riavvolte in
 * {@link DataAccessException}.
 *
 * @author Scolaro Gabriele, 760123, VA
 */

public class PreferitoDAO {

    private final DatabaseManager db;

    /**
     * Crea il DAO legato al gestore delle connessioni fornito.
     *
     * @param db il gestore delle connessioni al database
     */
    public PreferitoDAO (DatabaseManager db) {
        this.db = db;
    }

    /**
     * Aggiunge un ristorante ai preferiti di un cliente. L'operazione e'
     * idempotente: se la coppia (cliente, ristorante) esiste gia', il comando
     * <code>ON CONFLICT DO NOTHING</code> la lascia invariata senza errore.
     *
     * @param idCliente    l'identificativo del cliente
     * @param idRistorante l'identificativo del ristorante
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public void aggiungi (long idCliente, long idRistorante) throws DataAccessException {
        String sql = "INSERT INTO Preferiti (id_cliente, id_ristorante) VALUES (?, ?) "
           + "ON CONFLICT DO NOTHING";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idCliente);
            ps.setLong(2, idRistorante);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Errore nell'aggiunta del preferito: " + e.getMessage());
        }
    }

    /**
     * Rimuove un ristorante dai preferiti di un cliente.
     *
     * @param idCliente    l'identificativo del cliente
     * @param idRistorante l'identificativo del ristorante
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public void rimuovi (long idCliente, long idRistorante) throws DataAccessException {
        String sql = "DELETE FROM Preferiti WHERE id_cliente = ? AND id_ristorante = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idCliente);
            ps.setLong(2, idRistorante);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Errore nella rimozione del preferito: " + e.getMessage());
        }
    }

    /**
     * Recupera i ristoranti preferiti di un cliente, ordinati per nome.
     * <p>
     * la query <code>visualizzaPreferiti</code>
     * di <code>Queries.sql</code> seleziona un sottoinsieme di colonne non
     * sufficiente a comporre il {@link RistoranteDTO} completo; qui la proiezione
     * e' estesa a tutte le colonne necessarie.
     *
     * @param idCliente l'identificativo del cliente
     * @return la lista dei ristoranti preferiti, eventualmente vuota
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public List<RistoranteDTO> trovaPerCliente (long idCliente) throws DataAccessException {
        String sql = "SELECT r.id_ristorante, r.nome, r.nazione, r.citta, r.provincia, r.indirizzo, "
                   + "r.latitudine, r.longitudine, r.fascia_prezzo, r.prenotazione_online, "
                   + "r.consegna_a_domicilio, r.tipo_cucina, r.telefono, r.website, r.premi, "
                   + "r.id_gestore, COALESCE(AVG(rec.stelle), 0) AS media_stelle, "
                   + "COUNT(rec.id_recensione) AS numero_recensioni "
                   + "FROM Preferiti p "
                   + "JOIN RistorantiTheKnife r ON p.id_ristorante = r.id_ristorante "
                   + "LEFT JOIN Recensioni rec ON r.id_ristorante = rec.id_ristorante "
                   + "WHERE p.id_cliente = ? "
                   + "GROUP BY r.id_ristorante ORDER BY r.nome";
        List<RistoranteDTO> risultato = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long valore = rs.getLong("id_gestore");
                    Long idGestore = rs.wasNull() ? null : valore;
                    risultato.add(new RistoranteDTO(
                        rs.getLong("id_ristorante"),
                        rs.getString("nome"),
                        rs.getString("indirizzo"),
                        rs.getString("citta"),
                        rs.getString("provincia"),
                        rs.getString("nazione"),
                        rs.getDouble("latitudine"),
                        rs.getDouble("longitudine"),
                        rs.getInt("fascia_prezzo"),
                        rs.getBoolean("prenotazione_online"),
                        rs.getBoolean("consegna_a_domicilio"),
                        rs.getString("tipo_cucina"),
                        rs.getString("website"),
                        rs.getString("telefono"),
                        rs.getString("premi"),
                        null,
                        rs.getDouble("media_stelle"),
                        rs.getInt("numero_recensioni"),
                        idGestore));
                }
            }
            new RistoranteDAO(db).popolaServizi(risultato);
            return risultato;
        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero dei preferiti del cliente: "
                + e.getMessage());
        }
    }

    private void popolaServizi (List<RistoranteDTO> ristoranti) throws DataAccessException {
        if (ristoranti == null || ristoranti.isEmpty()) {
            return;
        }
        String sql = "SELECT rs.id_ristorante, s.id, s.nome "
                   + "FROM RistoranteServizio rs "
                   + "JOIN Servizio s ON rs.id_servizio = s.id "
                   + "WHERE rs.id_ristorante IN (" + segnaposto(ristoranti.size()) + ") "
                   + "ORDER BY s.nome";
        Map<Long, List<ServizioDTO>> serviziPerRistorante = new HashMap<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < ristoranti.size(); i++) {
                ps.setLong(i + 1, ristoranti.get(i).getIdRistorante());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long idRistorante = rs.getLong("id_ristorante");
                    List<ServizioDTO> lista =
                        serviziPerRistorante.computeIfAbsent(idRistorante, k -> new ArrayList<>());
                    lista.add(new ServizioDTO(rs.getLong("id"), rs.getString("nome")));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero dei servizi dei preferiti: "
                + e.getMessage());
        }
        for (RistoranteDTO r : ristoranti) {
            r.setServizi(serviziPerRistorante.getOrDefault(r.getIdRistorante(), new ArrayList<>()));
        }
    }




}