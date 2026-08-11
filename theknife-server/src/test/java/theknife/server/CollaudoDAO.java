package theknife.server;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import theknife.common.dto.CercaRistorantiDTO;
import theknife.common.dto.RistoranteDTO;
import theknife.server.dao.DatabaseManager;
import theknife.server.dao.RecensioneDAO;
import theknife.server.dao.RistoranteDAO;
import theknife.server.dao.ServizioDAO;
import theknife.server.dao.UtenteDAO;

/**
 * Collaudo usa-e-getta del layer DAO: verifica che le query di Scolaro
 * girino davvero contro dbTK e che il mapping ResultSet -> DTO sia corretto.
 *
 * NON fa parte del progetto. Da cancellare a collaudo finito, da non committare.
 */
public class CollaudoDAO {

    public static void main(String[] args) {

        ConfigurazioneDB config = new ConfigurazioneDB("localhost", "tk_app", "TheKnife-B");
        DatabaseManager db = new DatabaseManager(config);

        ServizioDAO servizioDAO = new ServizioDAO(db);
        UtenteDAO utenteDAO = new UtenteDAO(db);
        RistoranteDAO ristoranteDAO = new RistoranteDAO(db);
        RecensioneDAO recensioneDAO = new RecensioneDAO(db);

        try {
            System.out.println("--- 1. ServizioDAO.tutti() ---");
            int servizi = servizioDAO.tutti().size();
            System.out.println("servizi: " + servizi + "   [atteso 20]");

            System.out.println("--- 2. UtenteDAO.usernameEsiste() ---");
            boolean esiste = utenteDAO.usernameEsiste("pippo");
            System.out.println("usernameEsiste(pippo): " + esiste + "   [atteso false]");

            System.out.println("--- 3. RistoranteDAO.cerca() senza filtri ---");
            CercaRistorantiDTO filtriVuoti =
                new CercaRistorantiDTO(null, null, null, null, 0, null, null);
            List<RistoranteDTO> trovati = ristoranteDAO.cerca(filtriVuoti);
            System.out.println("ristoranti: " + trovati.size()
                + "   [atteso 17737 - se 0, LEFT JOIN sbagliato]");

            System.out.println("--- 4. duplicati e servizi ---");
            Set<Long> ids = new HashSet<>();
            int conServizi = 0;
            for (RistoranteDTO r : trovati) {
                ids.add(r.getIdRistorante());
                if (r.getServizi() != null && !r.getServizi().isEmpty()) {
                    conServizi++;
                }
            }
            System.out.println("id distinti: " + ids.size() + "   [deve essere uguale al punto 3]");
            System.out.println("con almeno un servizio: " + conServizi + "   [atteso > 0]");

            if (!trovati.isEmpty()) {
                RistoranteDTO primo = trovati.get(0);
                System.out.println("primo risultato: " + primo.getNome() + " (" + primo.getCitta() + ")"
                    + " | stelle " + primo.getMediaStelle()
                    + " | recensioni " + primo.getNumeroRecensioni()
                    + " | servizi " + (primo.getServizi() == null ? "null" : primo.getServizi().size()));
            }

            System.out.println("--- 5. RistoranteDAO.trovaPerId(1) ---");
            RistoranteDTO uno = ristoranteDAO.trovaPerId(1);
            System.out.println(uno == null ? "NULL" : uno.getNome() + " (" + uno.getCitta() + ")");

            System.out.println("--- 6. RecensioneDAO.trovaPerRistorante(1) ---");
            System.out.println("recensioni: " + recensioneDAO.trovaPerRistorante(1).size()
                + "   [atteso 0]");

            System.out.println("--- collaudo concluso ---");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
