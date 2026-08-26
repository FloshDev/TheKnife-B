package theknife.client.service;

import java.io.IOException;

import theknife.client.network.ServerConnection;
import theknife.common.dto.LoginDTO;
import theknife.common.dto.LoginResultDTO;
import theknife.common.dto.RegistrazioneDTO;
import theknife.common.enums.CommandType;
import theknife.common.enums.ResponseStatus;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;


/**
 * Espone le operazioni di autenticazione (login, registrazione, logout) ai
 * controller, traducendole in Request/Response verso il server tramite
 * ServerConnection.
 *
 * @author Barlera Marco, 760000, VA
 */

public class AuthService {
    /** Connessione condivisa al server, unico canale su cui viaggiano le richieste. */
    private final ServerConnection connection = ServerConnection.getInstance();

    /**
     * Effettua il login inviando le credenziali al server e, in caso di
     * successo, memorizza il token di sessione e l'utente corrente nella
     * {@code ServerConnection}.
     *
     * @param username username inserito dall'utente
     * @param password password inserita dall'utente
     * @return il risultato del login, con token di sessione e dati utente
     * @throws ErroreServerException se il server rifiuta le credenziali o la richiesta
     * @throws IOException se la connessione al server fallisce
     * @throws ClassNotFoundException se la deserializzazione della risposta fallisce
     */
    public LoginResultDTO login(String username, String password) throws IOException, ClassNotFoundException {
        LoginDTO payload = new LoginDTO(username, password); // Serializzazione
        Request request = new Request(CommandType.ACCEDI, payload, connection.getSessionToken()); // Rischiesta completa per server

        Response response = connection.inviaRichiesta(request); // Invia la richiesta e aspetta risposta

        if(response.getStatus() == ResponseStatus.SUCCESSO) {
            LoginResultDTO loginResultDTO = (LoginResultDTO) response.getPayload();
            connection.setSessionToken(loginResultDTO.getSessionToken());
            connection.setUtenteCorrente(loginResultDTO.getUtente());
            return loginResultDTO;
        } else {
            throw new ErroreServerException(response.getMessaggio());
        }
    }

    /**
     * Invia i dati di registrazione al server per la creazione di un nuovo utente.
     *
     * @param dati i dati di registrazione inseriti dall'utente
     * @throws ErroreServerException se il server rifiuta la registrazione (es. username o email già in uso)
     * @throws IOException se la connessione al server fallisce
     * @throws ClassNotFoundException se la deserializzazione della risposta fallisce
     */
    public void registrati(RegistrazioneDTO dati) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.REGISTRATI, dati, connection.getSessionToken());

        Response response = connection.inviaRichiesta(request);
        
        if(response.getStatus() != ResponseStatus.SUCCESSO) {
            throw new ErroreServerException(response.getMessaggio());
        }
    }

    /**
     * Disconnette l'utente corrente, avvisando il server e ripulendo la sessione
     * locale. Il token viene rimosso anche se la richiesta al server fallisce,
     * cosi' l'interfaccia torna sempre in stato non autenticato.
     *
     * @throws IOException se la connessione al server fallisce
     * @throws ClassNotFoundException se la deserializzazione della risposta fallisce
     */
    public void logout() throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.ESCI, null, connection.getSessionToken());
        try {
            connection.inviaRichiesta(request);
        } finally {
            connection.clearSessionToken(); // Rimuove il token e l'utente loggato anche se il server non è raggiungibile
        }
    }
}
