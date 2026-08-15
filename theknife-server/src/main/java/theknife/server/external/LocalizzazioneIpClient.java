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
 * Ricava una posizione approssimata dall'indirizzo IP pubblico della macchina
 * su cui gira il server, per precompilare il campo localita' dello splash
 * (decisione 18).
 * <p>
 * <b>Limite noto e accettato.</b> Il servizio geolocalizza il chiamante, cioe'
 * il <i>server</i>, non il client: coincide quando client e server girano
 * sulla stessa macchina o sulla stessa rete, cioe' lo scenario di demo e di
 * collaudo. Su postazioni remote il suggerimento indica la zona del server. La
 * decisione 18 registra gia' che questo valore e' solo un suggerimento
 * precompilato e che il percorso valido resta la digitazione manuale del
 * luogo: se la stima e' assente o sbagliata, l'utente la corregge e
 * l'applicazione funziona identica.
 * <p>
 * Come tutto il package, non lancia mai: restituisce <code>null</code> quando
 * il servizio non risponde. Su rete privata o senza uscita verso Internet il
 * fallimento e' la norma, non un'anomalia.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class LocalizzazioneIpClient {

    private static final String ENDPOINT = "http://ip-api.com/json/?fields=status,lat,lon,city";

    private static final Duration TIMEOUT = Duration.ofSeconds(2);

    private static final Pattern CAMPO_STATO = Pattern.compile("\"status\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern CAMPO_LAT = Pattern.compile("\"lat\"\\s*:\\s*(-?[0-9.]+)");
    private static final Pattern CAMPO_LON = Pattern.compile("\"lon\"\\s*:\\s*(-?[0-9.]+)");
    private static final Pattern CAMPO_CITTA = Pattern.compile("\"city\"\\s*:\\s*\"([^\"]*)\"");

    private final HttpClient http;

    public LocalizzazioneIpClient() {
        this.http = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    }

    /**
     * Stima la posizione corrente a partire dall'indirizzo IP pubblico.
     *
     * @return la posizione stimata con il nome della citta', oppure
     *         <code>null</code> se il servizio non risponde, non riconosce
     *         l'indirizzo o la macchina non ha accesso a Internet
     */
    public PosizioneDTO posizioneStimata () {
        try {
            HttpRequest richiesta = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
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
