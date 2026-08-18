package theknife.server.external;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import theknife.common.dto.PosizioneDTO;

/**
 * Ricava una posizione approssimata da un indirizzo IP, per precompilare il
 * campo localita' dello splash (decisione 18).
 * <p>
 * <b>Quale indirizzo (decisione 30, soluzione B).</b> Si geolocalizza l'IP del
 * <i>client</i>, non quello del server: l'indirizzo del peer TCP viene scritto
 * nella {@code Request} da {@code ClientHandler} e arriva fin qui. La variante
 * senza indirizzo - il servizio geolocalizza il chiamante, cioe' la macchina
 * del server - resta come ripiego per le richieste che non lo portano, come
 * quelle di un client non ancora aggiornato.
 * <p>
 * <b>Limite noto e accettato.</b> Un indirizzo privato o dietro CGNAT non e'
 * geolocalizzabile: in rete locale, che e' lo scenario di demo e di collaudo,
 * la stima manca. La decisione 18 registra gia' che questo valore e' solo un
 * suggerimento precompilato e che il percorso valido resta la digitazione
 * manuale del luogo: se la stima e' assente o sbagliata, l'utente la corregge
 * e l'applicazione funziona identica.
 * <p>
 * Come tutto il package, non lancia mai: restituisce <code>null</code> quando
 * il servizio non risponde. Su rete privata o senza uscita verso Internet il
 * fallimento e' la norma, non un'anomalia.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class LocalizzazioneIpClient {

    /**
     * Radice dell'endpoint. Senza indirizzo il servizio geolocalizza il
     * chiamante, cioe' il server; con l'indirizzo in coda geolocalizza quello.
     */
    private static final String BASE_ENDPOINT = "http://ip-api.com/json/";

    /**
     * Lista dei soli campi che servono: chiedere meno dati riduce la risposta
     * e non espone informazioni inutili.
     */
    private static final String CAMPI = "?fields=status,lat,lon,city";

    /**
     * Timeout di connessione e di risposta. Breve per la stessa ragione del
     * geocoding: la stima e' un suggerimento, non deve far attendere il client.
     */
    private static final Duration TIMEOUT = Duration.ofSeconds(2);

    /** Espressione regolare che isola l'esito della chiamata nella risposta JSON. */
    private static final Pattern CAMPO_STATO = Pattern.compile("\"status\"\\s*:\\s*\"([^\"]+)\"");

    /** Espressione regolare che isola la latitudine nella risposta JSON. */
    private static final Pattern CAMPO_LAT = Pattern.compile("\"lat\"\\s*:\\s*(-?[0-9.]+)");

    /** Espressione regolare che isola la longitudine nella risposta JSON. */
    private static final Pattern CAMPO_LON = Pattern.compile("\"lon\"\\s*:\\s*(-?[0-9.]+)");

    /** Espressione regolare che isola il nome della citta' nella risposta JSON. */
    private static final Pattern CAMPO_CITTA = Pattern.compile("\"city\"\\s*:\\s*\"([^\"]*)\"");

    /** Client HTTP del JDK, riusato per tutte le richieste dell'istanza. */
    private final HttpClient http;

    /** Costruisce il client con il timeout breve previsto per questa stima. */
    public LocalizzazioneIpClient() {
        this.http = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    }

    /**
     * Stima la posizione della macchina su cui gira il server, a partire dal
     * suo indirizzo IP pubblico. E' il ripiego per le richieste che non
     * portano l'indirizzo del client.
     *
     * @return la posizione stimata con il nome della citta', oppure
     *         <code>null</code> se il servizio non risponde, non riconosce
     *         l'indirizzo o la macchina non ha accesso a Internet
     */
    public PosizioneDTO posizioneStimata () {
        return posizioneStimata(null);
    }

    /**
     * Stima la posizione corrispondente all'indirizzo IP indicato
     * (decisione 30). Un indirizzo assente riporta al comportamento senza
     * indirizzo, cioe' alla stima sulla macchina del server.
     *
     * @param ip l'indirizzo IP da geolocalizzare, eventualmente
     *           <code>null</code> o vuoto
     * @return la posizione stimata con il nome della citta', oppure
     *         <code>null</code> se il servizio non risponde, non riconosce
     *         l'indirizzo - come accade con gli indirizzi privati e dietro
     *         CGNAT - o la macchina non ha accesso a Internet
     */
    public PosizioneDTO posizioneStimata (String ip) {
        String endpoint = (ip == null || ip.isBlank())
            ? BASE_ENDPOINT + CAMPI
            : BASE_ENDPOINT + ip + CAMPI;

        try {
            HttpRequest richiesta = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Accept", "application/json")
                .timeout(TIMEOUT)
                .GET()
                .build();

            HttpResponse<String> risposta =
                http.send(richiesta, HttpResponse.BodyHandlers.ofString());

            if (risposta.statusCode() != 200) {
                return null;
            }

            String corpo = risposta.body();

            String stato = estrai(CAMPO_STATO, corpo);
            if (!"success".equals(stato)) {
                return null;
            }

            String latitudine = estrai(CAMPO_LAT, corpo);
            String longitudine = estrai(CAMPO_LON, corpo);
            if (latitudine == null || longitudine == null) {
                return null;
            }

            String citta = estrai(CAMPO_CITTA, corpo);

            return new PosizioneDTO(Double.parseDouble(latitudine),
                                    Double.parseDouble(longitudine),
                                    citta);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Estrae il primo valore che corrisponde al campo indicato.
     *
     * @param campo il campo da isolare
     * @param corpo il corpo della risposta
     * @return il valore letto, oppure <code>null</code> se il campo non compare
     */
    private static String estrai (Pattern campo, String corpo) {
        Matcher m = campo.matcher(corpo);
        return m.find() ? m.group(1) : null;
    }
}
