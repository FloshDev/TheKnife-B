package theknife.server.service;

import java.util.List;

import theknife.common.dto.CercaRistorantiDTO;
import theknife.common.dto.CercaVicinoDTO;
import theknife.common.dto.IdRistoranteDTO;
import theknife.common.dto.PosizioneDTO;
import theknife.common.dto.RistoranteDTO;
import theknife.common.dto.StatisticheRistoranteDTO;
import theknife.server.dao.PreferitoDAO;
import theknife.server.dao.RistoranteDAO;
import theknife.server.exception.ApplicationException;
import theknife.server.exception.DataAccessException;
import theknife.server.exception.UnauthorizedException;
import theknife.server.exception.ValidationException;

/**
 * Service dei ristoranti: incapsula la logica di dominio per la ricerca, la
 * consultazione e i preferiti (decisione 21). Delega l'accesso ai dati a
 * {@link RistoranteDAO} e {@link PreferitoDAO}, ricevuti dal costruttore: il
 * service non conosce il database, solo i DAO.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class RistoranteService {

    private final RistoranteDAO ristoranteDAO;
    private final PreferitoDAO preferitoDAO;

    public RistoranteService(RistoranteDAO ristoranteDAO, PreferitoDAO preferitoDAO) {
        this.ristoranteDAO = ristoranteDAO;
        this.preferitoDAO = preferitoDAO;
    }

    public List<RistoranteDTO> cercaRistoranti (CercaRistorantiDTO filtri)
            throws ValidationException, UnauthorizedException, ApplicationException,
                   DataAccessException {

        if (filtri == null) {
            throw new ValidationException("Criteri di ricerca mancanti.");
        }

        return ristoranteDAO.cerca(filtri);
    }

    public RistoranteDTO ottieniDettagli (IdRistoranteDTO id)
            throws ValidationException, UnauthorizedException, ApplicationException,
                   DataAccessException {

        if (id == null) {
            throw new ValidationException("Identificativo del ristorante mancante.");
        }

        RistoranteDTO ristorante = ristoranteDAO.trovaPerId(id.getIdRistorante());
        if (ristorante == null) {
            throw new ApplicationException("Ristorante non trovato.");
        }

        return ristorante;
    }

    public List<RistoranteDTO> cercaVicino (CercaVicinoDTO filtri)
            throws ValidationException, UnauthorizedException, ApplicationException,
                   DataAccessException {

        if (filtri == null || filtri.getLuogo() == null || filtri.getLuogo().isBlank()) {
            throw new ValidationException("Luogo di riferimento mancante.");
        }
        if (filtri.getRaggioKm() <= 0) {
            throw new ValidationException("Il raggio di ricerca deve essere maggiore di zero.");
        }

        PosizioneDTO posizione = ristoranteDAO.trovaCoordinateLuogo(filtri.getLuogo());
        if (posizione == null) {
            throw new ApplicationException("Impossibile determinare le coordinate di "
                    + filtri.getLuogo() + ".");
        }

        return ristoranteDAO.cercaVicino(posizione.getLatitudine(),
                                         posizione.getLongitudine(),
                                         filtri.getRaggioKm());
    }

    public List<RistoranteDTO> ristorantiGestiti (long idGestore)
            throws ValidationException, UnauthorizedException, ApplicationException,
                   DataAccessException {

        return ristoranteDAO.trovaPerGestore(idGestore);
    }

    public StatisticheRistoranteDTO statistiche (IdRistoranteDTO id)
            throws ValidationException, UnauthorizedException, ApplicationException,
                   DataAccessException {

        if (id == null) {
            throw new ValidationException("Identificativo del ristorante mancante.");
        }

        return ristoranteDAO.statistiche(id.getIdRistorante());
    }

    public void associaRistorante (IdRistoranteDTO id, long idGestore)
            throws ValidationException, UnauthorizedException, ApplicationException,
                   DataAccessException {

        if (id == null) {
            throw new ValidationException("Identificativo del ristorante mancante.");
        }

        ristoranteDAO.associaGestore(id.getIdRistorante(), idGestore);
    }

    public void aggiungiPreferito (long idCliente, IdRistoranteDTO id)
            throws ValidationException, UnauthorizedException, ApplicationException,
                   DataAccessException {

        if (id == null) {
            throw new ValidationException("Identificativo del ristorante mancante.");
        }

        preferitoDAO.aggiungi(idCliente, id.getIdRistorante());
    }

    public void rimuoviPreferito (long idCliente, IdRistoranteDTO id)
            throws ValidationException, UnauthorizedException, ApplicationException,
                   DataAccessException {

        if (id == null) {
            throw new ValidationException("Identificativo del ristorante mancante.");
        }

        preferitoDAO.rimuovi(idCliente, id.getIdRistorante());
    }

    public List<RistoranteDTO> ottieniPreferiti (long idCliente)
            throws ValidationException, UnauthorizedException, ApplicationException,
                   DataAccessException {

        return preferitoDAO.trovaPerCliente(idCliente);
    }

}