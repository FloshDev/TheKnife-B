package theknife.server.service;

import theknife.common.dto.RistoranteDTO;

/**
 * Esito dell'inserimento di un nuovo ristorante: la scheda creata piu'
 * l'informazione se il geocoding dell'indirizzo ha davvero prodotto delle
 * coordinate (S46).
 * <p>
 * Serve perche' {@link RistoranteDTO} porta latitudine e longitudine come
 * <code>double</code> primitivi: un ristorante inserito senza coordinate non
 * resta "senza dato", torna al client con 0.0/0.0 - un punto nel Golfo di
 * Guinea - senza che nulla lo distingua da un indirizzo geocodificato davvero.
 * Il service sa se il geocoding e' fallito, questo tipo porta quel fatto fino
 * al comando, che lo riferisce al client nel messaggio della risposta.
 * <p>
 * E' un tipo interno del server: non viaggia sul socket, il payload della
 * risposta resta il solo {@link RistoranteDTO}.
 *
 * @param ristorante       la scheda del ristorante appena creato
 * @param coordinateValide <code>false</code> se il geocoding non ha prodotto coordinate
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public record EsitoAggiuntaRistorante(RistoranteDTO ristorante, boolean coordinateValide) {
}
