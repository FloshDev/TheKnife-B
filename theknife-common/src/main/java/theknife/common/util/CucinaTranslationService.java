package theknife.common.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Servizio per la traduzione dei tipi di cucina tra italiano e inglese.
 * Utilizza una mappa statica di 278 termini per la traduzione bidirezionale.
 * 
 * @author Scolaro Gabriele, 760123, VA
 */
public class CucinaTranslationService {

    private static final Map<String, String> IT_TO_EN;
    private static final Map<String, String> EN_TO_IT;

    static {
        Map<String, String> itToEn = new HashMap<>();
        // Cucine composte (multi-parola)
        itToEn.put("americana contemporanea", "american contemporary");
        itToEn.put("asiatica e occidentale", "asian and western");
        itToEn.put("asiatica contemporanea", "asian contemporary");
        itToEn.put("influenze asiatiche", "asian influences");
        itToEn.put("australiana contemporanea", "australian contemporary");
        itToEn.put("cucina pechinese", "beijing cuisine");
        itToEn.put("britannica contemporanea", "british contemporary");
        itToEn.put("carni arrosto cantonesi", "cantonese roast meats");
        itToEn.put("asiatica centrale", "central asian");
        itToEn.put("chao zhou", "chao zhou");
        itToEn.put("specialita' di pollo", "chicken specialities");
        itToEn.put("cinese contemporanea", "chinese contemporary");
        itToEn.put("chiu chow", "chiu chow");
        itToEn.put("cucina classica", "classic cuisine");
        itToEn.put("francese classica", "classic french");
        itToEn.put("cucina casereccia", "country cooking");
        itToEn.put("specialita' di granchio", "crab specialities");
        itToEn.put("britannica creativa", "creative british");
        itToEn.put("francese creativa", "creative french");
        itToEn.put("cucina abruzzese", "cuisine from abruzzo");
        itToEn.put("cucina lucana", "cuisine from basilicata");
        itToEn.put("cucina laziale", "cuisine from lazio");
        itToEn.put("cucina romagnola", "cuisine from romagna");
        itToEn.put("cucina del sud-ovest della francia", "cuisine from south west france");
        itToEn.put("cucina valtellinese", "cuisine from valtellina");
        itToEn.put("specialita' di anatra", "duck specialities");
        itToEn.put("dell'europa dell'est", "eastern european");
        itToEn.put("cucina emiratina", "emirati cuisine");
        itToEn.put("europea contemporanea", "european contemporary");
        itToEn.put("dal produttore alla tavola", "farm to table");
        itToEn.put("francese contemporanea", "french contemporary");
        itToEn.put("hang zhou", "hang zhou");
        itToEn.put("cucina casalinga", "home cooking");
        itToEn.put("cucina hui", "hui cuisine");
        itToEn.put("indiana vegetariana", "indian vegetarian");
        itToEn.put("italiana e giapponese", "italian and japanese");
        itToEn.put("italiana contemporanea", "italian contemporary");
        itToEn.put("giapponese contemporanea", "japanese contemporary");
        itToEn.put("steakhouse giapponese", "japanese steakhouse");
        itToEn.put("coreana contemporanea", "korean contemporary");
        itToEn.put("carne e griglia", "meats and grills");
        itToEn.put("carne e pesce", "meats and seafood");
        itToEn.put("cucina mediterranea", "mediterranean cuisine");
        itToEn.put("mediorientale", "middle eastern");
        itToEn.put("britannica moderna", "modern british");
        itToEn.put("cucina moderna", "modern cuisine");
        itToEn.put("francese moderna", "modern french");
        itToEn.put("noodles e congee", "noodles and congee");
        itToEn.put("tailandese del nord", "northern thai");
        itToEn.put("portoricana", "puerto rican");
        itToEn.put("cucina regionale", "regional cuisine");
        itToEn.put("europea regionale", "regional european");
        itToEn.put("piatti di riso", "rice dishes");
        itToEn.put("cucina stagionale", "seasonal cuisine");
        itToEn.put("shun tak", "shun tak");
        itToEn.put("singaporiana e malese", "singaporean and malaysian");
        itToEn.put("stuzzichini", "small eats");
        itToEn.put("sudafricana", "south african");
        itToEn.put("sudamericana", "south american");
        itToEn.put("dell'asia del sud-est", "south east asian");
        itToEn.put("tailandese del sud", "southern thai");
        itToEn.put("spagnola contemporanea", "spanish contemporary");
        itToEn.put("sri lankese", "sri lankan");
        itToEn.put("cibo di strada", "street food");
        itToEn.put("taiwanese contemporanea", "taiwanese contemporary");
        itToEn.put("tailandese contemporanea", "thai contemporary");
        itToEn.put("britannica tradizionale", "traditional british");
        itToEn.put("cucina tradizionale", "traditional cuisine");
        itToEn.put("vietnamita contemporanea", "vietnamese contemporary");
        itToEn.put("cucina dal mondo", "world cuisine");

        itToEn.put("afgana", "afghan");
        itToEn.put("africana", "african");
        itToEn.put("albanese", "albanian");
        itToEn.put("algerina", "algerian");
        itToEn.put("americana", "american");
        itToEn.put("anglo-indiana", "anglo-indian");
        itToEn.put("arab", "arab");
        itToEn.put("araba", "arab");
        itToEn.put("argentina", "argentine");
        itToEn.put("armena", "armenian");
        itToEn.put("asiatica", "asian");
        itToEn.put("australiana", "australian");
        itToEn.put("austriaca", "austrian");
        itToEn.put("azera", "azerbaijani");
        itToEn.put("bagnerese", "bagnerese");
        itToEn.put("balcanica", "balkan");
        itToEn.put("baltica", "baltic");
        itToEn.put("bangladese", "bangladeshi");
        itToEn.put("basca", "basque");
        itToEn.put("bavarese", "bavarian");
        itToEn.put("belga", "belgian");
        itToEn.put("bengalese", "bengali");
        itToEn.put("berbera", "berber");
        itToEn.put("bielorussa", "belarusian");
        itToEn.put("birmana", "burmese");
        itToEn.put("boliviana", "bolivian");
        itToEn.put("bosniaca", "bosnian");
        itToEn.put("brasiliana", "brazilian");
        itToEn.put("bretone", "breton");
        itToEn.put("britannica", "british");
        itToEn.put("bulgara", "bulgarian");
        itToEn.put("birmana", "burmese");
        itToEn.put("cajun", "cajun");
        itToEn.put("cambogiana", "cambodian");
        itToEn.put("canadese", "canadian");
        itToEn.put("cantinese", "cantonese");
        itToEn.put("caraibica", "caribbean");
        itToEn.put("catalana", "catalan");
        itToEn.put("ceca", "czech");
        itToEn.put("cecoslovacca", "czechoslovakian");
        itToEn.put("chilena", "chilean");
        itToEn.put("cinese", "chinese");
        itToEn.put("cipriota", "cypriot");
        itToEn.put("colombiana", "colombian");
        itToEn.put("comoriana", "comorian");
        itToEn.put("congolese", "congolese");
        itToEn.put("contemporanea", "contemporary");
        itToEn.put("coreana", "korean");
        itToEn.put("corse", "corsican");
        itToEn.put("costaricana", "costa rican");
        itToEn.put("creativa", "creative");
        itToEn.put("creola", "creole");
        itToEn.put("croata", "croatian");
        itToEn.put("cubana", "cuban");
        itToEn.put("danese", "danish");
        itToEn.put("del medio oriente", "middle eastern");
        itToEn.put("del sud", "southern");
        itToEn.put("dell'africa occidentale", "west african");
        itToEn.put("dell'africa orientale", "east african");
        itToEn.put("dell'europa centrale", "central european");
        itToEn.put("dell'europa orientale", "eastern european");
        itToEn.put("dell'america centrale", "central american");
        itToEn.put("dell'america latina", "latin american");
        itToEn.put("dell'america meridionale", "south american");
        itToEn.put("dell'america settentrionale", "north american");
        itToEn.put("dell'asia centrale", "central asian");
        itToEn.put("dell'asia meridionale", "south asian");
        itToEn.put("dell'asia orientale", "east asian");
        itToEn.put("dell'asia sudorientale", "southeast asian");
        itToEn.put("dell'atlantico", "atlantic");
        itToEn.put("del pacifico", "pacific");
        itToEn.put("del mediterraneo", "mediterranean");
        itToEn.put("dominicana", "dominican");
        itToEn.put("egiziana", "egyptian");
        itToEn.put("emiliana", "emilian");
        itToEn.put("equadoregna", "ecuadorian");
        itToEn.put("eritrea", "eritrean");
        itToEn.put("estoniana", "estonian");
        itToEn.put("etiopica", "ethiopian");
        itToEn.put("europea", "european");
        itToEn.put("filippina", "filipino");
        itToEn.put("finlandese", "finnish");
        itToEn.put("fiore", "fiorentine");
        itToEn.put("fiorentina", "fiorentine");
        itToEn.put("francese", "french");
        itToEn.put("fusion", "fusion");
        itToEn.put("gabonese", "gabonese");
        itToEn.put("georgiana", "georgian");
        itToEn.put("germanica", "german");
        itToEn.put("ghanese", "ghanaian");
        itToEn.put("giamaicana", "jamaican");
        itToEn.put("giapponese", "japanese");
        itToEn.put("greca", "greek");
        itToEn.put("guatemalteca", "guatemalan");
        itToEn.put("guineana", "guinean");
        itToEn.put("hawaiana", "hawaiian");
        itToEn.put("indiana", "indian");
        itToEn.put("indonesiana", "indonesian");
        itToEn.put("inglese", "english");
        itToEn.put("internazionale", "international");
        itToEn.put("iraniana", "iranian");
        itToEn.put("irakena", "iraqi");
        itToEn.put("irlandese", "irish");
        itToEn.put("israeliana", "israeli");
        itToEn.put("italiana", "italian");
        itToEn.put("ivoriana", "ivorian");
        itToEn.put("giamaicana", "jamaican");
        itToEn.put("giapponese", "japanese");
        itToEn.put("ebraica", "jewish");
        itToEn.put("kashmiriana", "kashmiri");
        itToEn.put("keniota", "kenyan");
        itToEn.put("khmer", "khmer");
        itToEn.put("lao", "lao");
        itToEn.put("latina", "latin");
        itToEn.put("lettone", "latvian");
        itToEn.put("libanese", "lebanese");
        itToEn.put("ligure", "ligurian");
        itToEn.put("lituana", "lithuanian");
        itToEn.put("lussemburghese", "luxembourgish");
        itToEn.put("macedone", "macedonian");
        itToEn.put("malgascia", "malagasy");
        itToEn.put("malese", "malaysian");
        itToEn.put("maldiviana", "maldivian");
        itToEn.put("maltese", "maltese");
        itToEn.put("marocchina", "moroccan");
        itToEn.put("messicana", "mexican");
        itToEn.put("moldava", "moldovan");
        itToEn.put("mongola", "mongolian");
        itToEn.put("marocchina", "moroccan");
        itToEn.put("mozambicana", "mozambican");
        itToEn.put("birmana", "burmese");
        itToEn.put("nepalese", "nepalese");
        itToEn.put("neozelandese", "new zealand");
        itToEn.put("nicaraguense", "nicaraguan");
        itToEn.put("nigeriana", "nigerian");
        itToEn.put("norvegese", "norwegian");
        itToEn.put("novozelandese", "new zealand");
        itToEn.put("olandese", "dutch");
        itToEn.put("pachistana", "pakistani");
        itToEn.put("palestinese", "palestinian");
        itToEn.put("panamense", "panamanian");
        itToEn.put("papuana", "papuan");
        itToEn.put("paraguaiana", "paraguayan");
        itToEn.put("peruviana", "peruvian");
        itToEn.put("polacca", "polish");
        itToEn.put("portoghese", "portuguese");
        itToEn.put("prussiana", "prussian");
        itToEn.put("rumena", "romanian");
        itToEn.put("russa", "russian");
        itToEn.put("ruandese", "rwandan");
        itToEn.put("samoana", "samoan");
        itToEn.put("sarda", "sardinian");
        itToEn.put("scozzese", "scottish");
        itToEn.put("senegalese", "senegalese");
        itToEn.put("serba", "serbian");
        itToEn.put("siciliana", "sicilian");
        itToEn.put("singaporiana", "singaporean");
        itToEn.put("slovacca", "slovak");
        itToEn.put("slovena", "slovenian");
        itToEn.put("somala", "somalian");
        itToEn.put("spagnola", "spanish");
        itToEn.put("sri lankese", "sri lankan");
        itToEn.put("sudafricana", "south african");
        itToEn.put("sudamericana", "south american");
        itToEn.put("sudanese", "sudanese");
        itToEn.put("svedese", "swedish");
        itToEn.put("svizzera", "swiss");
        itToEn.put("siriana", "syrian");
        itToEn.put("taiwanese", "taiwanese");
        itToEn.put("tailandese", "thai");
        itToEn.put("tamil", "tamil");
        itToEn.put("tanzaniana", "tanzanian");
        itToEn.put("tedesca", "german");
        itToEn.put("thai", "thai");
        itToEn.put("tibetana", "tibetan");
        itToEn.put("togolese", "togolese");
        itToEn.put("trinitense", "trinidadian");
        itToEn.put("tunisina", "tunisian");
        itToEn.put("turca", "turkish");
        itToEn.put("ucraina", "ukrainian");
        itToEn.put("ugandese", "ugandan");
        itToEn.put("ungherese", "hungarian");
        itToEn.put("uruguaiana", "uruguayan");
        itToEn.put("uzbeka", "uzbek");
        itToEn.put("venezuelana", "venezuelan");
        itToEn.put("vietnamita", "vietnamese");
        itToEn.put("yemenita", "yemeni");
        itToEn.put("yugo-slava", "yugoslav");
        itToEn.put("zambiana", "zambian");
        itToEn.put("zimbaweana", "zimbabwean");
        itToEn.put("zulù", "zulu");
        // Sinonimi e varianti comuni
        itToEn.put("cucina italiana", "italian");
        itToEn.put("cucina cinese", "chinese");
        itToEn.put("cucina giapponese", "japanese");
        itToEn.put("cucina indiana", "indian");
        itToEn.put("cucina messicana", "mexican");
        itToEn.put("cucina thai", "thai");
        itToEn.put("cucina vietnamita", "vietnamese");
        itToEn.put("cucina greca", "greek");
        itToEn.put("cucina mediorientale", "middle eastern");
        itToEn.put("cucina africana", "african");
        itToEn.put("cucina latinoamericana", "latin american");
        itToEn.put("cucina mediterranea", "mediterranean");
        itToEn.put("cucina fusion", "fusion");
        itToEn.put("cucina vegetariana", "vegetarian");
        itToEn.put("cucina vegana", "vegan");
        itToEn.put("cucina senza glutine", "gluten-free");
        itToEn.put("street food", "street food");
        itToEn.put("fast food", "fast food");
        itToEn.put("pizzeria", "pizza");
        itToEn.put("gelateria", "ice cream");
        itToEn.put("pasticceria", "bakery");
        itToEn.put("caffetteria", "cafe");
        itToEn.put("bar", "bar");
        itToEn.put("pub", "pub");
        itToEn.put("bistrot", "bistro");
        itToEn.put("trattoria", "trattoria");
        itToEn.put("osteria", "osteria");
        itToEn.put("enoteca", "wine bar");
        itToEn.put("rosticceria", "rotisserie");
        itToEn.put("griglieria", "grill");
        itToEn.put("steakhouse", "steakhouse");
        itToEn.put("hamburgeria", "burger");
        itToEn.put("paninoteca", "sandwich shop");
        itToEn.put("piadineria", "piadina");
        itToEn.put("focacceria", "focaccia");
        itToEn.put("frittura", "fried food");
        itToEn.put("fritto misto", "mixed fry");
        itToEn.put("pesce", "seafood");
        itToEn.put("frutti di mare", "seafood");
        itToEn.put("sushi", "sushi");
        itToEn.put("sashimi", "sashimi");
        itToEn.put("ramen", "ramen");
        itToEn.put("udon", "udon");
        itToEn.put("tempura", "tempura");
        itToEn.put("yakitori", "yakitori");
        itToEn.put("okonomiyaki", "okonomiyaki");
        itToEn.put("takoyaki", "takoyaki");
        itToEn.put("dim sum", "dim sum");
        itToEn.put("bao", "bao");
        itToEn.put("ravioli cinesi", "chinese dumplings");
        itToEn.put("wonton", "wonton");
        itToEn.put("noodles", "noodles");
        itToEn.put("pho", "pho");
        itToEn.put("banh mi", "banh mi");
        itToEn.put("curry", "curry");
        itToEn.put("tandoori", "tandoori");
        itToEn.put("naan", "naan");
        itToEn.put("biryani", "biryani");
        itToEn.put("kebab", "kebab");
        itToEn.put("shawarma", "shawarma");
        itToEn.put("falafel", "falafel");
        itToEn.put("hummus", "hummus");
        itToEn.put("tabbouleh", "tabbouleh");
        itToEn.put("couscous", "couscous");
        itToEn.put("tajine", "tagine");
        itToEn.put("harira", "harira");
        itToEn.put("pastilla", "pastilla");
        itToEn.put("empanada", "empanada");
        itToEn.put("taco", "taco");
        itToEn.put("burrito", "burrito");
        itToEn.put("quesadilla", "quesadilla");
        itToEn.put("enchilada", "enchilada");
        itToEn.put("guacamole", "guacamole");
        itToEn.put("nachos", "nachos");
        itToEn.put("ceviche", "ceviche");
        itToEn.put("tiradito", "tiradito");
        itToEn.put("arepa", "arepa");
        itToEn.put("asado", "asado");
        itToEn.put("chimichurri", "chimichurri");
        itToEn.put("choripan", "choripan");
        itToEn.put("milanesa", "milanesa");
        itToEn.put("dulce de leche", "dulce de leche");
        itToEn.put("alfajor", "alfajor");
        itToEn.put("mate", "mate");
        itToEn.put("feijoada", "feijoada");
        itToEn.put("moqueca", "moqueca");
        itToEn.put("acaraje", "acaraje");
        itToEn.put("pao de queijo", "cheese bread");
        itToEn.put("brigadeiro", "brigadeiro");
        itToEn.put("caipirinha", "caipirinha");
        itToEn.put("churrasco", "churrasco");
        itToEn.put("picanha", "picanha");
        itToEn.put("linguiça", "sausage");
        itToEn.put("farofa", "farofa");
        itToEn.put("vatapa", "vatapa");
        itToEn.put("bobó de camarão", "shrimp bobó");
        itToEn.put("escondidinho", "escondidinho");
        itToEn.put("baião de dois", "baiao de dois");
        itToEn.put("carne de sol", "sun-dried beef");
        itToEn.put("queijo coalho", "coalho cheese");
        itToEn.put("tapioca", "tapioca");
        itToEn.put("cuscuz", "couscous");
        itToEn.put("cuzcuz", "couscous");
        itToEn.put("pamonha", "pamonha");
        itToEn.put("curau", "curau");
        itToEn.put("canjica", "canjica");
        itToEn.put("pé de moleque", "peanut brittle");
        itToEn.put("cocada", "cocada");
        itToEn.put("quindim", "quindim");
        itToEn.put("beijinho", "beijinho");
        itToEn.put("brigadeiro", "brigadeiro");
        itToEn.put("cajuzinho", "cashew candy");
        itToEn.put("olho de sogra", "mother-in-law's eye");
        itToEn.put("maria mole", "maria mole");
        itToEn.put("pé de moleque", "peanut brittle");
        itToEn.put("amendoim", "peanut");
        itToEn.put("castanha", "chestnut");
        itToEn.put("noce", "nut");
        itToEn.put("mandorla", "almond");
        itToEn.put("pistacchio", "pistachio");
        itToEn.put("nocciole", "hazelnut");
        itToEn.put("anacardi", "cashew");
        itToEn.put("pinoli", "pine nut");
        itToEn.put("semi", "seeds");
        itToEn.put("zucca", "pumpkin");
        itToEn.put("girasole", "sunflower");
        itToEn.put("lino", "flax");
        itToEn.put("chia", "chia");
        itToEn.put("sesamo", "sesame");
        itToEn.put("papavero", "poppy");
        IT_TO_EN = Collections.unmodifiableMap(itToEn);

        Map<String, String> enToIt = new HashMap<>();
        for (Map.Entry<String, String> entry : itToEn.entrySet()) {
            enToIt.put(entry.getValue().toLowerCase(Locale.ROOT), entry.getKey());
        }
        EN_TO_IT = Collections.unmodifiableMap(enToIt);
    }

    public CucinaTranslationService() {
        // Public constructor for instantiation
    }

    /**
     * Traduce un termine di cucina dall'italiano all'inglese.
     * Se il termine non è trovato, restituisce l'originale in minuscolo.
     *
     * @param italianTerm il termine in italiano
     * @return il termine in inglese (o l'originale se non trovato)
     */
    public static String translateToEnglish(String italianTerm) {
        if (italianTerm == null || italianTerm.isBlank()) {
            return "";
        }
        String normalized = italianTerm.trim().toLowerCase(Locale.ROOT);
        return IT_TO_EN.getOrDefault(normalized, normalized);
    }

    /**
     * Traduce un termine di cucina dall'inglese all'italiano.
     * Se il termine non è trovato, restituisce l'originale.
     *
     * @param englishTerm il termine in inglese
     * @return il termine in italiano (o l'originale se non trovato)
     */
    public static String translateToItalian(String englishTerm) {
        if (englishTerm == null || englishTerm.isBlank()) {
            return "";
        }
        String normalized = englishTerm.trim().toLowerCase(Locale.ROOT);
        return EN_TO_IT.getOrDefault(normalized, englishTerm);
    }

    /**
     * Verifica se un termine è presente nel dizionario (in italiano o inglese).
     *
     * @param term il termine da verificare
     * @return true se il termine è noto
     */
    public static boolean isKnownTerm(String term) {
        if (term == null || term.isBlank()) {
            return false;
        }
        String normalized = term.trim().toLowerCase(Locale.ROOT);
        return IT_TO_EN.containsKey(normalized) || EN_TO_IT.containsKey(normalized);
    }
}
