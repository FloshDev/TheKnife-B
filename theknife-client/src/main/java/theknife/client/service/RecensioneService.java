package theknife.client.service;

import java.io.IOException;
import java.util.List;

import theknife.client.network.ServerConnection;
import theknife.common.dto.AggiungiRecensioneDTO;
import theknife.common.dto.IdRecensioneDTO;
import theknife.common.dto.IdRistoranteDTO;
import theknife.common.dto.ModificaRecensioneDTO;
import theknife.common.dto.RecensioneDTO;
import theknife.common.dto.RispondiRecensioneDTO;
import theknife.common.enums.CommandType;
import theknife.common.enums.ResponseStatus;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;


/**
 * Espone le operazioni sulle recensioni (lettura, aggiunta, modifica,
 * eliminazione, risposta lato ristoratore) ai controller, traducendole in
 * Request/Response verso il server tramite ServerConnection.
 *
 * @author Barlera Marco, 760000, VA
 */

public class RecensioneService {
    
    private final ServerConnection connection = ServerConnection.getInstance();

    @SuppressWarnings("unchecked")
    public List<RecensioneDTO> leggiRecensioni(IdRistoranteDTO idRistorante) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.LEGGI_RECENSIONI, idRistorante, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);
        
        if (response.getStatus() == ResponseStatus.SUCCESSO) {
            return (List<RecensioneDTO>) response.getPayload();
        } else {
            throw new IOException(response.getMessaggio());
        }
    }

    public void aggiungiRecensione(AggiungiRecensioneDTO dati) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.AGGIUNGI_RECENSIONE, dati, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);
        
        if (response.getStatus() != ResponseStatus.SUCCESSO) {
            throw new IOException(response.getMessaggio());
        }
    }

    public void modificaRecensione(ModificaRecensioneDTO recensione) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.MODIFICA_RECENSIONE, recensione, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);
        
        if (response.getStatus() != ResponseStatus.SUCCESSO) {
            throw new IOException(response.getMessaggio());
        }
    }

    public void eliminaRecensione(IdRecensioneDTO idRecensione) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.ELIMINA_RECENSIONE, idRecensione, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);
        
        if (response.getStatus() != ResponseStatus.SUCCESSO)
            throw new IOException(response.getMessaggio());
    }

    @SuppressWarnings("unchecked")
    public List<RecensioneDTO> leggiRecensioniRistorantiGestiti() throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.LEGGI_RECENSIONI_RISTORANTI_GESTITI, null, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);
        
        if (response.getStatus() == ResponseStatus.SUCCESSO) {
            return (List<RecensioneDTO>) response.getPayload();
        } else {
            throw new IOException(response.getMessaggio());
        }
    }

    public void rispondiRecensione(RispondiRecensioneDTO recensione) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.RISPONDI_RECENSIONE, recensione, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);
        
        if (response.getStatus() != ResponseStatus.SUCCESSO) {
            throw new IOException(response.getMessaggio());
        }
    }
}
