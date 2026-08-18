package theknife.server.service;

import java.util.List;

import theknife.common.dto.AggiungiRistoranteDTO;
import theknife.common.dto.CercaRistorantiDTO;
import theknife.common.dto.CercaVicinoDTO;
import theknife.common.dto.IdRistoranteDTO;
import theknife.common.dto.PosizioneDTO;
import theknife.common.dto.RistoranteDTO;
import theknife.common.dto.StatisticheRistoranteDTO;
import theknife.server.dao.PreferitoDAO;
import theknife.server.dao.RistoranteDAO;
import theknife.server.dao.ServizioDAO;
import theknife.server.exception.ApplicationException;
import theknife.server.exception.DataAccessException;
import theknife.server.exception.ValidationException;
import theknife.server.external.GeocodingClient;
import theknife.server.external.LocalizzazioneIpClient;

/**
 * Service dei ristoranti: incapsula la logica di dominio per la ricerca, la
 * consultazione, l'inserimento e i preferiti (decisione 21). Delega l'accesso
 * ai dati ai DAO e le informazioni geografiche ai client del package
 * {@code server.external}: il service non conosce ne' il database ne' il
 * protocollo HTTP.
 * <p>
 * Non contiene controlli di autorizzazione: per la decisione 20 il ruolo e'
 * verificato dal Dispatcher e la proprieta' dal singolo Command.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class RistoranteService {

    /** Accesso alla tabella dei ristoranti. */
    private final RistoranteDAO ristoranteDAO;

    /** Accesso ai preferiti dei clienti. */
    private final PreferitoDAO preferitoDAO;

    /** Accesso ai servizi associati ai ristoranti. */
    private final ServizioDAO servizioDAO;

    /** Traduzione fra indirizzi e coordinate, delegata al servizio esterno. */
    private final GeocodingClient geocoding;

    /** Stima della posizione da indirizzo IP, per la localita' iniziale. */
    private final LocalizzazioneIpClient localizzazione;

    /**
     * Costruisce il service sui tre DAO e sui due client esterni che gli
     * servono.
     *
     * @param ristoranteDAO  accesso ai ristoranti
     * @param preferitoDAO   accesso ai preferiti dei clienti
     * @param servizioDAO    accesso ai servizi offerti dai ristoranti
     * @param geocoding      traduzione indirizzo/coordinate (decisioni 17, 29)
     * @param localizzazione stima della posizione da IP (decisione 18)
     */
    public RistoranteService(RistoranteDAO ristoranteDAO,
                             PreferitoDAO preferitoDAO,
                             ServizioDAO servizioDAO,
                             GeocodingClient geocoding,
                             LocalizzazioneIpClient localizzazione) {
        this.ristoranteDAO = ristoranteDAO;
        this.preferitoDAO = preferitoDAO;
        this.servizioDAO = servizioDAO;
        this.geocoding = geocoding;
        this.localizzazione = localizzazione;
    }

    /**
     * Cerca i ristoranti che soddisfano i criteri del filtro. I criteri non
     * valorizzati vengono ignorati dal DAO, quindi un filtro con tutti i campi
     * vuoti restituisce l'intero catalogo.
     *
     * @param filtri i criteri di ricerca
     * @return i ristoranti trovati, eventualmente in lista vuota
     * @throws ValidationException se il filtro e' <code>null</code>
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public List<RistoranteDTO> cercaRistoranti (CercaRistorantiDTO filtri)
            throws ValidationException, DataAccessException {

        if (filtri == null) {
            throw new ValidationException("Criteri di ricerca mancanti.");
        }

        return ristoranteDAO.cerca(filtri);
    }

    /**
     * Restituisce la scheda completa di un ristorante.
     *
     * @param id l'identificativo del ristorante
     * @return il ristorante richiesto
     * @throws ValidationException  se l'identificativo e' <code>null</code>
     * @throws ApplicationException se nessun ristorante ha quell'identificativo
     * @throws DataAccessException  se l'accesso al database fallisce
     */
    public RistoranteDTO ottieniDettagli (IdRistoranteDTO id)
            throws ValidationException, ApplicationException, DataAccessException {

        if (id == null) {
            throw new ValidationException("Identificativo del ristorante mancante.");
        }

        RistoranteDTO ristorante = ristoranteDAO.trovaPerId(id.getIdRistorante());
        if (ristorante == null) {
            throw new ApplicationException("Ristorante non trovato.");
        }

        return ristorante;
    }

    /**
     * Cerca i ristoranti entro un raggio dal luogo indicato. Le coordinate del
     * luogo arrivano dal geocoding esterno; se il servizio non risponde si
     * ripiega sulla media delle coordinate dei ristoranti gia' presenti in
     * quella citta' (decisione 17).
     *
     * @param filtri il luogo di riferimento e il raggio in chilometri
     * @return i ristoranti entro il raggio, eventualmente in lista vuota
     * @throws ValidationException  se il luogo manca o il raggio non e' positivo
     * @throws ApplicationException se le coordinate del luogo non sono determinabili
     * @throws DataAccessException  se l'accesso al database fallisce
     */
    public List<RistoranteDTO> cercaVicino (CercaVicinoDTO filtri)
            throws ValidationException, ApplicationException, DataAccessException {

        if (filtri == null || filtri.getLuogo() == null || filtri.getLuogo().isBlank()) {
            throw new ValidationException("Luogo di riferimento mancante.");
        }
        if (filtri.getRaggioKm() <= 0) {
            throw new ValidationException("Il raggio di ricerca deve essere maggiore di zero.");
        }

        PosizioneDTO posizione = geocoding.geocodifica(filtri.getLuogo());
        if (posizione == null) {
            posizione = ristoranteDAO.trovaCoordinateLuogo(filtri.getLuogo());
        }
        if (posizione == null) {
            throw new ApplicationException("Impossibile determinare le coordinate di "
                    + filtri.getLuogo() + ".");
        }

        return ristoranteDAO.cercaVicino(posizione.getLatitudine(),
                                         posizione.getLongitudine(),
                                         filtri.getRaggioKm());
    }

    /**
     * Stima la localita' da suggerire all'avvio del client (decisione 18)
     * geolocalizzando l'indirizzo del client stesso (decisione 30). Il
     * fallimento non e' un errore: significa che la stima non e' disponibile e
     * che l'utente digitera' il luogo a mano.
     *
     * @param indirizzoClient l'indirizzo IP del client, scritto nella richiesta
     *                        dal gestore della connessione; se assente la stima
     *                        ripiega sulla macchina del server
     * @return la posizione stimata, oppure <code>null</code> se non determinabile
     */
    public PosizioneDTO localitaIniziale (String indirizzoClient) {
        return localizzazione.posizioneStimata(indirizzoClient);
    }

    /**
     * Inserisce un nuovo ristorante gestito dall'utente indicato. Le coordinate
     * non arrivano dal client: le calcola il server geocodificando l'indirizzo
     * (decisione 29), con ripiego sulla citta' e, se anche quello fallisce,
     * inserimento senza coordinate - il ristorante viene creato comunque e
     * semplicemente non comparira' nelle ricerche per vicinanza.
     *
     * @param dati      i dati del ristorante da inserire
     * @param idGestore l'identificativo del ristoratore che lo inserisce
     * @return il ristorante appena creato, riletto dal database
     * @throws ValidationException  se mancano i campi obbligatori
     * @throws ApplicationException se il ristorante non e' rileggibile dopo l'inserimento
     * @throws DataAccessException  se l'accesso al database fallisce
     */
    public RistoranteDTO aggiungi (AggiungiRistoranteDTO dati, long idGestore)
            throws ValidationException, ApplicationException, DataAccessException {

        validaDatiRistorante(dati);

        PosizioneDTO posizione = geocoding.geocodifica(indirizzoCompleto(dati));
        if (posizione == null) {
            posizione = ristoranteDAO.trovaCoordinateLuogo(dati.getCitta());
        }

        Double latitudine = posizione != null ? posizione.getLatitudine() : null;
        Double longitudine = posizione != null ? posizione.getLongitudine() : null;

        long idRistorante = ristoranteDAO.inserisci(dati, idGestore, latitudine, longitudine);

        if (dati.getServizi() != null && !dati.getServizi().isEmpty()) {
            servizioDAO.associa(idRistorante, dati.getServizi());
        }

        RistoranteDTO creato = ristoranteDAO.trovaPerId(idRistorante);
        if (creato == null) {
            throw new ApplicationException("Ristorante inserito ma non rileggibile.");
        }

        return creato;
    }

    /**
     * Restituisce i ristoranti gestiti da un ristoratore.
     *
     * @param idGestore l'identificativo del ristoratore
     * @return i ristoranti gestiti, eventualmente in lista vuota
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public List<RistoranteDTO> ristorantiGestiti (long idGestore)
            throws DataAccessException {

        return ristoranteDAO.trovaPerGestore(idGestore);
    }

    /**
     * Restituisce il riepilogo delle recensioni di un ristorante.
     *
     * @param id l'identificativo del ristorante
     * @return media delle stelle e numero di recensioni
     * @throws ValidationException se l'identificativo e' <code>null</code>
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public StatisticheRistoranteDTO statistiche (IdRistoranteDTO id)
            throws ValidationException, DataAccessException {

        if (id == null) {
            throw new ValidationException("Identificativo del ristorante mancante.");
        }

        return ristoranteDAO.statistiche(id.getIdRistorante());
    }

    /**
     * Assegna a un ristoratore un ristorante gia' presente in catalogo.
     *
     * @param id        l'identificativo del ristorante
     * @param idGestore l'identificativo del ristoratore
     * @throws ValidationException se l'identificativo e' <code>null</code>
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public void associaRistorante (IdRistoranteDTO id, long idGestore)
            throws ValidationException, DataAccessException {

        if (id == null) {
            throw new ValidationException("Identificativo del ristorante mancante.");
        }

        ristoranteDAO.associaGestore(id.getIdRistorante(), idGestore);
    }

    /**
     * Aggiunge un ristorante ai preferiti di un cliente.
     *
     * @param idCliente l'identificativo del cliente
     * @param id        l'identificativo del ristorante
     * @throws ValidationException se l'identificativo e' <code>null</code>
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public void aggiungiPreferito (long idCliente, IdRistoranteDTO id)
            throws ValidationException, DataAccessException {

        if (id == null) {
            throw new ValidationException("Identificativo del ristorante mancante.");
        }

        preferitoDAO.aggiungi(idCliente, id.getIdRistorante());
    }

    /**
     * Rimuove un ristorante dai preferiti di un cliente.
     *
     * @param idCliente l'identificativo del cliente
     * @param id        l'identificativo del ristorante
     * @throws ValidationException se l'identificativo e' <code>null</code>
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public void rimuoviPreferito (long idCliente, IdRistoranteDTO id)
            throws ValidationException, DataAccessException {

        if (id == null) {
            throw new ValidationException("Identificativo del ristorante mancante.");
        }

        preferitoDAO.rimuovi(idCliente, id.getIdRistorante());
    }

    /**
     * Restituisce i ristoranti preferiti di un cliente.
     *
     * @param idCliente l'identificativo del cliente
     * @return i preferiti, eventualmente in lista vuota
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public List<RistoranteDTO> ottieniPreferiti (long idCliente)
            throws DataAccessException {

        return preferitoDAO.trovaPerCliente(idCliente);
    }

    /**
     * Verifica che i campi obbligatori del nuovo ristorante siano presenti.
     * L'indirizzo e' obbligatorio per intero, provincia compresa (decisione 28).
     *
     * @param dati i dati da controllare
     * @throws ValidationException se un campo obbligatorio manca o non e' valido
     */
    private void validaDatiRistorante (AggiungiRistoranteDTO dati) throws ValidationException {
        if (dati == null) {
            throw new ValidationException("Dati del ristorante mancanti.");
        }
        esigiTesto(dati.getNome(), "il nome");
        esigiTesto(dati.getIndirizzo(), "l'indirizzo");
        esigiTesto(dati.getCitta(), "la citta'");
        esigiTesto(dati.getProvincia(), "la provincia");
        esigiTesto(dati.getNazione(), "la nazione");
        esigiTesto(dati.getTipoCucina(), "il tipo di cucina");

        if (dati.getFasciaPrezzo() < 1 || dati.getFasciaPrezzo() > 4) {
            throw new ValidationException("La fascia di prezzo deve essere compresa tra 1 e 4.");
        }
    }

    /**
     * Verifica che un campo testuale obbligatorio sia valorizzato.
     *
     * @param valore il valore da controllare
     * @param nome   il nome del campo, usato nel messaggio d'errore
     * @throws ValidationException se il valore e' assente o composto di soli spazi
     */
    private void esigiTesto (String valore, String nome) throws ValidationException {
        if (valore == null || valore.isBlank()) {
            throw new ValidationException("Campo obbligatorio mancante: " + nome + ".");
        }
    }

    /**
     * Compone l'indirizzo da mandare al geocoding, unendo i campi valorizzati.
     *
     * @param dati i dati del ristorante
     * @return l'indirizzo completo su una riga
     */
    private String indirizzoCompleto (AggiungiRistoranteDTO dati) {
        return dati.getIndirizzo() + ", "
             + dati.getCitta() + ", "
             + dati.getProvincia() + ", "
             + dati.getNazione();
    }
}
