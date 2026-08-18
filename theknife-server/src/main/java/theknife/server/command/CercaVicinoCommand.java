package theknife.server.command;

import java.util.List;

import theknife.common.dto.CercaVicinoDTO;
import theknife.common.dto.RistoranteDTO;
import theknife.common.dto.UtenteDTO;
import theknife.common.enums.ResponseStatus;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;
import theknife.server.exception.ApplicationException;
import theknife.server.exception.DataAccessException;
import theknife.server.exception.ValidationException;
import theknife.server.service.RistoranteService;

/**
 * Comando CERCA_VICINO: restituisce i ristoranti entro un raggio dal luogo
 * indicato. Non richiede autenticazione. Le coordinate del luogo le calcola il
 * server, il client manda solo il nome del luogo e il raggio.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class CercaVicinoCommand implements Command {

    /**
     * Service dei ristoranti, a cui il comando delega la logica di dominio.
     */
    private final RistoranteService ristoranteService;

    /**
     * Costruisce il comando sul service dei ristoranti.
     *
     * @param ristoranteService il service dei ristoranti
     */
    public CercaVicinoCommand(RistoranteService ristoranteService) {
        this.ristoranteService = ristoranteService;
    }

    /**
     * Cerca i ristoranti entro il raggio indicato dal luogo richiesto.
     *
     * @param request la richiesta, con un {@link CercaVicinoDTO} come payload
     * @param utente  ignorato: il comando non richiede autenticazione
     * @return SUCCESSO con la lista dei {@link RistoranteDTO} trovati,
     *         NON_TROVATO con lista vuota se nessuno rientra nel raggio, ERRORE
     *         se le coordinate del luogo non sono determinabili,
     *         ERRORE_VALIDAZIONE se luogo o raggio non sono validi,
     *         ERRORE_SERVER se l'accesso al database fallisce
     */
    @Override
    public Response execute(Request request, UtenteDTO utente) {
        if (!(request.getPayload() instanceof CercaVicinoDTO filtri)) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null,
                    "Criteri di ricerca per vicinanza mancanti o non validi.");
        }

        try {
            List<RistoranteDTO> ristoranti = ristoranteService.cercaVicino(filtri);

            if (ristoranti.isEmpty()) {
                return new Response(ResponseStatus.NON_TROVATO, ristoranti,
                        "Nessun ristorante trovato entro il raggio indicato.");
            }

            return new Response(ResponseStatus.SUCCESSO, ristoranti,
                    "Ristoranti trovati con successo.");

        } catch (ValidationException e) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null, e.getMessage());
        } catch (ApplicationException e) {
            return new Response(ResponseStatus.ERRORE, null, e.getMessage());
        } catch (DataAccessException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, e.getMessage());
        } catch (Exception e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null,
                    "Errore durante la ricerca per vicinanza: " + e.getMessage());
        }
    }
}
