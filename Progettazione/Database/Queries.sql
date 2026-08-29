-- ============================================================
-- Utilizzo: questo file contiene tutte le interrogazioni
-- alle varie tabelle del database.
--
-- @author Scolaro Gabriele, 760123, VA
-- ============================================================




-- ============================================================
-- 1. RICERCA RISTORANTI (guest e utenti registrati)
-- ============================================================


-- cercaRistorantePerLuogo(): ricerca per locazione geografica (obbligatoria)

SELECT r.id_ristorante, r.nome, r.nazione, r.citta, r.indirizzo, r.latitudine, r.longitudine,
       r.fascia_prezzo, r.prenotazione_online, r.consegna_a_domicilio, r.tipo_cucina,
       r.telefono, r.website, r.premi
FROM RistorantiTheKnife r
WHERE r.citta ILIKE '%' || ? || '%' OR r.nazione ILIKE '%' || ? || '%';



-- cercaRistorantePerCucina(): ricerca per tipologia di cucina

SELECT r.id_ristorante, r.nome, r.nazione, r.citta, r.indirizzo, r.latitudine, r.longitudine,
       r.fascia_prezzo, r.prenotazione_online, r.consegna_a_domicilio, r.tipo_cucina,
       r.telefono, r.website, r.premi
FROM RistorantiTheKnife r
WHERE r.tipo_cucina ILIKE '%' || ? || '%';



-- cercaRistorantePerFasciaPrezzo(): ricerca per fascia di prezzo (1 = economico,
-- 4 = molto caro)

SELECT r.id_ristorante, r.nome, r.nazione, r.citta, r.indirizzo, r.latitudine, r.longitudine,
       r.fascia_prezzo, r.prenotazione_online, r.consegna_a_domicilio, r.tipo_cucina,
       r.telefono, r.website, r.premi
FROM RistorantiTheKnife r
WHERE r.fascia_prezzo <= ?;



-- cercaRistorantePerFasciaPrezzo(): fascia compresa tra un minimo e un massimo

SELECT r.id_ristorante, r.nome, r.nazione, r.citta, r.indirizzo, r.latitudine, r.longitudine,
       r.fascia_prezzo, r.prenotazione_online, r.consegna_a_domicilio, r.tipo_cucina,
       r.telefono, r.website, r.premi
FROM RistorantiTheKnife r
WHERE r.fascia_prezzo BETWEEN ? AND ?;



-- cercaRistorantePerConsegnaADomicilio(): disponibilita' del servizio di delivery

SELECT r.id_ristorante, r.nome, r.nazione, r.citta, r.indirizzo, r.latitudine, r.longitudine,
       r.fascia_prezzo, r.prenotazione_online, r.consegna_a_domicilio, r.tipo_cucina,
       r.telefono, r.website, r.premi
FROM RistorantiTheKnife r
WHERE r.consegna_a_domicilio = TRUE;



-- cercaRistorantePerPrenotazioneOnline(): disponibilita' della prenotazione online

SELECT r.id_ristorante, r.nome, r.nazione, r.citta, r.indirizzo, r.latitudine, r.longitudine,
       r.fascia_prezzo, r.prenotazione_online, r.consegna_a_domicilio, r.tipo_cucina,
       r.telefono, r.website, r.premi
FROM RistorantiTheKnife r
WHERE r.prenotazione_online = TRUE;




-- cercaRistorantePerServizio(): ricerca per servizio offerto

SELECT r.id_ristorante, r.nome, r.nazione, r.citta, r.indirizzo, r.latitudine, r.longitudine,
       r.fascia_prezzo, r.prenotazione_online, r.consegna_a_domicilio, r.tipo_cucina,
       r.telefono, r.website, r.premi
FROM RistorantiTheKnife r
JOIN RistoranteServizio rs ON r.id_ristorante = rs.id_ristorante
JOIN Servizio s ON rs.id_servizio = s.id
WHERE s.nome = ?;




-- cercaRistorantePerMediaStelle(): ricerca in base alla media delle stelle

SELECT r.id_ristorante, r.nome, r.nazione, r.citta, r.indirizzo, r.fascia_prezzo,
       r.tipo_cucina, r.premi,
       AVG(rec.stelle) AS media_stelle, COUNT(rec.id_recensione) AS numero_recensioni
FROM RistorantiTheKnife r
LEFT JOIN Recensioni rec ON r.id_ristorante = rec.id_ristorante
GROUP BY r.id_ristorante
HAVING AVG(rec.stelle) >= ?
ORDER BY media_stelle DESC;




-- cercaRistoranteCombinata(): combinazione dei criteri di ricerca.
-- Le condizioni vengono concatenate dinamicamente in Java; la locazione e' sempre obbligatoria.

SELECT r.id_ristorante, r.nome, r.nazione, r.citta, r.indirizzo, r.fascia_prezzo,
       r.prenotazione_online, r.consegna_a_domicilio, r.tipo_cucina, r.premi,
       AVG(rec.stelle) AS media_stelle, COUNT(rec.id_recensione) AS numero_recensioni
FROM RistorantiTheKnife r
LEFT JOIN Recensioni rec ON r.id_ristorante = rec.id_ristorante
WHERE (r.citta ILIKE '%' || ? || '%' OR r.nazione ILIKE '%' || ? || '%')
  AND (? = '' OR r.tipo_cucina ILIKE '%' || ? || '%')
  AND (? = 0 OR r.fascia_prezzo <= ?)
  AND (? = FALSE OR r.consegna_a_domicilio = TRUE)
  AND (? = FALSE OR r.prenotazione_online = TRUE)
GROUP BY r.id_ristorante
HAVING (? = 0 OR AVG(rec.stelle) >= ?)
ORDER BY r.nome;



-- ristorantiVicini(): ristoranti vicini alle coordinate indicate
-- (formula di Haversine, distanza in km). Parametri: lat, lon, lat, raggioKm).

SELECT t.id_ristorante, t.nome, t.nazione, t.citta, t.provincia, t.indirizzo,
       t.latitudine, t.longitudine, t.fascia_prezzo, t.prenotazione_online,
       t.consegna_a_domicilio, t.tipo_cucina, t.telefono, t.website, t.premi,
       t.id_gestore, COALESCE(t.media_stelle, 0) AS media_stelle,
       t.numero_recensioni
FROM (
  SELECT r.id_ristorante, r.nome, r.nazione, r.citta, r.provincia, r.indirizzo,
         r.latitudine, r.longitudine, r.fascia_prezzo, r.prenotazione_online,
         r.consegna_a_domicilio, r.tipo_cucina, r.telefono, r.website, r.premi,
         r.id_gestore, AVG(rec.stelle) AS media_stelle,
         COUNT(rec.id_recensione) AS numero_recensioni,
         (6371 * acos(LEAST(1.0,
            cos(radians(?)) * cos(radians(r.latitudine)) *
            cos(radians(r.longitudine) - radians(?)) +
            sin(radians(?)) * sin(radians(r.latitudine))))) AS distanza_km
  FROM RistorantiTheKnife r
  LEFT JOIN Recensioni rec ON r.id_ristorante = rec.id_ristorante
  WHERE r.latitudine IS NOT NULL AND r.longitudine IS NOT NULL
  GROUP BY r.id_ristorante
) t
WHERE t.distanza_km <= ?
ORDER BY t.distanza_km;



-- ============================================================
-- 2. CONSULTAZIONE
-- ============================================================



-- visualizzaRistorante(): dettagli di un singolo ristorante (con i suoi servizi)

SELECT r.id_ristorante, r.nome, r.nazione, r.citta, r.provincia, r.indirizzo,
       r.latitudine, r.longitudine, r.fascia_prezzo, r.prenotazione_online,
       r.consegna_a_domicilio, r.tipo_cucina, r.telefono, r.website, r.premi, r.id_gestore
FROM RistorantiTheKnife r
WHERE r.id_ristorante = ?;




-- visualizzaServiziRistorante(): servizi offerti da un ristorante

SELECT s.id, s.nome
FROM Servizio s
JOIN RistoranteServizio rs ON s.id = rs.id_servizio
WHERE rs.id_ristorante = ?;



-- visualizzaRecensioni() - Query A: numero recensioni e media stelle

SELECT COUNT(*) AS numero_recensioni, COALESCE(AVG(stelle), 0) AS media_stelle
FROM Recensioni
WHERE id_ristorante = ?;



-- visualizzaRecensioni() - Query B: elenco delle recensioni

SELECT id_recensione, id_ristorante, id_cliente, titolo, testo, stelle,
       data_pubblicazione, risposta, data_risposta
FROM Recensioni
WHERE id_ristorante = ?
ORDER BY data_pubblicazione DESC;




-- ============================================================
-- 3. REGISTRAZIONE E AUTENTICAZIONE
-- ============================================================



-- checkUsernameEsistente(): verifica che lo username non sia gia' in uso

SELECT 1 FROM Utenti WHERE username = ?;



-- checkEmailEsistente(): verifica che l'email non sia gia' in uso

SELECT 1 FROM Utenti WHERE email = ?;



-- registrazione(): inserimento di un nuovo utente (id generato via SERIAL)

INSERT INTO Utenti (username, password, nome, cognome, email, data_nascita, domicilio, ruolo)
VALUES (?, ?, ?, ?, ?, ?, ?, ?);



-- login(): recupero dati utente per l'autenticazione (id per le FK successive)

SELECT id, username, password, nome, cognome, email, data_nascita, domicilio, ruolo
FROM Utenti
WHERE username = ?;




-- ============================================================
-- 4. PREFERITI (clienti registrati)
-- ============================================================



-- isPreferito(): verifica se il ristorante e' gia' nei preferiti del cliente

SELECT COUNT(*) FROM Preferiti
WHERE id_cliente = ? AND id_ristorante = ?;



-- aggiungiPreferito(): aggiunta di un ristorante ai preferiti

INSERT INTO Preferiti (id_cliente, id_ristorante) VALUES (?, ?);



-- rimuoviPreferito(): rimozione di un ristorante dai preferiti

DELETE FROM Preferiti
WHERE id_cliente = ? AND id_ristorante = ?;



-- visualizzaPreferiti(): elenco dei ristoranti preferiti del cliente

SELECT r.id_ristorante, r.nome, r.nazione, r.citta, r.provincia, r.indirizzo,
       r.latitudine, r.longitudine, r.fascia_prezzo, r.prenotazione_online,
       r.consegna_a_domicilio, r.tipo_cucina, r.telefono, r.website, r.premi,
       r.id_gestore, COALESCE(AVG(rec.stelle), 0) AS media_stelle,
       COUNT(rec.id_recensione) AS numero_recensioni
FROM Preferiti p
JOIN RistorantiTheKnife r ON p.id_ristorante = r.id_ristorante
LEFT JOIN Recensioni rec ON r.id_ristorante = rec.id_ristorante
WHERE p.id_cliente = ?
GROUP BY r.id_ristorante ORDER BY r.nome;




-- ============================================================
-- 5. RECENSIONI (clienti registrati)
-- ============================================================



-- verificaEsistenzaRecensione(): controlla se il cliente ha gia' recensito il ristorante

SELECT 1 FROM Recensioni
WHERE id_cliente = ? AND id_ristorante = ?;



-- aggiungiRecensione(): inserimento di una recensione

INSERT INTO Recensioni (id_ristorante, id_cliente, titolo, testo, stelle)
VALUES (?, ?, ?, ?, ?);



-- modificaRecensione(): modifica della propria recensione

UPDATE Recensioni
SET titolo = ?, testo = ?, stelle = ?, data_pubblicazione = CURRENT_TIMESTAMP
WHERE id_recensione = ?;



-- eliminaRecensione(): cancellazione della propria recensione

DELETE FROM Recensioni
WHERE id_recensione = ?;



-- visualizzaMieRecensioni(): recensioni inserite dal cliente

SELECT rec.id_recensione, rec.id_ristorante, r.nome AS nome_ristorante,
       rec.titolo, rec.testo, rec.stelle, rec.data_pubblicazione, rec.risposta, rec.data_risposta
FROM Recensioni rec
JOIN RistorantiTheKnife r ON rec.id_ristorante = r.id_ristorante
WHERE rec.id_cliente = ?
ORDER BY rec.data_pubblicazione DESC;




-- ============================================================
-- 6. GESTORI (RISTORATORI)
-- ============================================================



-- aggiungiRistorante(): inserimento di un nuovo ristorante da parte del gestore

INSERT INTO RistorantiTheKnife
(nome, nazione, citta, provincia, indirizzo, latitudine, longitudine, fascia_prezzo,
 prenotazione_online, consegna_a_domicilio, tipo_cucina, telefono, website, premi, id_gestore)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
RETURNING id_ristorante;



-- modificaRistorante(): aggiornamento dei dati di un proprio ristorante

UPDATE RistorantiTheKnife
SET nome = ?, nazione = ?, citta = ?, provincia = ?, indirizzo = ?, latitudine = ?,
    longitudine = ?, fascia_prezzo = ?, prenotazione_online = ?, consegna_a_domicilio = ?,
    tipo_cucina = ?, telefono = ?, website = ?, premi = ?
WHERE id_ristorante = ? AND id_gestore = ?;



-- eliminaRistorante(): cancellazione di un proprio ristorante
-- (recensioni e preferiti collegati vengono eliminati in cascata)

DELETE FROM RistorantiTheKnife
WHERE id_ristorante = ? AND id_gestore = ?;



-- visualizzaMieiRistoranti(): elenco dei ristoranti inseriti dal gestore

SELECT id_ristorante, nome, nazione, citta, indirizzo, fascia_prezzo, tipo_cucina, premi
FROM RistorantiTheKnife
WHERE id_gestore = ?
ORDER BY nome;



-- visualizzaRiepilogo(): numero recensioni e media stelle dei propri ristoranti

SELECT r.id_ristorante, r.nome,
       COUNT(rec.id_recensione) AS numero_recensioni,
       COALESCE(AVG(rec.stelle), 0) AS media_stelle
FROM RistorantiTheKnife r
LEFT JOIN Recensioni rec ON r.id_ristorante = rec.id_ristorante
WHERE r.id_gestore = ?
GROUP BY r.id_ristorante, r.nome
ORDER BY r.nome;



-- visualizzaRecensioniGestore(): recensioni ricevute dai propri ristoranti

SELECT r.id_ristorante, r.nome AS nome_ristorante,
       rec.id_recensione, rec.id_cliente, rec.titolo, rec.testo, rec.stelle,
       rec.data_pubblicazione, rec.risposta, rec.data_risposta
FROM RistorantiTheKnife r
JOIN Recensioni rec ON r.id_ristorante = rec.id_ristorante
WHERE r.id_gestore = ?
ORDER BY r.id_ristorante, rec.data_pubblicazione DESC;



-- verificaRispostaEsistente(): controlla se il gestore ha gia' risposto alla recensione

SELECT 1 FROM Recensioni
WHERE id_recensione = ?
  AND id_ristorante IN (SELECT id_ristorante FROM RistorantiTheKnife WHERE id_gestore = ?)
  AND risposta IS NOT NULL;



-- rispostaRecensioni(): risposta del gestore a una recensione (max una risposta)

UPDATE Recensioni
SET risposta = ?, data_risposta = CURRENT_TIMESTAMP
WHERE id_recensione = ?
  AND id_ristorante IN (SELECT id_ristorante FROM RistorantiTheKnife WHERE id_gestore = ?)
  AND risposta IS NULL;







