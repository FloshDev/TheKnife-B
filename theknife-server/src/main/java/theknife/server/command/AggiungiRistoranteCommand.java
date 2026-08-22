package theknife.server.command;

import theknife.common.dto.AggiungiRistoranteDTO;
import theknife.common.dto.RistoranteDTO;
import theknife.common.dto.UtenteDTO;
import theknife.common.enums.ResponseStatus;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;
import theknife.server.exception.ApplicationException;
import theknife.server.exception.DataAccessException;
import theknife.server.exception.ValidationException;
import theknife.server.service.EsitoAggiuntaRistorante;
import theknife.server.service.RistoranteService;

/**
 * Comando AGGIUNGI_RISTORANTE: inserisce un nuovo ristorante gestito dal
 * Ristoratore autenticato e restituisce la scheda creata.
 * <p>
 * Il gestore compila solo l'indirizzo: le coordinate le calcola il server
 * geocodificando (decisione 29). Se il geocoding fallisce il ristorante viene
 * comunque creato, senza coordinate, e semplicemente non comparira' nelle
 * ricerche per vicinanza finche' non viene corretto - ma la risposta lo dice
 * esplicitamente nel messaggio (S46), invece di tornare un SUCCESSO uguale a
 * quello di un indirizzo geocodificato davvero.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class AggiungiRistoranteCommand implements Command {

    /**
     * Service dei ristoranti, a cui il comando delega la logica di dominio.
     */
    private final RistoranteService ristoranteService;

    /**
     * Costruisce il comando sul service dei ristoranti.
     *
     * @param ristoranteService il service dei ristoranti
     */
    public AggiungiRistoranteCommand(RistoranteService ristoranteService) {
        this.ristoranteService = ristoranteService;
    }

    /**
     * Inserisce il ristorante contenuto nel payload a nome del ristoratore di sessione.
     *
     * @param request la richiesta, con un {@link AggiungiRistoranteDTO} come payload
     * @param utente  il ristoratore autenticato, che diventa gestore del ristorante
     * @return SUCCESSO con il {@link RistoranteDTO} appena creato - con il
     *         messaggio che avvisa se le coordinate non sono state determinate -,
     *         ERRORE_VALIDAZIONE se un campo obbligatorio manca, ERRORE se il
     *         ristorante non e' rileggibile dopo l'inserimento, ERRORE_SERVER se l'accesso al database fallisce
     */
    @Override
    public Response execute(Request request, UtenteDTO utente) {
        if (!(request.getPayload() instanceof AggiungiRistoranteDTO dati)) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null,
                    "Dati del ristorante mancanti o non validi.");
        }

        try {
            EsitoAggiuntaRistorante esito = ristoranteService.aggiungi(dati, utente.getIdUtente());

            String messaggio = esito.coordinateValide()
                    ? "Ristorante aggiunto."
                    : "Ristorante aggiunto, ma non e' stato possibile determinare le "
                      + "coordinate dell'indirizzo: non comparira' nelle ricerche per "
                      + "vicinanza finche' l'indirizzo non viene corretto.";

            return new Response(ResponseStatus.SUCCESSO, esito.ristorante(), messaggio);

        } catch (ValidationException e) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null, e.getMessage());
        } catch (ApplicationException e) {
            return new Response(ResponseStatus.ERRORE, null, e.getMessage());
        } catch (DataAccessException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, e.getMessage());
        } catch (Exception e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null,
                    "Errore durante l'inserimento del ristorante: " + e.getMessage());
        }
    }
}
