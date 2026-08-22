package theknife.client.service;

import java.io.IOException;
import java.util.List;

import theknife.client.network.ServerConnection;
import theknife.common.dto.AggiungiRistoranteDTO;
import theknife.common.dto.CercaRistorantiDTO;
import theknife.common.dto.CercaVicinoDTO;
import theknife.common.dto.IdRistoranteDTO;
import theknife.common.dto.PosizioneDTO;
import theknife.common.dto.RistoranteDTO;
import theknife.common.enums.CommandType;
import theknife.common.enums.ResponseStatus;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;


/**
 * Espone le operazioni sui ristoranti (ricerca, dettaglio, preferiti,
 * gestione lato ristoratore) ai controller, traducendole in Request/Response
 * verso il server tramite ServerConnection.
 *
 * @author Barlera Marco, 760000, VA
 */

public class RistoranteService {

    /** Connessione condivisa al server, unico canale su cui viaggiano le richieste. */
    private final ServerConnection connection = ServerConnection.getInstance();

    @SuppressWarnings("unchecked")
    public List<RistoranteDTO> cercaRistoranti(CercaRistorantiDTO filtri) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.CERCA_RISTORANTI, filtri, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() == ResponseStatus.SUCCESSO) {
            return (List<RistoranteDTO>) response.getPayload();
        } else {
            throw new IOException(response.getMessaggio());
        }
    }

    @SuppressWarnings("unchecked")
    public List<RistoranteDTO> cercaVicino(CercaVicinoDTO filtri) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.CERCA_VICINO, filtri, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() == ResponseStatus.SUCCESSO) {
            return (List<RistoranteDTO>) response.getPayload();
        } else {
            throw new IOException(response.getMessaggio());
        }
    }

    public RistoranteDTO ottieniDettagli(IdRistoranteDTO id) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.OTTIENI_DETTAGLI_RISTORANTE, id, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() == ResponseStatus.SUCCESSO) {
            return (RistoranteDTO) response.getPayload();
        } else {
            throw new IOException(response.getMessaggio());
        }
    }

    public PosizioneDTO ottieniLocalitaIniziale() throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.OTTIENI_LOCALITA_INIZIALE, null, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() == ResponseStatus.SUCCESSO) {
            return (PosizioneDTO) response.getPayload();
        } else {
            throw new IOException(response.getMessaggio());
        }
    }

    public void aggiungiPreferito(IdRistoranteDTO id) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.AGGIUNGI_PREFERITO, id, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() != ResponseStatus.SUCCESSO)
            throw new IOException(response.getMessaggio());
    }

    public void rimuoviPreferito(IdRistoranteDTO id) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.RIMUOVI_PREFERITO, id, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() != ResponseStatus.SUCCESSO)
            throw new IOException(response.getMessaggio());    
    }

    @SuppressWarnings("unchecked")
    public List<RistoranteDTO> ottieniPreferiti() throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.VEDI_PREFERITI, null, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() == ResponseStatus.SUCCESSO) {
            return (List<RistoranteDTO>) response.getPayload();
        } else {
            throw new IOException(response.getMessaggio());
        }
    }

    public RistoranteDTO aggiungiRistorante(AggiungiRistoranteDTO ristorante) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.AGGIUNGI_RISTORANTE, ristorante, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() == ResponseStatus.SUCCESSO) {
            return (RistoranteDTO) response.getPayload();
        } else {
            throw new IOException(response.getMessaggio());
        }
    }

    @SuppressWarnings("unchecked")
    public List<RistoranteDTO> vediRistorantiGestiti() throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.VEDI_RISTORANTI_GESTITI, null, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() == ResponseStatus.SUCCESSO) {
            return (List<RistoranteDTO>) response.getPayload();
        } else {
            throw new IOException(response.getMessaggio());
        }
    }

    public void associaRistorante(IdRistoranteDTO ristorante) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.ASSOCIA_RISTORANTE, ristorante, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() != ResponseStatus.SUCCESSO)
            throw new IOException(response.getMessaggio());
    }

    public void eliminaRistorante(IdRistoranteDTO id) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.ELIMINA_RISTORANTE, id, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() != ResponseStatus.SUCCESSO)
            throw new IOException(response.getMessaggio());
    }
}