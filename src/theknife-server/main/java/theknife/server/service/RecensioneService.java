package theknife.server.service;

import java.util.List;

import theknife.common.dto.AggiungiRecensioneDTO;
import theknife.common.dto.IdRecensioneDTO;
import theknife.common.dto.IdRistoranteDTO;
import theknife.common.dto.ModificaRecensioneDTO;
import theknife.common.dto.RecensioneDTO;
import theknife.common.dto.RispondiRecensioneDTO;
import theknife.server.dao.RecensioneDAO;
import theknife.server.exception.ApplicationException;
import theknife.server.exception.DataAccessException;
import theknife.server.exception.ValidationException;

/**
 * Service delle recensioni: incapsula la logica di dominio per la lettura, la
 * scrittura e la risposta alle recensioni.
 * <p>
 * Non verifica la proprieta' delle operazioni: per la decisione 20 il ruolo e'
 * verificato dal Dispatcher e la proprieta' dal singolo Command, che risale
 * all'autore tramite {@link #ottieniRecensione(long)}. I metodi di modifica
 * ed eliminazione del DAO filtrano solo per identificativo, quindi chiamarli
 * senza aver prima verificato la proprieta' consente a chiunque di agire sulle
 * recensioni altrui.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class RecensioneService {

    /** Voto minimo ammesso, come il vincolo <code>CHECK</code> sulla tabella. */
    private static final int STELLE_MINIME = 1;

    /** Voto massimo ammesso, come il vincolo <code>CHECK</code> sulla tabella. */
    private static final int STELLE_MASSIME = 5;

    /** Accesso alla tabella delle recensioni. */
    private final RecensioneDAO recensioneDAO;

    /**
     * Costruisce il service sul DAO delle recensioni.
     *
     * @param recensioneDAO accesso alle recensioni
     */
    public RecensioneService(RecensioneDAO recensioneDAO) {
        this.recensioneDAO = recensioneDAO;
    }

    /**
     * Restituisce tutte le recensioni di un ristorante, risposte comprese.
     *
     * @param id l'identificativo del ristorante
     * @return le recensioni trovate, eventualmente in lista vuota
     * @throws ValidationException se l'identificativo e' <code>null</code>
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public List<RecensioneDTO> leggiRecensioni (IdRistoranteDTO id)
            throws ValidationException, DataAccessException {

        if (id == null) {
            throw new ValidationException("Identificativo del ristorante mancante.");
        }

        return recensioneDAO.trovaPerRistorante(id.getIdRistorante());
    }

    /**
     * Restituisce le recensioni di tutti i ristoranti gestiti da un ristoratore.
     *
     * @param idGestore l'identificativo del ristoratore
     * @return le recensioni trovate, eventualmente in lista vuota
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public List<RecensioneDTO> recensioniRistorantiGestiti (long idGestore)
            throws DataAccessException {

        return recensioneDAO.trovaPerGestore(idGestore);
    }

    /**
     * Restituisce tutte le recensioni scritte da un cliente, di qualsiasi
     * ristorante. E' la lista dietro la schermata "Le mie recensioni".
     *
     * @param idCliente l'identificativo del cliente autore delle recensioni
     * @return le recensioni trovate, eventualmente in lista vuota
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public List<RecensioneDTO> recensioniCliente (long idCliente)
            throws DataAccessException {

        return recensioneDAO.trovaPerCliente(idCliente);
    }

    /**
     * Rilegge una singola recensione. E' il metodo con cui i Command risalgono
     * all'autore di una recensione per verificarne la proprieta'.
     *
     * @param idRecensione l'identificativo della recensione
     * @return la recensione richiesta
     * @throws ApplicationException se nessuna recensione ha quell'identificativo
     * @throws DataAccessException  se l'accesso al database fallisce
     */
    public RecensioneDTO ottieniRecensione (long idRecensione)
            throws ApplicationException, DataAccessException {

        RecensioneDTO recensione = recensioneDAO.trovaPerId(idRecensione);
        if (recensione == null) {
            throw new ApplicationException("Recensione non trovata.");
        }
        return recensione;
    }

    /**
     * Pubblica una nuova recensione a nome dell'utente indicato.
     *
     * @param dati     titolo, testo, stelle e ristorante recensito
     * @param idUtente l'identificativo dell'autore
     * @throws ValidationException  se le stelle sono fuori scala o il testo e' vuoto
     * @throws ApplicationException se l'utente ha gia' recensito quel ristorante
     * @throws DataAccessException  se l'accesso al database fallisce
     */
    public void aggiungiRecensione (AggiungiRecensioneDTO dati, long idUtente)
            throws ValidationException, ApplicationException, DataAccessException {

        if (dati == null) {
            throw new ValidationException("Dati della recensione mancanti.");
        }
        validaStelle(dati.getStelle());
        validaTesto(dati.getTesto());

        recensioneDAO.inserisci(dati, idUtente);
    }

    /**
     * Aggiorna titolo, testo e stelle di una recensione esistente. La
     * proprieta' va verificata dal Command prima di chiamare questo metodo.
     *
     * @param dati i nuovi contenuti della recensione
     * @throws ValidationException se le stelle sono fuori scala o il testo e' vuoto
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public void modificaRecensione (ModificaRecensioneDTO dati)
            throws ValidationException, DataAccessException {

        if (dati == null) {
            throw new ValidationException("Dati della recensione mancanti.");
        }
        validaStelle(dati.getNuoveStelle());
        validaTesto(dati.getNuovoTesto());

        recensioneDAO.aggiorna(dati);
    }

    /**
     * Elimina una recensione. La proprieta' va verificata dal Command prima di
     * chiamare questo metodo.
     *
     * @param id l'identificativo della recensione da eliminare
     * @throws ValidationException se l'identificativo e' <code>null</code>
     * @throws DataAccessException se l'accesso al database fallisce
     */
    public void eliminaRecensione (IdRecensioneDTO id)
            throws ValidationException, DataAccessException {

        if (id == null) {
            throw new ValidationException("Identificativo della recensione mancante.");
        }

        recensioneDAO.elimina(id.getIdRecensione());
    }

    /**
     * Registra la risposta del gestore a una recensione. Che il gestore sia
     * quello del ristorante recensito va verificato dal Command prima di
     * chiamare questo metodo (decisione 24).
     *
     * @param dati la recensione e il testo della risposta
     * @throws ValidationException  se la risposta e' vuota
     * @throws ApplicationException se la recensione ha gia' una risposta
     * @throws DataAccessException  se l'accesso al database fallisce
     */
    public void rispondiRecensione (RispondiRecensioneDTO dati)
            throws ValidationException, ApplicationException, DataAccessException {

        if (dati == null) {
            throw new ValidationException("Dati della risposta mancanti.");
        }
        if (dati.getRisposta() == null || dati.getRisposta().isBlank()) {
            throw new ValidationException("La risposta non puo' essere vuota.");
        }

        recensioneDAO.rispondi(dati);
    }

    /**
     * Verifica che il voto rientri nella scala prevista dal modello dei dati
     * (1-5, vincolo <code>CHECK</code> in <code>Schema.sql</code>).
     *
     * @param stelle il voto da controllare
     * @throws ValidationException se il voto e' fuori scala
     */
    private void validaStelle (int stelle) throws ValidationException {
        if (stelle < STELLE_MINIME || stelle > STELLE_MASSIME) {
            throw new ValidationException("Le stelle devono essere comprese tra "
                    + STELLE_MINIME + " e " + STELLE_MASSIME + ".");
        }
    }

    /**
     * Verifica che il testo della recensione sia presente. Il modello
     * concettuale lo dichiara obbligatorio, ma la colonna del database non ha
     * il vincolo <code>NOT NULL</code>: questo e' l'unico punto in cui la
     * regola viene applicata.
     *
     * @param testo il testo da controllare
     * @throws ValidationException se il testo e' assente o composto di soli spazi
     */
    private void validaTesto (String testo) throws ValidationException {
        if (testo == null || testo.isBlank()) {
            throw new ValidationException("Il testo della recensione non puo' essere vuoto.");
        }
    }
}
