package theknife.server.service;

import java.util.List;
import theknife.common.dto.AggiungiRecensioneDTO;
import theknife.common.dto.IdRecensioneDTO;
import theknife.common.dto.IdRistoranteDTO;
import theknife.common.dto.ModificaRecensioneDTO;
import theknife.common.dto.RecensioneDTO;
import theknife.common.dto.RispondiRecensioneDTO;
import theknife.common.dto.RistoranteDTO;
import theknife.server.dao.RecensioneDAO;
import theknife.server.dao.RistoranteDAO;
import theknife.server.exception.ApplicationException;
import theknife.server.exception.DataAccessException;
import theknife.server.exception.UnauthorizedException;
import theknife.server.exception.ValidationException;

/**
 * Service delle recensioni: incapsula la logica di dominio per la lettura, la
 * scrittura e la risposta alle recensioni. Verifica qui la proprieta' delle
 * operazioni (decisione 24), perche' i metodi di aggiornamento di
 * {@link RecensioneDAO} filtrano solo per identificativo. Usa
 * {@link RistoranteDAO} per risalire dal ristorante al suo gestore.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class RecensioneService {

    private static final int STELLE_MINIME = 1;
    private static final int STELLE_MASSIME = 5;

    private final RecensioneDAO recensioneDAO;
    private final RistoranteDAO ristoranteDAO;

    public RecensioneService(RecensioneDAO recensioneDAO, RistoranteDAO ristoranteDAO) {
        this.recensioneDAO = recensioneDAO;
        this.ristoranteDAO = ristoranteDAO;
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

    public void modificaRecensione (ModificaRecensioneDTO dati, long idUtente)
            throws ValidationException, UnauthorizedException, ApplicationException,
                   DataAccessException {

        if (dati == null) {
            throw new ValidationException("Dati della recensione mancanti.");
        }
        validaStelle(dati.getNuoveStelle());
        if (dati.getNuovoTesto() == null || dati.getNuovoTesto().isBlank()) {
            throw new ValidationException("Il testo della recensione non puo' essere vuoto.");
        }

        RecensioneDTO recensione = caricaRecensione(dati.getIdRecensione());
        if (recensione.getIdUtente() != idUtente) {
            throw new UnauthorizedException("La recensione appartiene a un altro utente.");
        }

        recensioneDAO.aggiorna(dati);
    }

    public void eliminaRecensione (IdRecensioneDTO id, long idUtente)
            throws ValidationException, UnauthorizedException, ApplicationException,
                   DataAccessException {

        if (id == null) {
            throw new ValidationException("Identificativo della recensione mancante.");
        }

        RecensioneDTO recensione = caricaRecensione(id.getIdRecensione());
        if (recensione.getIdUtente() != idUtente) {
            throw new UnauthorizedException("La recensione appartiene a un altro utente.");
        }

        recensioneDAO.elimina(id.getIdRecensione());
    }

    public void rispondiRecensione (RispondiRecensioneDTO dati, long idGestore)
            throws ValidationException, UnauthorizedException, ApplicationException,
                   DataAccessException {

        if (dati == null) {
            throw new ValidationException("Dati della risposta mancanti.");
        }
        if (dati.getRisposta() == null || dati.getRisposta().isBlank()) {
            throw new ValidationException("La risposta non puo' essere vuota.");
        }

        RecensioneDTO recensione = caricaRecensione(dati.getIdRecensione());

        RistoranteDTO ristorante = ristoranteDAO.trovaPerId(recensione.getIdRistorante());
        if (ristorante == null) {
            throw new ApplicationException("Ristorante non trovato.");
        }
        if (ristorante.getIdGestore() == null || ristorante.getIdGestore() != idGestore) {
            throw new UnauthorizedException("Il ristorante e' gestito da un altro utente.");
        }

        recensioneDAO.rispondi(dati);
    }

    private RecensioneDTO caricaRecensione (long idRecensione)
            throws ApplicationException, DataAccessException {

        RecensioneDTO recensione = recensioneDAO.trovaPerId(idRecensione);
        if (recensione == null) {
            throw new ApplicationException("Recensione non trovata.");
        }
        return recensione;
    }

    private void validaStelle (int stelle) throws ValidationException {
        if (stelle < STELLE_MINIME || stelle > STELLE_MASSIME) {
            throw new ValidationException("Le stelle devono essere comprese tra "
                    + STELLE_MINIME + " e " + STELLE_MASSIME + ".");
        }
    }

}
