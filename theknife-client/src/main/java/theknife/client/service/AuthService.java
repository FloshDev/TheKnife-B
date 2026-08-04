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
    private final ServerConnection connection = ServerConnection.getInstance();

    public LoginResultDTO login(String username, String password) throws IOException, ClassNotFoundException {
        LoginDTO payload = new LoginDTO(username, password); // Serializzazione
        Request request = new Request(CommandType.ACCEDI, payload, connection.getSessionToken()); // Rischiesta completa per server

        Response response = connection.inviaRichiesta(request); // Invia la richiesta e aspetta risposta

        if(response.getStatus() == ResponseStatus.SUCCESSO) {
            LoginResultDTO loginResultDTO = (LoginResultDTO) response.getPayload(); // Deserializzazione
            connection.setSessionToken(loginResultDTO.getSessionToken()); // Salva il token
            return loginResultDTO; 
        } else {
            throw new IOException(response.getMessaggio());
        }
    }

    public LoginResultDTO registrati(RegistrazioneDTO dati) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.REGISTRATI, dati, connection.getSessionToken());

        Response response = connection.inviaRichiesta(request);
        
        if(response.getStatus() == ResponseStatus.SUCCESSO) {
            LoginResultDTO loginResultDTO = (LoginResultDTO) response.getPayload();
            connection.setSessionToken(loginResultDTO.getSessionToken());
            return loginResultDTO;
        } else {
            throw new IOException(response.getMessaggio());
        }
    }

    public void logout() throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.ESCI, null, connection.getSessionToken());
        connection.inviaRichiesta(request);
        connection.setSessionToken(null);
    }
}
