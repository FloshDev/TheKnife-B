package theknife.server.command;

import java.util.List;

import theknife.common.dto.RistoranteDTO;
import theknife.common.dto.UtenteDTO;
import theknife.common.enums.ResponseStatus;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;
import theknife.server.exception.DataAccessException;
import theknife.server.service.RistoranteService;

/**
 * Comando VEDI_RISTORANTI_GESTITI: restituisce i ristoranti del Ristoratore
 * autenticato. Non ha payload: l'identificativo del gestore arriva dall'utente
 * di sessione, il che rende impossibile chiedere i ristoranti di un altro.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class VediRistorantiGestitiCommand implements Command {

    private final RistoranteService ristoranteService;

    public VediRistorantiGestitiCommand(RistoranteService ristoranteService) {
        this.ristoranteService = ristoranteService;
    }

    @Override
    public Response execute(Request request, UtenteDTO utente) {
        try {
            List<RistoranteDTO> gestiti =
                ristoranteService.ristorantiGestiti(utente.getIdUtente());

            return new Response(ResponseStatus.SUCCESSO, gestiti,
                    "Ristoranti gestiti recuperati.");

        } catch (DataAccessException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, e.getMessage());
        } catch (Exception e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null,
                    "Errore durante il recupero dei ristoranti gestiti: " + e.getMessage());
        }
    }
}
