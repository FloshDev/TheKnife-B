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

/**
 * Service degli utenti: incapsula la logica di autenticazione e registrazione.
 * Verifica le credenziali contro l'hash BCrypt salvato nel database, apre e
 * chiude le sessioni, cifra le password prima di affidarle al DAO.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class UtenteService {

    private static final int COSTO_BCRYPT = 12;
    private static final String CREDENZIALI_NON_VALIDE = "Username o password non validi.";

    private final UtenteDAO utenteDAO;
    private final SessionManager sessionManager;

    public UtenteService (UtenteDAO utenteDAO, SessionManager sessionManager) {
        this.utenteDAO = utenteDAO;
        this.sessionManager = sessionManager;
    }

    public LoginResultDTO accedi (LoginDTO credenziali)
            throws ApplicationException, DataAccessException {

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

    public void registra (RegistrazioneDTO dati)
            throws ApplicationException, DataAccessException {

        if (utenteDAO.usernameEsiste(dati.getUsername())) {
            throw new ApplicationException("Username gia' in uso.");
        }

        String hash = BCrypt.withDefaults()
            .hashToString(COSTO_BCRYPT, dati.getPassword().toCharArray());

        utenteDAO.inserisci(dati, hash);
    }

    public void esci (String token) {
        sessionManager.logout(token);
    }
}
