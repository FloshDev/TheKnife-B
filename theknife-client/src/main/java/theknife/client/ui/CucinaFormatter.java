package theknife.client.ui;

import java.util.Arrays;
import java.util.stream.Collectors;

import theknife.common.util.CucinaTranslationService;

/**
 * Traduce in italiano, solo per la visualizzazione, il tipo di cucina di un
 * ristorante — la colonna {@code tipo_cucina} arriva dal server in inglese
 * (dataset Michelin) e può essere composta da più termini separati da
 * virgola (es. "Creative, Contemporary").
 * <p>
 * Non tocca mai il DTO/dato reale: solo la stringa mostrata a schermo.
 *
 * @author Barlera Marco, 760000, VA
 */
public class CucinaFormatter {

    /** Costruttore privato: solo metodi statici, la classe non va istanziata. */
    private CucinaFormatter() {
    }

    /**
     * Traduce ogni termine (separato da virgola) in italiano, mantenendo la
     * stessa composizione. Un termine non trovato nel dizionario resta
     * invariato ({@link CucinaTranslationService#translateToItalian} non
     * fallisce mai).
     *
     * @param tipoCucina il valore grezzo restituito dal server, o {@code null}
     * @return il tipo di cucina tradotto, o stringa vuota se {@code tipoCucina} è {@code null}/vuoto
     */
    public static String italiano(String tipoCucina) {
        if (tipoCucina == null || tipoCucina.isBlank()) {
            return "";
        }
        return Arrays.stream(tipoCucina.split(","))
            .map(String::trim)
            .filter(termine -> !termine.isEmpty())
            .map(CucinaFormatter::traduciECapitalizza)
            .collect(Collectors.joining(", "));
    }

    /**
     * Traduce un singolo termine e ne capitalizza la prima lettera, per
     * restare coerenti con lo stile del testo originale in inglese
     * (`CucinaTranslationService` lavora tutto in minuscolo).
     *
     * @param termine il singolo termine, già senza spazi ai bordi
     * @return il termine tradotto e capitalizzato
     */
    private static String traduciECapitalizza(String termine) {
        String tradotto = CucinaTranslationService.translateToItalian(termine);
        if (tradotto.isEmpty()) {
            return tradotto;
        }
        return Character.toUpperCase(tradotto.charAt(0)) + tradotto.substring(1);
    }
}
