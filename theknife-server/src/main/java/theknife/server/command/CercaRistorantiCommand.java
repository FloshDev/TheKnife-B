package theknife.server.command;

import theknife.common.protocol.Request;
import theknife.common.protocol.Response;
import theknife.common.dto.CercaRistorantiDTO;
import java.util.List;
import theknife.common.dto.RistoranteDTO;
import theknife.common.enums.ResponseStatus;
import theknife.common.dto.UtenteDTO;
import theknife.server.service.RistoranteService;
import theknife.server.exception.ValidationException;
import theknife.server.exception.UnauthorizedException;
import theknife.server.exception.ApplicationException;

public class CercaRistorantiCommand implements Command {
    
    private RistoranteService ristoranteService;

//Costruttore
    public CercaRistorantiCommand(RistoranteService ristoranteService) {
        this.ristoranteService = ristoranteService;
    }

    @Override
    public Response execute(Request request, UtenteDTO utente) {

        CercaRistorantiDTO dto = (CercaRistorantiDTO) request.getPayload();
        try {
            List<RistoranteDTO> ristoranti = ristoranteService.cercaRistoranti(dto);
            if(!ristoranti.isEmpty()) {
                return new Response(ResponseStatus.SUCCESSO, ristoranti, "Ristoranti trovati con successo.");
            } else {
                return new Response(ResponseStatus.NON_TROVATO, ristoranti, "Nessun ristorante trovato con i criteri specificati.");
            }
        } catch (ValidationException e) {
            return new Response(ResponseStatus.ERRORE_VALIDAZIONE, null, "Errore di validazione: " + e.getMessage());
        } catch (UnauthorizedException e) {
            return new Response(ResponseStatus.NON_AUTORIZZATO, null, "Non autorizzato: " + e.getMessage());
        } catch (ApplicationException e) {
            return new Response(ResponseStatus.ERRORE, null, "Errore applicativo: " + e.getMessage());
        } catch (Exception e) {
            return new Response(ResponseStatus.ERRORE_SERVER, null, "Errore durante la ricerca dei ristoranti: " + e.getMessage());
        }
    }
}
