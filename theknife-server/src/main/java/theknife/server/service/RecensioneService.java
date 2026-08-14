package theknife.server.service;

import java.util.List;
import theknife.common.dto.AggiungiRecensioneDTO;
import theknife.common.dto.IdRecensioneDTO;
import theknife.common.dto.IdRistoranteDTO;
import theknife.common.dto.ModificaRecensioneDTO;
import theknife.common.dto.RecensioneDTO;
import theknife.common.dto.RispondiRecensioneDTO;
import theknife.server.dao.RecensioneDAO;
import theknife.server.exception.ApplicationException;
import theknife.server.exception.DataAccessException;
import theknife.server.exception.UnauthorizedException;
import theknife.server.exception.ValidationException;

/**
 * Service delle recensioni: incapsula la logica di dominio per la lettura, la
 * scrittura e la risposta alle recensioni. Non verifica la proprieta' delle
 * operazioni: la decisione 20 la colloca nel singolo Command, che risale
 * all'autore tramite {@link #ottieniRecensione(long)}.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class RecensioneService {

    private static final int STELLE_MINIME = 1;
    private static final int STELLE_MASSIME = 5;

    private final RecensioneDAO recensioneDAO;

    public RecensioneService(RecensioneDAO recensioneDAO) {
        this.recensioneDAO = recensioneDAO;
    }

    public List<RecensioneDTO> leggiRecensioni (IdRistoranteDTO id)
            throws ValidationException, UnauthorizedException, ApplicationException,
                   DataAccessException {

        if (id == null) {
            throw new ValidationException("Identificativo del ristorante mancante.");
        }

        return recensioneDAO.trovaPerRistorante(id.getIdRistorante());
    }

    public List<RecensioneDTO> recensioniRistorantiGestiti (long idGestore)
            throws ValidationException, UnauthorizedException, ApplicationException,
                   DataAccessException {

        return recensioneDAO.trovaPerGestore(idGestore);
    }

    public RecensioneDTO ottieniRecensione (long idRecensione)
            throws ValidationException, UnauthorizedException, ApplicationException,
                   DataAccessException {

        RecensioneDTO recensione = recensioneDAO.trovaPerId(idRecensione);
        if (recensione == null) {
            throw new ApplicationException("Recensione non trovata.");
        }
        return recensione;
    }

    public void aggiungiRecensione (AggiungiRecensioneDTO dati, long idUtente)
            throws ValidationException, UnauthorizedException, ApplicationException,
                   DataAccessException {

        if (dati == null) {
            throw new ValidationException("Dati della recensione mancanti.");
        }
        validaStelle(dati.getStelle());
        if (dati.getTesto() == null || dati.getTesto().isBlank()) {
            throw new ValidationException("Il testo della recensione non puo' essere vuoto.");
        }

        recensioneDAO.inserisci(dati, idUtente);
    }

    public void modificaRecensione (ModificaRecensioneDTO dati)
            throws ValidationException, UnauthorizedException, ApplicationException,
                   DataAccessException {

        if (dati == null) {
            throw new ValidationException("Dati della recensione mancanti.");
        }
        validaStelle(dati.getNuoveStelle());
        if (dati.getNuovoTesto() == null || dati.getNuovoTesto().isBlank()) {
            throw new ValidationException("Il testo della recensione non puo' essere vuoto.");
        }

        recensioneDAO.aggiorna(dati);
    }

    public void eliminaRecensione (IdRecensioneDTO id)
            throws ValidationException, UnauthorizedException, ApplicationException,
                   DataAccessException {

        if (id == null) {
            throw new ValidationException("Identificativo della recensione mancante.");
        }

        recensioneDAO.elimina(id.getIdRecensione());
    }

    public void rispondiRecensione (RispondiRecensioneDTO dati)
            throws ValidationException, UnauthorizedException, ApplicationException,
                   DataAccessException {

        if (dati == null) {
            throw new ValidationException("Dati della risposta mancanti.");
        }
        if (dati.getRisposta() == null || dati.getRisposta().isBlank()) {
            throw new ValidationException("La risposta non puo' essere vuota.");
        }

        recensioneDAO.rispondi(dati);
    }

    private void validaStelle (int stelle) throws ValidationException {
        if (stelle < STELLE_MINIME || stelle > STELLE_MASSIME) {
            throw new ValidationException("Le stelle devono essere comprese tra "
                    + STELLE_MINIME + " e " + STELLE_MASSIME + ".");
        }
    }

}
