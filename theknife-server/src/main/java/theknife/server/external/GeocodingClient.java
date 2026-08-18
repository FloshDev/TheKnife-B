package theknife.server.external;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import theknife.common.dto.PosizioneDTO;

/**
 * Client del servizio di geocoding Nominatim (OpenStreetMap). Traduce un
 * indirizzo in coordinate (geocoding diretto, decisione 29) e una coppia di
 * coordinate nel nome del luogo (reverse geocoding, decisione 26).
 * <p>
 * Rispetta la policy d'uso di Nominatim: <code>User-Agent</code> esplicito,
 * non piu' di una richiesta al secondo (limite applicato a livello di classe,
 * quindi condiviso da tutti i thread client) e timeout breve. Ogni fallimento
 * e' <b>silenzioso</b>: i metodi restituiscono <code>null</code> invece di
 * lanciare, perche' il geocoding e' sempre un ramo migliorativo e non deve
 * mai impedire l'operazione che lo ha richiesto.
 * <p>
 * Non introduce librerie esterne: usa il client HTTP del JDK e legge dalla
 * risposta JSON i soli tre campi che servono, senza un parser generale.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class GeocodingClient {

    /** Endpoint del geocoding diretto: da indirizzo a coordinate. */
    private static final String BASE_RICERCA = "https://nominatim.openstreetmap.org/search";

    /** Endpoint del reverse geocoding: da coordinate a nome del luogo. */
    private static final String BASE_REVERSE = "https://nominatim.openstreetmap.org/reverse";

    /** Richiesto dalla policy di Nominatim: le richieste anonime vengono rifiutate. */
    private static final String USER_AGENT =
        "TheKnife/1.0 (Laboratorio Interdisciplinare B - progetto universitario)";

    /**
     * Timeout di connessione e di risposta. Volutamente breve: il geocoding e'
     * un ramo migliorativo, un servizio lento non deve far attendere il client.
     */
    private static final Duration TIMEOUT = Duration.ofSeconds(2);

    /** Intervallo minimo fra due richieste, in millisecondi (policy: 1 req/s). */
    private static final long INTERVALLO_MINIMO_MS = 1000;

    /** Espressione regolare che isola la latitudine nella risposta JSON. */
    private static final Pattern CAMPO_LAT = Pattern.compile("\"lat\"\\s*:\\s*\"([^\"]+)\"");

    /** Espressione regolare che isola la longitudine nella risposta JSON. */
    private static final Pattern CAMPO_LON = Pattern.compile("\"lon\"\\s*:\\s*\"([^\"]+)\"");

    /**
     * Espressione regolare che isola il nome del luogo nella risposta JSON.
     * Tiene conto delle sequenze di escape, che nei nomi di luogo compaiono
     * spesso.
     */
    private static final Pattern CAMPO_NOME =
        Pattern.compile("\"display_name\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");

    /** Istante dell'ultima richiesta inviata, condiviso fra tutti i thread. */
    private static long ultimaRichiesta = 0;

    /** Client HTTP del JDK, riusato per tutte le richieste dell'istanza. */
    private final HttpClient http;

    /**
     * Costruisce il client con il timeout e la gestione dei redirect previsti
     * dalla policy di Nominatim.
     */
    public GeocodingClient() {
        this.http = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    }

    /**
     * Traduce un indirizzo testuale nelle sue coordinate geografiche.
     *
     * @param indirizzo l'indirizzo completo da geocodificare (via, citta',
     *                  nazione); puo' essere <code>null</code>
     * @return la posizione trovata con il nome restituito dal servizio, oppure
     *         <code>null</code> se l'indirizzo e' vuoto, il servizio non
     *         risponde o non trova alcun risultato
     */
    public PosizioneDTO geocodifica (String indirizzo) {
        if (indirizzo == null || indirizzo.isBlank()) {
            return null;
        }

        String url = BASE_RICERCA
            + "?q=" + URLEncoder.encode(indirizzo, StandardCharsets.UTF_8)
            + "&format=json&limit=1";

        String corpo = chiama(url);
        if (corpo == null) {
            return null;
        }

        Double latitudine = estraiNumero(CAMPO_LAT, corpo);
        Double longitudine = estraiNumero(CAMPO_LON, corpo);
        if (latitudine == null || longitudine == null) {
            return null;
        }

        String nome = estraiTesto(CAMPO_NOME, corpo);
        return new PosizioneDTO(latitudine, longitudine, nome != null ? nome : indirizzo);
    }

    /**
     * Traduce una coppia di coordinate nel nome leggibile del luogo
     * corrispondente (decisione 26).
     *
     * @param latitudine  la latitudine del punto
     * @param longitudine la longitudine del punto
     * @return il nome del luogo, oppure <code>null</code> se il servizio non
     *         risponde o non riconosce le coordinate
     */
    public String nomeLuogo (double latitudine, double longitudine) {
        String url = BASE_REVERSE
            + "?lat=" + latitudine
            + "&lon=" + longitudine
            + "&format=json&zoom=10";

        String corpo = chiama(url);
        if (corpo == null) {
            return null;
        }

        return estraiTesto(CAMPO_NOME, corpo);
    }

    /**
     * Esegue la richiesta HTTP rispettando l'intervallo minimo fra due
     * chiamate. Qualunque errore - rete, timeout, codice di stato diverso da
     * 200, thread interrotto - produce <code>null</code>.
     *
     * @param url l'URL completo della richiesta
     * @return il corpo della risposta, oppure <code>null</code> in caso di
     *         fallimento
     */
    private String chiama (String url) {
        try {
            attendiTurno();

            HttpRequest richiesta = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", USER_AGENT)
                .header("Accept", "application/json")
                .timeout(TIMEOUT)
                .GET()
                .build();

            HttpResponse<String> risposta =
                http.send(richiesta, HttpResponse.BodyHandlers.ofString());

            if (risposta.statusCode() != 200) {
                return null;
            }

            return risposta.body();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Blocca il thread chiamante finche' non e' trascorso l'intervallo minimo
     * dall'ultima richiesta inviata da qualunque thread.
     *
     * @throws InterruptedException se il thread viene interrotto durante l'attesa
     */
    private static synchronized void attendiTurno () throws InterruptedException {
        long adesso = System.currentTimeMillis();
        long trascorso = adesso - ultimaRichiesta;

        if (trascorso < INTERVALLO_MINIMO_MS) {
            Thread.sleep(INTERVALLO_MINIMO_MS - trascorso);
        }

        ultimaRichiesta = System.currentTimeMillis();
    }

    /**
     * Estrae un valore numerico dal corpo JSON.
     *
     * @param campo l'espressione regolare che isola il valore
     * @param corpo il corpo della risposta
     * @return il numero letto, oppure <code>null</code> se assente o non valido
     */
    private static Double estraiNumero (Pattern campo, String corpo) {
        String testo = estraiTesto(campo, corpo);
        if (testo == null) {
            return null;
        }
        try {
            return Double.valueOf(testo);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Estrae un valore testuale dal corpo JSON.
     *
     * @param campo il campo da isolare
     * @param corpo il corpo della risposta
     * @return il testo letto, oppure <code>null</code> se il campo non compare
     */
    private static String estraiTesto (Pattern campo, String corpo) {
        Matcher m = campo.matcher(corpo);
        return m.find() ? m.group(1) : null;
    }
}
