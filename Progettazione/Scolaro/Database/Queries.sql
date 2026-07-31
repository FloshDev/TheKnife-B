-- 1. RICERCA RISTORANTI (guest e utenti registrati)

-- cercaRistorantePerLuogo(): ricerca per locazione geografica (obbligatoria)
SELECT id_ristorante, nome, nazione, citta, indirizzo, latitudine, longitudine,
       fascia_prezzo, delivery, prenotazione_online, tipo_cucina, telefono, website,
       premi, servizi, descrizione
FROM RistorantiTheKnife
WHERE citta ILIKE '%' || ? || '%' OR nazione ILIKE '%' || ? || '%';

-- cercaRistorantePerCucina(): ricerca per tipologia di cucina
SELECT id_ristorante, nome, nazione, citta, indirizzo, latitudine, longitudine,
       fascia_prezzo, delivery, prenotazione_online, tipo_cucina, telefono, website,
       premi, servizi, descrizione
FROM RistorantiTheKnife
WHERE tipo_cucina ILIKE '%' || ? || '%';

-- cercaRistorantePerFasciaPrezzo(): ricerca per fascia di prezzo
-- il filtro avviene sul numero di simboli (1 = economico, 4 = molto caro),
-- indipendentemente dalla valuta
SELECT id_ristorante, nome, nazione, citta, indirizzo, latitudine, longitudine,
       fascia_prezzo, delivery, prenotazione_online, tipo_cucina, telefono, website,
       premi, servizi, descrizione
FROM RistorantiTheKnife
WHERE LENGTH(fascia_prezzo) <= ?;

-- cercaRistorantePerFasciaPrezzo(): fascia compresa tra un minimo e un massimo
SELECT id_ristorante, nome, nazione, citta, indirizzo, latitudine, longitudine,
       fascia_prezzo, delivery, prenotazione_online, tipo_cucina, telefono, website,
       premi, servizi, descrizione
FROM RistorantiTheKnife
WHERE LENGTH(fascia_prezzo) BETWEEN ? AND ?;

-- cercaRistorantePerDelivery(): disponibilita' del servizio di delivery
SELECT id_ristorante, nome, nazione, citta, indirizzo, latitudine, longitudine,
       fascia_prezzo, delivery, prenotazione_online, tipo_cucina, telefono, website,
       premi, servizi, descrizione
FROM RistorantiTheKnife
WHERE delivery = TRUE;

-- cercaRistorantePerPrenotazioneOnline(): disponibilita' della prenotazione online
SELECT id_ristorante, nome, nazione, citta, indirizzo, latitudine, longitudine,
       fascia_prezzo, delivery, prenotazione_online, tipo_cucina, telefono, website,
       premi, servizi, descrizione
FROM RistorantiTheKnife
WHERE prenotazione_online = TRUE;

-- cercaRistorantePerMediaStelle(): ricerca in base alla media delle stelle
SELECT r.id_ristorante, r.nome, r.nazione, r.citta, r.indirizzo, r.fascia_prezzo,
       r.tipo_cucina, r.premi, r.servizi,
       AVG(rec.stelle) AS media_stelle, COUNT(rec.id_recensione) AS numero_recensioni
FROM RistorantiTheKnife r
LEFT JOIN Recensioni rec ON r.id_ristorante = rec.id_ristorante
GROUP BY r.id_ristorante
HAVING AVG(rec.stelle) >= ?
ORDER BY media_stelle DESC;

-- cercaRistoranteCombinata(): combinazione dei criteri di ricerca precedenti.
-- Le condizioni vengono concatenate dinamicamente in Java in base ai filtri
-- selezionati dall'utente; la locazione geografica e' sempre obbligatoria.
SELECT r.id_ristorante, r.nome, r.nazione, r.citta, r.indirizzo, r.fascia_prezzo,
       r.delivery, r.prenotazione_online, r.tipo_cucina, r.premi, r.servizi,
       AVG(rec.stelle) AS media_stelle, COUNT(rec.id_recensione) AS numero_recensioni
FROM RistorantiTheKnife r
LEFT JOIN Recensioni rec ON r.id_ristorante = rec.id_ristorante
WHERE (r.citta ILIKE '%' || ? || '%' OR r.nazione ILIKE '%' || ? || '%')
  AND (? = '' OR r.tipo_cucina ILIKE '%' || ? || '%')
  AND (? = 0 OR LENGTH(r.fascia_prezzo) <= ?)
  AND (? = FALSE OR r.delivery = TRUE)
  AND (? = FALSE OR r.prenotazione_online = TRUE)
GROUP BY r.id_ristorante
HAVING (? = 0 OR AVG(rec.stelle) >= ?)
ORDER BY r.nome;

-- ristorantiVicini(): ristoranti vicini alle coordinate indicate
-- (formula di Haversine, distanza in km). Parametri: lat, lon, lat, limite.
SELECT id_ristorante, nome, nazione, citta, indirizzo, latitudine, longitudine,
       fascia_prezzo, delivery, prenotazione_online, tipo_cucina, premi, servizi,
       (6371 * acos(LEAST(1.0,
          cos(radians(?)) * cos(radians(latitudine)) *
          cos(radians(longitudine) - radians(?)) +
          sin(radians(?)) * sin(radians(latitudine))))) AS distanza_km
FROM RistorantiTheKnife
WHERE latitudine IS NOT NULL AND longitudine IS NOT NULL
ORDER BY distanza_km
LIMIT ?;


-- 2. CONSULTAZIONE

-- visualizzaRistorante(): dettagli di un singolo ristorante
SELECT id_ristorante, nome, nazione, citta, indirizzo, latitudine, longitudine,
       fascia_prezzo, delivery, prenotazione_online, tipo_cucina, telefono, website,
       premi, servizi, descrizione, username_gestore
FROM RistorantiTheKnife
WHERE id_ristorante = ?;

-- visualizzaRecensioni() - Query A: numero recensioni e media stelle
SELECT COUNT(*) AS numero_recensioni, COALESCE(AVG(stelle), 0) AS media_stelle
FROM Recensioni
WHERE id_ristorante = ?;

-- visualizzaRecensioni() - Query B: elenco delle recensioni
SELECT id_recensione, username_cliente, stelle, commento, data_recensione,
       risposta, data_risposta
FROM Recensioni
WHERE id_ristorante = ?
ORDER BY data_recensione DESC;


-- 3. REGISTRAZIONE E AUTENTICAZIONE


-- checkUsernameEsistente(): verifica che lo username non sia gia' in uso
SELECT 1 FROM Utenti WHERE username = ?;

-- checkEmailEsistente(): verifica che l'email non sia gia' in uso
SELECT 1 FROM Utenti WHERE email = ?;

-- registrazione(): inserimento di un nuovo utente
INSERT INTO Utenti (username, password, nome, cognome, email, data_nascita, domicilio, ruolo)
VALUES (?, ?, ?, ?, ?, ?, ?, ?);

-- login(): recupero dati utente per l'autenticazione
-- (il confronto della password avviene lato applicazione)
SELECT username, password, nome, cognome, email, data_nascita, domicilio, ruolo
FROM Utenti
WHERE username = ?;


-- 4. PREFERITI (clienti registrati)


-- isPreferito(): verifica se il ristorante e' gia' nei preferiti del cliente
SELECT COUNT(*) FROM Preferiti
WHERE username_cliente = ? AND id_ristorante = ?;

-- aggiungiPreferito(): aggiunta di un ristorante ai preferiti
INSERT INTO Preferiti (username_cliente, id_ristorante) VALUES (?, ?);

-- rimuoviPreferito(): rimozione di un ristorante dai preferiti
DELETE FROM Preferiti
WHERE username_cliente = ? AND id_ristorante = ?;

-- visualizzaPreferiti(): elenco dei ristoranti preferiti del cliente
SELECT r.id_ristorante, r.nome, r.nazione, r.citta, r.indirizzo, r.fascia_prezzo,
       r.tipo_cucina, r.premi, r.servizi
FROM Preferiti p
JOIN RistorantiTheKnife r ON p.id_ristorante = r.id_ristorante
WHERE p.username_cliente = ?
ORDER BY r.nome;


-- 5. RECENSIONI (clienti registrati)


-- verificaEsistenzaRecensione(): controlla se il cliente ha gia' recensito
-- il ristorante (supporto per messaggio d'errore, il vincolo UNIQUE resta attivo)
SELECT 1 FROM Recensioni
WHERE username_cliente = ? AND id_ristorante = ?;

-- aggiungiRecensione(): inserimento di una recensione
INSERT INTO Recensioni (id_ristorante, username_cliente, stelle, commento)
VALUES (?, ?, ?, ?);

-- modificaRecensione(): modifica della propria recensione
UPDATE Recensioni
SET stelle = ?, commento = ?, data_recensione = CURRENT_TIMESTAMP
WHERE id_recensione = ? AND username_cliente = ?;

-- eliminaRecensione(): cancellazione della propria recensione
DELETE FROM Recensioni
WHERE id_recensione = ? AND username_cliente = ?;

-- visualizzaMieRecensioni(): recensioni inserite dal cliente
SELECT rec.id_recensione, rec.id_ristorante, r.nome AS nome_ristorante,
       rec.stelle, rec.commento, rec.data_recensione, rec.risposta, rec.data_risposta
FROM Recensioni rec
JOIN RistorantiTheKnife r ON rec.id_ristorante = r.id_ristorante
WHERE rec.username_cliente = ?
ORDER BY rec.data_recensione DESC;


-- 6. GESTORI


-- aggiungiRistorante(): inserimento di un nuovo ristorante da parte del gestore
INSERT INTO RistorantiTheKnife
(nome, nazione, citta, indirizzo, latitudine, longitudine, fascia_prezzo,
 delivery, prenotazione_online, tipo_cucina, telefono, website, premi, servizi,
 descrizione, username_gestore)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
RETURNING id_ristorante;

-- modificaRistorante(): aggiornamento dei dati di un proprio ristorante
UPDATE RistorantiTheKnife
SET nome = ?, nazione = ?, citta = ?, indirizzo = ?, latitudine = ?, longitudine = ?,
    fascia_prezzo = ?, delivery = ?, prenotazione_online = ?, tipo_cucina = ?,
    telefono = ?, website = ?, premi = ?, servizi = ?, descrizione = ?
WHERE id_ristorante = ? AND username_gestore = ?;

-- eliminaRistorante(): cancellazione di un proprio ristorante
-- (recensioni e preferiti collegati vengono eliminati a cascata)
DELETE FROM RistorantiTheKnife
WHERE id_ristorante = ? AND username_gestore = ?;

-- visualizzaMieiRistoranti(): elenco dei ristoranti inseriti dal gestore
SELECT id_ristorante, nome, nazione, citta, indirizzo, fascia_prezzo, tipo_cucina, premi
FROM RistorantiTheKnife
WHERE username_gestore = ?
ORDER BY nome;

-- visualizzaRiepilogo(): numero recensioni e media stelle dei propri ristoranti
SELECT r.id_ristorante, r.nome,
       COUNT(rec.id_recensione) AS numero_recensioni,
       COALESCE(AVG(rec.stelle), 0) AS media_stelle
FROM RistorantiTheKnife r
LEFT JOIN Recensioni rec ON r.id_ristorante = rec.id_ristorante
WHERE r.username_gestore = ?
GROUP BY r.id_ristorante, r.nome
ORDER BY r.nome;

-- visualizzaRecensioniGestore(): recensioni ricevute dai propri ristoranti
SELECT r.id_ristorante, r.nome AS nome_ristorante,
       rec.id_recensione, rec.username_cliente, rec.stelle, rec.commento,
       rec.data_recensione, rec.risposta, rec.data_risposta
FROM RistorantiTheKnife r
JOIN Recensioni rec ON r.id_ristorante = rec.id_ristorante
WHERE r.username_gestore = ?
ORDER BY r.id_ristorante, rec.data_recensione DESC;

-- verificaRispostaEsistente(): controlla se il gestore ha gia' risposto
-- alla recensione (al massimo una risposta per ogni recensione)
SELECT 1 FROM Recensioni
WHERE id_recensione = ?
  AND id_ristorante IN (SELECT id_ristorante FROM RistorantiTheKnife WHERE username_gestore = ?)
  AND risposta IS NOT NULL;

-- rispostaRecensioni(): risposta del gestore a una recensione
-- l'UPDATE fallisce se la risposta e' gia' presente (vincolo: max una risposta)
UPDATE Recensioni
SET risposta = ?, data_risposta = CURRENT_TIMESTAMP
WHERE id_recensione = ?
  AND id_ristorante IN (SELECT id_ristorante FROM RistorantiTheKnife WHERE username_gestore = ?)
  AND risposta IS NULL;
