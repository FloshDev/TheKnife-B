package theknife.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import theknife.common.dto.AggiungiRistoranteDTO;
import theknife.common.dto.CercaRistorantiDTO;
import theknife.common.dto.PosizioneDTO;
import theknife.common.dto.RistoranteDTO;
import theknife.common.dto.ServizioDTO;
import theknife.common.dto.StatisticheRistoranteDTO;
import theknife.server.exception.DataAccessException;

/**
 * DAO di accesso alla tabella <code>RistorantiTheKnife</code> e alle tabelle
 * ad essa collegate. Non contiene logica di dominio: costruisce il filtro di ricerca
 * dagli input ricevuti, legge righe e le impacchetta in
 * {@link RistoranteDTO}.
 * <p>
 * Note di implementazione concordate nel contratto:
 * <ul>
 * <li><code>mediaStelle</code> e <code>numeroRecensioni</code> non sono
 * colonne ma aggregati su <code>Recensioni</code>: vengono calcolati con
 * <code>AVG</code>/<code>COUNT</code> e <code>LEFT JOIN</code> (con
 * <code>INNER</code> i ristoranti senza recensioni sparirebbero, ed e' la
 * maggioranza del dataset).</li>
 * <li><code>servizi</code> e' una lista proveniente dalla tabella ponte
 * N:N <code>RistoranteServizio</code>: una query con join duplicherebbe le
 * righe del ristorante, percio' i servizi vengono caricati in una singola
 * query <code>IN</code> separata e raggruppati in Java (nessun N+1).</li>
 * <li><code>fascia_prezzo</code> e' <code>SMALLINT</code>, il DTO ha
 * <code>int</code>: <code>getInt()</code> legge correttamente senza
 * conversioni.</li>
 * </ul>
 * Ogni metodo apre una connessione nuova tramite {@link DatabaseManager}, la
 * usa con <code>try-with-resources</code> e la chiude da solo. Le
 * {@link SQLException} vengono catturate e riavvolte in
 * {@link DataAccessException}.
 *
 * @author Scolaro Gabriele, 760123, VA
 */

public class RistoranteDAO {

    private final DatabaseManager db;

    /**
     * Crea il DAO legato al gestore delle connessioni fornito.
     *
     * @param db il gestore delle connessioni al database
     */
    public RistoranteDAO (DatabaseManager db) {
        this.db = db;
    }

    /**
     * Cerca i ristoranti applicando i criteri non nulli presenti nel filtro.
     * Il filtro viene costruito dinamicamente: ogni criterio compilato diventa
     * una condizione <code>WHERE</code> aggiuntiva. Tutti i ristoranti
     * restituiti includono <code>servizi</code>, <code>mediaStelle</code> e
     * <code>numeroRecensioni</code>.
     *
     * @param filtri i criteri di ricerca; quelli a <code>null</code>/vuoti
     *               vengono ignorati
     * @return la lista dei ristoranti trovati, eventualmente vuota
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public List<RistoranteDTO> cerca (CercaRistorantiDTO filtri) throws DataAccessException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT r.id_ristorante, r.nome, r.nazione, r.citta, r.provincia, r.indirizzo, ")
           .append("r.latitudine, r.longitudine, r.fascia_prezzo, r.prenotazione_online, ")
           .append("r.consegna_a_domicilio, r.tipo_cucina, r.telefono, r.website, r.premi, r.id_gestore, ")
           .append("COALESCE(AVG(rec.stelle), 0) AS media_stelle, ")
           .append("COUNT(rec.id_recensione) AS numero_recensioni ")
           .append("FROM RistorantiTheKnife r ")
           .append("LEFT JOIN Recensioni rec ON r.id_ristorante = rec.id_ristorante ");

        List<String> condizioni = new ArrayList<>();
        List<Object> parametri = new ArrayList<>();

        if (filtri.getNome() != null && !filtri.getNome().isBlank()) {
            condizioni.add("r.nome ILIKE ?");
            parametri.add("%" + filtri.getNome() + "%");
        }
        if (filtri.getCitta() != null && !filtri.getCitta().isBlank()) {
            condizioni.add("r.citta ILIKE ?");
            parametri.add("%" + filtri.getCitta() + "%");
        }
        if (filtri.getTipoCucina() != null && !filtri.getTipoCucina().isBlank()) {
            condizioni.add("r.tipo_cucina ILIKE ?");
            parametri.add("%" + filtri.getTipoCucina() + "%");
        }
        if (filtri.isPrenotazioneOnline() != null) {
            condizioni.add("r.prenotazione_online = ?");
            parametri.add(filtri.isPrenotazioneOnline());
        }
        if (filtri.getFasciaPrezzo() != 0) {
            condizioni.add("r.fascia_prezzo <= ?");
            parametri.add(filtri.getFasciaPrezzo());
        }
        if (filtri.isConsegnaADomicilio() != null) {
            condizioni.add("r.consegna_a_domicilio = ?");
            parametri.add(filtri.isConsegnaADomicilio());
        }
        if (filtri.getServizi() != null && !filtri.getServizi().isEmpty()) {
            // il ristorante offre almeno uno dei servizi richiesti (scelta "ANY")
            condizioni.add("r.id_ristorante IN (SELECT rs.id_ristorante FROM RistoranteServizio rs "
                + "WHERE rs.id_servizio IN (" + segnaposto(filtri.getServizi().size()) + "))");
            for (ServizioDTO s : filtri.getServizi()) {
                parametri.add(s.getIdServizio());
            }
        }

        if (!condizioni.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", condizioni));
        }
        sql.append(" GROUP BY r.id_ristorante ORDER BY r.nome");

        List<RistoranteDTO> risultato = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            setParametri(ps, parametri);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    risultato.add(toDTO(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nella ricerca dei ristoranti: " + e.getMessage());
        }
        popolaServizi(risultato);
        return risultato;
    }

    /**
     * Recupera il dettaglio completo di un singolo ristorante, inclusi
     * servizi, media delle stelle e numero di recensioni.
     *
     * @param idRistorante l'identificativo del ristorante
     * @return il ristorante trovato, oppure <code>null</code> se assente
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public RistoranteDTO trovaPerId (long idRistorante) throws DataAccessException {
        String sql = "SELECT r.id_ristorante, r.nome, r.nazione, r.citta, r.provincia, r.indirizzo, "
                   + "r.latitudine, r.longitudine, r.fascia_prezzo, r.prenotazione_online, "
                   + "r.consegna_a_domicilio, r.tipo_cucina, r.telefono, r.website, r.premi, r.id_gestore, "
                   + "COALESCE(AVG(rec.stelle), 0) AS media_stelle, "
                   + "COUNT(rec.id_recensione) AS numero_recensioni "
                   + "FROM RistorantiTheKnife r "
                   + "LEFT JOIN Recensioni rec ON r.id_ristorante = rec.id_ristorante "
                   + "WHERE r.id_ristorante = ? GROUP BY r.id_ristorante";
        RistoranteDTO ristorante = null;
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idRistorante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ristorante = toDTO(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero del ristorante: " + e.getMessage());
        }
        if (ristorante != null) {
            popolaServizi(Collections.singletonList(ristorante));
        }
        return ristorante;
    }

    /**
     * Recupera tutti i ristoranti gestiti da un ristoratore.
     *
     * @param idGestore l'identificativo del ristoratore (gestore)
     * @return la lista dei ristoranti del gestore, eventualmente vuota
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public List<RistoranteDTO> trovaPerGestore (long idGestore) throws DataAccessException {
        String sql = "SELECT r.id_ristorante, r.nome, r.nazione, r.citta, r.provincia, r.indirizzo, "
                   + "r.latitudine, r.longitudine, r.fascia_prezzo, r.prenotazione_online, "
                   + "r.consegna_a_domicilio, r.tipo_cucina, r.telefono, r.website, r.premi, r.id_gestore, "
                   + "COALESCE(AVG(rec.stelle), 0) AS media_stelle, "
                   + "COUNT(rec.id_recensione) AS numero_recensioni "
                   + "FROM RistorantiTheKnife r "
                   + "LEFT JOIN Recensioni rec ON r.id_ristorante = rec.id_ristorante "
                   + "WHERE r.id_gestore = ? GROUP BY r.id_ristorante ORDER BY r.nome";
        List<RistoranteDTO> risultato = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idGestore);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    risultato.add(toDTO(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero dei ristoranti del gestore: "
                + e.getMessage());
        }
        popolaServizi(risultato);
        return risultato;
    }

    /**
     * Inserisce un nuovo ristorante associato a un gestore.
     * <p>
     * ATTENZIONE (punto di attenzione n.1 del verbale): il DTO
     * {@link AggiungiRistoranteDTO} non espone <code>provincia</code>,
     * <code>latitudine</code> e <code>longitudine</code>, pertanto queste
     * colonne vengono inserite come <code>NULL</code>. Da allineare con
     * Gasparini appena il DTO verra' esteso.
     *
     * @param dati      i dati del ristorante da inserire
     * @param idGestore l'identificativo del gestore che inserisce il
     *                  ristorante
     * @return l'identificativo generato per il nuovo ristorante
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public long inserisci (AggiungiRistoranteDTO dati, long idGestore) throws DataAccessException {
        String sql = "INSERT INTO RistorantiTheKnife "
                   + "(nome, nazione, citta, provincia, indirizzo, latitudine, longitudine, "
                   + " fascia_prezzo, prenotazione_online, consegna_a_domicilio, tipo_cucina, "
                   + " telefono, website, premi, id_gestore) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                   + "RETURNING id_ristorante";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dati.getNome());
            ps.setString(2, dati.getNazione());
            ps.setString(3, dati.getCitta());
            ps.setNull(4, java.sql.Types.VARCHAR);   // provincia non presente nel DTO
            ps.setString(5, dati.getIndirizzo());
            ps.setNull(6, java.sql.Types.DOUBLE);    // latitudine non presente nel DTO
            ps.setNull(7, java.sql.Types.DOUBLE);    // longitudine non presente nel DTO
            ps.setInt(8, dati.getFasciaPrezzo());
            ps.setBoolean(9, dati.isPrenotazioneOnline());
            ps.setBoolean(10, dati.isConsegnaADomicilio());
            ps.setString(11, dati.getTipoCucina());
            ps.setString(12, dati.getTelefono());
            ps.setString(13, dati.getWebsite());
            ps.setString(14, dati.getPremi());
            ps.setLong(15, idGestore);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_ristorante");
                }
                throw new DataAccessException("Inserimento ristorante senza id generato.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nell'inserimento del ristorante: " + e.getMessage());
        }
    }

    /**
     * Associa un gestore a un ristorante gia' esistente.
     *
     * @param idRistorante l'identificativo del ristorante
     * @param idGestore    l'identificativo del gestore da associare
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public void associaGestore (long idRistorante, long idGestore) throws DataAccessException {
        String sql = "UPDATE RistorantiTheKnife SET id_gestore = ? WHERE id_ristorante = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idGestore);
            ps.setLong(2, idRistorante);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Errore nell'associazione del gestore al ristorante: "
                + e.getMessage());
        }
    }

    /**
     * Calcola le statistiche di un ristorante: media delle stelle e numero di
     * recensioni ricevute.
     *
     * @param idRistorante l'identificativo del ristorante
     * @return le statistiche del ristorante, oppure <code>null</code> se il
     *         ristorante non esiste
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public StatisticheRistoranteDTO statistiche (long idRistorante) throws DataAccessException {
        String sql = "SELECT r.nome, COALESCE(AVG(rec.stelle), 0) AS media_stelle, "
                   + "COUNT(rec.id_recensione) AS numero_recensioni "
                   + "FROM RistorantiTheKnife r "
                   + "LEFT JOIN Recensioni rec ON r.id_ristorante = rec.id_ristorante "
                   + "WHERE r.id_ristorante = ? GROUP BY r.nome";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idRistorante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new StatisticheRistoranteDTO(idRistorante,
                        rs.getString("nome"),
                        rs.getDouble("media_stelle"),
                        rs.getInt("numero_recensioni"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nel calcolo delle statistiche del ristorante: "
                + e.getMessage());
        }
    }

    /**
     * Cerca i ristoranti entro un raggio in chilometri da una posizione
     * geografica. La distanza e' calcolata con la formula di Haversine
     * direttamente in SQL. Le coordinate arrivano gia' convertite dal livello
     * service (decisione 14): il DAO applica soltanto il filtro geografico sul
     * raggio, senza alcuna conversione nome-luogo.
     *
     * @param lat      la latitudine del punto di riferimento
     * @param lon      la longitudine del punto di riferimento
     * @param raggioKm il raggio di ricerca in chilometri
     * @return la lista dei ristoranti nel raggio, ordinati per distanza
     *         crescente, eventualmente vuota
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public List<RistoranteDTO> cercaVicino (double lat, double lon, double raggioKm)
            throws DataAccessException {
        String sql = "SELECT t.id_ristorante, t.nome, t.nazione, t.citta, t.provincia, t.indirizzo, "
                   + "t.latitudine, t.longitudine, t.fascia_prezzo, t.prenotazione_online, "
                   + "t.consegna_a_domicilio, t.tipo_cucina, t.telefono, t.website, t.premi, "
                   + "t.id_gestore, COALESCE(t.media_stelle, 0) AS media_stelle, "
                   + "t.numero_recensioni "
                   + "FROM ("
                   + "  SELECT r.id_ristorante, r.nome, r.nazione, r.citta, r.provincia, r.indirizzo, "
                   + "    r.latitudine, r.longitudine, r.fascia_prezzo, r.prenotazione_online, "
                   + "    r.consegna_a_domicilio, r.tipo_cucina, r.telefono, r.website, r.premi, "
                   + "    r.id_gestore, AVG(rec.stelle) AS media_stelle, "
                   + "    COUNT(rec.id_recensione) AS numero_recensioni, "
                   + "    (6371 * acos(LEAST(1.0, "
                   + "        cos(radians(?)) * cos(radians(r.latitudine)) * "
                   + "        cos(radians(r.longitudine) - radians(?)) + "
                   + "        sin(radians(?)) * sin(radians(r.latitudine))))) AS distanza_km "
                   + "  FROM RistorantiTheKnife r "
                   + "  LEFT JOIN Recensioni rec ON r.id_ristorante = rec.id_ristorante "
                   + "  WHERE r.latitudine IS NOT NULL AND r.longitudine IS NOT NULL "
                   + "  GROUP BY r.id_ristorante"
                   + ") t "
                   + "WHERE t.distanza_km <= ? "
                   + "ORDER BY t.distanza_km";
        List<RistoranteDTO> risultato = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, lat);
            ps.setDouble(2, lon);
            ps.setDouble(3, lat);
            ps.setDouble(4, raggioKm);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    risultato.add(toDTO(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nella ricerca dei ristoranti vicini: "
                + e.getMessage());
        }
        popolaServizi(risultato);
        return risultato;
    }

    /**
     * Individua le coordinate geografiche (latitudine e longitudine) di un
     * luogo a partire dal suo nome, calcolandole come media delle coordinate
     * dei ristoranti presenti in quella citta'. E' il ramo di fallback del
     * geocoding: viene usato quando il servizio esterno di
     * geocodifica non risponde.
     *
     * @param luogo il nome del luogo (citta') da geocodificare
     * @return la posizione stimata del luogo, oppure <code>null</code> se non
     *         esiste alcun ristorante in quella citta'
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public PosizioneDTO trovaCoordinateLuogo (String luogo) throws DataAccessException {
        String sql = "SELECT AVG(latitudine) AS latitudine, AVG(longitudine) AS longitudine "
                   + "FROM RistorantiTheKnife WHERE citta ILIKE ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, luogo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getObject("latitudine") != null) {
                    return new PosizioneDTO(
                        rs.getDouble("latitudine"),
                        rs.getDouble("longitudine"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nella geocodifica del luogo: " + e.getMessage());
        }
    }

    /**
     * Trasforma la riga corrente del result set in un {@link RistoranteDTO}.
     * La lista <code>servizi</code> viene lasciata a <code>null</code>: viene
     * valorizzata separatamente da {@link #popolaServizi(List)}.
     *
     * @param rs il result set con la riga gia' posizionata
     * @return il DTO corrispondente alla riga
     * @throws SQLException se la lettura dei campi fallisce
     */
    private RistoranteDTO toDTO (ResultSet rs) throws SQLException {
        long valore = rs.getLong("id_gestore");
        Long idGestore = rs.wasNull() ? null : valore;
        return new RistoranteDTO(
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
            idGestore);
    }

    /**
     * Popola la lista <code>servizi</code> dei ristoranti indicati con una
     * singola query <code>IN</code> sulla tabella ponte, raggruppando i
     * risultati in Java. Evita il problema delle righe duplicate dei join N:N
     * (trappola b del contratto) e il problema N+1.
     *
     * @param ristoranti i ristoranti da arricchire
     * @throws DataAccessException se l'accesso al database fallisce
     */
     void popolaServizi (List<RistoranteDTO> ristoranti) throws DataAccessException {
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
            throw new DataAccessException("Errore nel recupero dei servizi dei ristoranti: "
                + e.getMessage());
        }
        for (RistoranteDTO r : ristoranti) {
            r.setServizi(serviziPerRistorante.getOrDefault(r.getIdRistorante(), new ArrayList<>()));
        }
    }

    /**
     * Genera la stringa dei segnaposto <code>?</code> per una clausola
     * <code>IN</code> con <code>n</code> elementi.
     *
     * @param n il numero di segnaposto da generare
     * @return la stringa <code>?, ?, ...</code> lunga <code>n</code>
     */
    private static String segnaposto (int n) {
        return String.join(",", Collections.nCopies(n, "?"));
    }

    /**
     * Valorizza i segnaposto di un {@link PreparedStatement} con una lista di
     * parametri, mantenendo l'ordine.
     *
     * @param ps     il prepared statement da valorizzare
     * @param params i parametri, nell'ordine dei segnaposto
     * @throws SQLException se l'assegnazione di un parametro fallisce
     */
    private static void setParametri (PreparedStatement ps, List<Object> params)
            throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }
}