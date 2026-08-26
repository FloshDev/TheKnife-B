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

    /**
     * Cerca i ristoranti che soddisfano i filtri indicati (nome, cucina, fascia
     * di prezzo, delivery, prenotazione, ecc.).
     *
     * @param filtri i criteri di ricerca
     * @return la lista dei ristoranti trovati
     * @throws ErroreServerException se il server rifiuta la richiesta
     * @throws IOException se la connessione al server fallisce
     * @throws ClassNotFoundException se la deserializzazione della risposta fallisce
     */
    @SuppressWarnings("unchecked")
    public List<RistoranteDTO> cercaRistoranti(CercaRistorantiDTO filtri) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.CERCA_RISTORANTI, filtri, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() == ResponseStatus.SUCCESSO) {
            return (List<RistoranteDTO>) response.getPayload();
        } else {
            throw new ErroreServerException(response.getMessaggio());
        }
    }

    /**
     * Cerca i ristoranti vicini a una posizione entro un raggio di distanza
     * ("Vicino a me").
     *
     * @param filtri la posizione di riferimento e il raggio di ricerca
     * @return la lista dei ristoranti trovati, ordinati per distanza
     * @throws ErroreServerException se il server rifiuta la richiesta
     * @throws IOException se la connessione al server fallisce
     * @throws ClassNotFoundException se la deserializzazione della risposta fallisce
     */
    @SuppressWarnings("unchecked")
    public List<RistoranteDTO> cercaVicino(CercaVicinoDTO filtri) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.CERCA_VICINO, filtri, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() == ResponseStatus.SUCCESSO) {
            return (List<RistoranteDTO>) response.getPayload();
        } else {
            throw new ErroreServerException(response.getMessaggio());
        }
    }

    /**
     * Recupera i dettagli completi di un ristorante, comprese le recensioni.
     *
     * @param id l'identificativo del ristorante
     * @return i dettagli del ristorante
     * @throws ErroreServerException se il ristorante non esiste o la richiesta viene rifiutata
     * @throws IOException se la connessione al server fallisce
     * @throws ClassNotFoundException se la deserializzazione della risposta fallisce
     */
    public RistoranteDTO ottieniDettagli(IdRistoranteDTO id) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.OTTIENI_DETTAGLI_RISTORANTE, id, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() == ResponseStatus.SUCCESSO) {
            return (RistoranteDTO) response.getPayload();
        } else {
            throw new ErroreServerException(response.getMessaggio());
        }
    }

    /**
     * Richiede al server la localita' iniziale stimata dall'indirizzo IP del
     * client, proposta all'Ospite prima della ricerca.
     *
     * @return la posizione stimata
     * @throws ErroreServerException se il server non riesce a stimare la posizione
     * @throws IOException se la connessione al server fallisce
     * @throws ClassNotFoundException se la deserializzazione della risposta fallisce
     */
    public PosizioneDTO ottieniLocalitaIniziale() throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.OTTIENI_LOCALITA_INIZIALE, null, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() == ResponseStatus.SUCCESSO) {
            return (PosizioneDTO) response.getPayload();
        } else {
            throw new ErroreServerException(response.getMessaggio());
        }
    }

    /**
     * Aggiunge un ristorante alla lista preferiti del Cliente autenticato.
     *
     * @param id l'identificativo del ristorante
     * @throws ErroreServerException se il server rifiuta la richiesta
     * @throws IOException se la connessione al server fallisce
     * @throws ClassNotFoundException se la deserializzazione della risposta fallisce
     */
    public void aggiungiPreferito(IdRistoranteDTO id) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.AGGIUNGI_PREFERITO, id, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() != ResponseStatus.SUCCESSO)
            throw new ErroreServerException(response.getMessaggio());
    }

    /**
     * Rimuove un ristorante dalla lista preferiti del Cliente autenticato.
     *
     * @param id l'identificativo del ristorante
     * @throws ErroreServerException se il server rifiuta la richiesta
     * @throws IOException se la connessione al server fallisce
     * @throws ClassNotFoundException se la deserializzazione della risposta fallisce
     */
    public void rimuoviPreferito(IdRistoranteDTO id) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.RIMUOVI_PREFERITO, id, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() != ResponseStatus.SUCCESSO)
            throw new ErroreServerException(response.getMessaggio());    
    }

    /**
     * Recupera la lista preferiti del Cliente autenticato.
     *
     * @return la lista dei ristoranti preferiti
     * @throws ErroreServerException se il server rifiuta la richiesta
     * @throws IOException se la connessione al server fallisce
     * @throws ClassNotFoundException se la deserializzazione della risposta fallisce
     */
    @SuppressWarnings("unchecked")
    public List<RistoranteDTO> ottieniPreferiti() throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.VEDI_PREFERITI, null, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() == ResponseStatus.SUCCESSO) {
            return (List<RistoranteDTO>) response.getPayload();
        } else {
            throw new ErroreServerException(response.getMessaggio());
        }
    }

    /**
     * Inserisce un nuovo ristorante nel sistema, associandolo al Ristoratore
     * autenticato come gestore.
     *
     * @param ristorante i dati del nuovo ristorante
     * @return il ristorante creato
     * @throws ErroreServerException se il server rifiuta la richiesta
     * @throws IOException se la connessione al server fallisce
     * @throws ClassNotFoundException se la deserializzazione della risposta fallisce
     */
    public RistoranteDTO aggiungiRistorante(AggiungiRistoranteDTO ristorante) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.AGGIUNGI_RISTORANTE, ristorante, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() == ResponseStatus.SUCCESSO) {
            return (RistoranteDTO) response.getPayload();
        } else {
            throw new ErroreServerException(response.getMessaggio());
        }
    }

    /**
     * Recupera la lista dei ristoranti gestiti dal Ristoratore autenticato.
     *
     * @return la lista dei ristoranti gestiti
     * @throws ErroreServerException se il server rifiuta la richiesta
     * @throws IOException se la connessione al server fallisce
     * @throws ClassNotFoundException se la deserializzazione della risposta fallisce
     */
    @SuppressWarnings("unchecked")
    public List<RistoranteDTO> vediRistorantiGestiti() throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.VEDI_RISTORANTI_GESTITI, null, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() == ResponseStatus.SUCCESSO) {
            return (List<RistoranteDTO>) response.getPayload();
        } else {
            throw new ErroreServerException(response.getMessaggio());
        }
    }

    /**
     * Associa il Ristoratore autenticato come gestore di un ristorante
     * esistente non ancora gestito.
     *
     * @param ristorante l'identificativo del ristorante
     * @throws ErroreServerException se il ristorante e' gia' gestito o la richiesta viene rifiutata
     * @throws IOException se la connessione al server fallisce
     * @throws ClassNotFoundException se la deserializzazione della risposta fallisce
     */
    public void associaRistorante(IdRistoranteDTO ristorante) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.ASSOCIA_RISTORANTE, ristorante, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() != ResponseStatus.SUCCESSO)
            throw new ErroreServerException(response.getMessaggio());
    }

    /**
     * Elimina un ristorante gestito dal Ristoratore autenticato.
     *
     * @param id l'identificativo del ristorante
     * @throws ErroreServerException se il server rifiuta la richiesta
     * @throws IOException se la connessione al server fallisce
     * @throws ClassNotFoundException se la deserializzazione della risposta fallisce
     */
    public void eliminaRistorante(IdRistoranteDTO id) throws IOException, ClassNotFoundException {
        Request request = new Request(CommandType.ELIMINA_RISTORANTE, id, connection.getSessionToken());
        Response response = connection.inviaRichiesta(request);

        if (response.getStatus() != ResponseStatus.SUCCESSO)
            throw new ErroreServerException(response.getMessaggio());
    }
}