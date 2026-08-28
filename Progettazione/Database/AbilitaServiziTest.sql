-- ============================================================
-- Utilizzo: abilita prenotazione online e consegna a domicilio
-- su un sottoinsieme di ristoranti, sparsi su tutte le città.
-- Il dataset Michelin originale (Data.sql) li ha TUTTI a FALSE:
-- senza questo script i filtri "Prenotazione online"/"Consegna
-- a domicilio" non restituiscono mai risultati, in nessuna
-- ricerca. Da eseguire DOPO Data.sql.
-- ============================================================

UPDATE RistorantiTheKnife SET prenotazione_online = TRUE
    WHERE id_ristorante % 5 = 0;

UPDATE RistorantiTheKnife SET consegna_a_domicilio = TRUE
    WHERE id_ristorante % 7 = 0;

-- ~1 ristorante su 5 con prenotazione online, ~1 su 7 con consegna a
-- domicilio, una piccola parte con entrambi — id_ristorante è sequenziale
-- sull'ordine di inserimento del CSV, non raggruppato per città, quindi la
-- copertura è distribuita su tutte le città del dataset, non concentrata.
