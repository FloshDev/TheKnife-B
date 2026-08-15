package theknife.server.service;

import java.util.UUID;

import at.favre.lib.crypto.bcrypt.BCrypt;

import theknife.common.dto.LoginDTO;
import theknife.common.dto.LoginResultDTO;
import theknife.common.dto.RegistrazioneDTO;
import theknife.common.dto.UtenteDTO;
import theknife.server.dao.UtenteDAO;
import theknife.server.exception.ApplicationException;
import theknife.server.exception.DataAccessException;
import theknife.server.exception.ValidationException;

/**
 * Service degli utenti: incapsula la logica di autenticazione e registrazione.
 * Verifica le credenziali contro l'hash BCrypt salvato nel database, apre e
 * chiude le sessioni, cifra le password prima di affidarle al DAO.
 * <p>
 * La validazione dei campi obbligatori e' qui, non solo nella GUI: un client
 * modificato o un errore di validazione lato interfaccia non devono poter
 * creare un account senza password.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class UtenteService {

    private static final int COSTO_BCRYPT = 12;
    private static final String CREDENZIALI_NON_VALIDE = "Username o password non validi.";

    private final UtenteDAO utenteDAO;
    private final SessionManager sessionManager;

    /**
     * @param utenteDAO      accesso agli utenti
     * @param sessionManager registro delle sessioni attive
     */
    public UtenteService (UtenteDAO utenteDAO, SessionManager sessionManager) {
        this.utenteDAO = utenteDAO;
        this.sessionManager = sessionManager;
    }

    /**
     * Verifica le credenziali e apre una sessione. Il token generato viene
     * registrato nel {@link SessionManager} e restituito al client.
     *
     * @param credenziali username e password in chiaro
     * @return token di sessione, ruolo e dati dell'utente autenticato
     * @throws ValidationException  se username o password sono assenti
     * @throws ApplicationException se le credenziali non corrispondono a nessun utente
     * @throws DataAccessException  se l'accesso al database fallisce
     */
    public LoginResultDTO accedi (LoginDTO credenziali)
            throws ValidationException, ApplicationException, DataAccessException {

        if (credenziali == null) {
            throw new ValidationException("Credenziali mancanti.");
        }
        esigiTesto(credenziali.getUsername(), "lo username");
        esigiTesto(credenziali.getPassword(), "la password");

        String hashSalvato = utenteDAO.trovaHashPassword(credenziali.getUsername());

        if (hashSalvato == null) {
            throw new ApplicationException(CREDENZIALI_NON_VALIDE);
        }

        BCrypt.Result esito = BCrypt.verifyer()
            .verify(credenziali.getPassword().toCharArray(), hashSalvato);

        if (!esito.verified) {
            throw new ApplicationException(CREDENZIALI_NON_VALIDE);
        }

        UtenteDTO utente = utenteDAO.trovaPerUsername(credenziali.getUsername());

        if (utente == null) {
            throw new ApplicationException(CREDENZIALI_NON_VALIDE);
        }

        String token = UUID.randomUUID().toString();
        sessionManager.login(token, utente);

        return new LoginResultDTO(token, utente.getRuolo(), utente);
    }

    /**
     * Registra un nuovo utente cifrando la password con BCrypt prima di
     * affidarla al DAO. La password in chiaro non lascia mai questo metodo.
     *
     * @param dati i dati anagrafici e le credenziali del nuovo utente
     * @throws ValidationException  se un campo obbligatorio manca
     * @throws ApplicationException se lo username e' gia' in uso
     * @throws DataAccessException  se l'accesso al database fallisce
     */
    public void registra (RegistrazioneDTO dati)
            throws ValidationException, ApplicationException, DataAccessException {

        validaDatiRegistrazione(dati);

        if (utenteDAO.usernameEsiste(dati.getUsername())) {
            throw new ApplicationException("Username gia' in uso.");
        }

        String hash = BCrypt.withDefaults()
            .hashToString(COSTO_BCRYPT, dati.getPassword().toCharArray());

        utenteDAO.inserisci(dati, hash);
    }

    /**
     * Chiude la sessione associata al token. Un token sconosciuto o
     * <code>null</code> non produce errore: l'effetto voluto - nessuna
     * sessione attiva per quel token - e' gia' verificato.
     *
     * @param token il token della sessione da chiudere
     */
    public void esci (String token) {
        sessionManager.logout(token);
    }

    /**
     * Verifica che i campi obbligatori della registrazione siano presenti.
     * E' il controllo che impedisce la creazione di un account con password
     * vuota indipendentemente da cosa fa la GUI.
     *
     * @param dati i dati da controllare
     * @throws ValidationException se un campo obbligatorio manca
     */
    private void validaDatiRegistrazione (RegistrazioneDTO dati) throws ValidationException {
        if (dati == null) {
            throw new ValidationException("Dati di registrazione mancanti.");
        }
        esigiTesto(dati.getNome(), "il nome");
        esigiTesto(dati.getCognome(), "il cognome");
        esigiTesto(dati.getUsername(), "lo username");
        esigiTesto(dati.getPassword(), "la password");

        if (dati.getRuolo() == null) {
            throw new ValidationException("Campo obbligatorio mancante: il ruolo.");
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
}
