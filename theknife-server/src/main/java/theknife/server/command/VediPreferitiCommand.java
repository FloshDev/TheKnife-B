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
 * Comando VEDI_PREFERITI: restituisce i ristoranti preferiti del Cliente
 * autenticato. Non ha payload: l'identificativo del cliente arriva
 * dall'utente di sessione.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class VediPreferitiCommand implements Command {

    private final RistoranteService ristoranteService;

    public VediPreferitiCommand(RistoranteService ristoranteService) {
        this.ristoranteService = ristoranteService;
    }

    @Override
    public Response execute(Request request, UtenteDTO utente) {
        try {
            List<RistoranteDTO> preferiti =
                ristoranteService.ottieniPreferiti(utente.getIdUtente());

            return new Response(ResponseStatus.SUCCESSO, preferiti, "Preferiti recuperati.");

        } catch (DataAccessException e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, e.getMessage());
        } catch (Exception e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null,
                    "Errore durante il recupero dei preferiti: " + e.getMessage());
        }
    }
}
