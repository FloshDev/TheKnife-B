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
    
    /** Connessione condivisa al server, unico canale su cui viaggiano le richieste. */
    private final ServerConnection connection = ServerConnection.getInstance();

    /**
     * Recupera le recensioni pubblicate su un ristorante, comprese le
     * eventuali risposte del gestore.
     *
     * @param idRistorante l'identificativo del ristorante
     * @return la lista delle recensioni
     * @throws ErroreServerException se il server rifiuta la richiesta
     * @throws IOException se la connessione al server fallisce
     * @throws ClassNotFoundException se la deserializzazione della risposta fallisce
     */
    @SuppressWarnings("unchecked")
    public List<RecensioneDTO> leggiRecensioni(IdRistoranteDTO idRistorante) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.LEGGI_RECENSIONI, idRistorante, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);
        
        if (response.getStatus() == ResponseStatus.SUCCESSO) {
            return (List<RecensioneDTO>) response.getPayload();
        } else {
            throw new ErroreServerException(response.getMessaggio());
        }
    }

    /**
     * Pubblica una nuova recensione su un ristorante a nome del Cliente
     * autenticato.
     *
     * @param dati i dati della recensione (ristorante, stelle, testo)
     * @throws ErroreServerException se il server rifiuta la richiesta
     * @throws IOException se la connessione al server fallisce
     * @throws ClassNotFoundException se la deserializzazione della risposta fallisce
     */
    public void aggiungiRecensione(AggiungiRecensioneDTO dati) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.AGGIUNGI_RECENSIONE, dati, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);
        
        if (response.getStatus() != ResponseStatus.SUCCESSO) {
            throw new ErroreServerException(response.getMessaggio());
        }
    }

    /**
     * Modifica una recensione gia' pubblicata dal Cliente autenticato.
     *
     * @param recensione i nuovi dati della recensione
     * @throws ErroreServerException se il server rifiuta la richiesta
     * @throws IOException se la connessione al server fallisce
     * @throws ClassNotFoundException se la deserializzazione della risposta fallisce
     */
    public void modificaRecensione(ModificaRecensioneDTO recensione) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.MODIFICA_RECENSIONE, recensione, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);
        
        if (response.getStatus() != ResponseStatus.SUCCESSO) {
            throw new ErroreServerException(response.getMessaggio());
        }
    }

    /**
     * Elimina una recensione pubblicata dal Cliente autenticato.
     *
     * @param idRecensione l'identificativo della recensione
     * @throws ErroreServerException se il server rifiuta la richiesta
     * @throws IOException se la connessione al server fallisce
     * @throws ClassNotFoundException se la deserializzazione della risposta fallisce
     */
    public void eliminaRecensione(IdRecensioneDTO idRecensione) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.ELIMINA_RECENSIONE, idRecensione, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);
        
        if (response.getStatus() != ResponseStatus.SUCCESSO)
            throw new ErroreServerException(response.getMessaggio());
    }

    /**
     * Recupera tutte le recensioni ricevute dai ristoranti gestiti dal
     * Ristoratore autenticato.
     *
     * @return la lista delle recensioni
     * @throws ErroreServerException se il server rifiuta la richiesta
     * @throws IOException se la connessione al server fallisce
     * @throws ClassNotFoundException se la deserializzazione della risposta fallisce
     */
    @SuppressWarnings("unchecked")
    public List<RecensioneDTO> leggiRecensioniRistorantiGestiti() throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.LEGGI_RECENSIONI_RISTORANTI_GESTITI, null, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);
        
        if (response.getStatus() == ResponseStatus.SUCCESSO) {
            return (List<RecensioneDTO>) response.getPayload();
        } else {
            throw new ErroreServerException(response.getMessaggio());
        }
    }

    /**
     * Pubblica la risposta del Ristoratore autenticato a una recensione di un
     * proprio ristorante.
     *
     * @param recensione l'identificativo della recensione e il testo della risposta
     * @throws ErroreServerException se il server rifiuta la richiesta
     * @throws IOException se la connessione al server fallisce
     * @throws ClassNotFoundException se la deserializzazione della risposta fallisce
     */
    public void rispondiRecensione(RispondiRecensioneDTO recensione) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.RISPONDI_RECENSIONE, recensione, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);
        
        if (response.getStatus() != ResponseStatus.SUCCESSO) {
            throw new ErroreServerException(response.getMessaggio());
        }
    }
}
