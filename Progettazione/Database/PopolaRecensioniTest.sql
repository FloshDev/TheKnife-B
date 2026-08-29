-- ============================================================
-- Utilizzo: popola la tabella Recensioni con recensioni fittizie
-- per circa un quinto dei ristoranti, assegnando ad alcuni di essi
-- recensioni multiple e risposte dei ristoratori. 
-- Da eseguire DOPO Schema.sql, Data.sql e DataUtentiTest.sql.
--
-- @author Scolaro Gabriele, 760123, VA
-- ============================================================



-- 1. Inserisce la prima recensione (da parte di 'cliente_test') per 1 ristorante su 5 (id_ristorante % 5 = 0)

INSERT INTO Recensioni (id_ristorante, id_cliente, titolo, testo, stelle, data_pubblicazione)
SELECT 
    r.id_ristorante,
    u.id,
    'Ottima esperienza!',
    'Servizio impeccabile e piatti davvero deliziosi. Atmosfera accogliente, ci tornerò sicuramente con amici!',
    CASE 
        WHEN r.id_ristorante % 3 = 0 THEN 5
        WHEN r.id_ristorante % 3 = 1 THEN 4
        ELSE 3
    END,
    CURRENT_TIMESTAMP - (r.id_ristorante % 30 || ' days')::INTERVAL
FROM RistorantiTheKnife r
CROSS JOIN (SELECT id FROM Utenti WHERE username = 'cliente_test') u
WHERE r.id_ristorante % 5 = 0
ON CONFLICT (id_cliente, id_ristorante) DO NOTHING;




-- 2. Inserisce la seconda recensione (da parte di 'cliente_test2') per un sottoinsieme di essi (id_ristorante % 10 = 0)
-- In questo modo molti ristoranti avranno 2 recensioni scritte da due utenti diversi

INSERT INTO Recensioni (id_ristorante, id_cliente, titolo, testo, stelle, data_pubblicazione)
SELECT 
    r.id_ristorante,
    u.id,
    'Da provare assolutamente',
    'Cucina di ottima qualità con ingredienti freschi. Qualche piccola attesa di troppo per il servizio, ma l'attesa è stata ampiamente ripagata dai sapori.',
    CASE 
        WHEN r.id_ristorante % 3 = 0 THEN 4
        WHEN r.id_ristorante % 3 = 1 THEN 3
        ELSE 5
    END,
    CURRENT_TIMESTAMP - (r.id_ristorante % 15 || ' days')::INTERVAL
FROM RistorantiTheKnife r
CROSS JOIN (SELECT id FROM Utenti WHERE username = 'cliente_test2') u
WHERE r.id_ristorante % 10 = 0
ON CONFLICT (id_cliente, id_ristorante) DO NOTHING;




-- 3. Aggiunge una risposta del ristoratore per alcune delle recensioni (es. id_ristorante % 20 = 0)
-- Questo consente di testare il corretto funzionamento della visualizzazione delle risposte nella UI

UPDATE Recensioni
SET 
    risposta = 'Grazie mille per la splendida recensione! Siamo davvero felici che abbia apprezzato la nostra accoglienza e la qualità dei nostri piatti. Speriamo di rivederla presto!',
    data_risposta = data_pubblicazione + INTERVAL '1 day'
WHERE id_ristorante % 20 = 0;







