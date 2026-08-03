package theknife.server.service;

import java.util.List;
import java.util.ArrayList;
import theknife.common.dto.RistoranteDTO;
import theknife.common.dto.CercaRistorantiDTO;
import theknife.server.exception.ValidationException;
import theknife.server.exception.UnauthorizedException;
import theknife.server.exception.ApplicationException;

/**
 * Service dei ristoranti: incapsula la logica di dominio per la ricerca e la
 * gestione dei ristoranti. Il DAO (accesso al DB) verrà iniettato qui; per ora
 * i metodi sono stub in attesa dello schema definitivo.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class RistoranteService {

    public List<RistoranteDTO> cercaRistoranti (CercaRistorantiDTO dto)
            throws ValidationException, UnauthorizedException, ApplicationException {
        return new ArrayList<>();
    }
    
}
