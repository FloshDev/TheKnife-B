-- ============================================================
-- Utilizzo: popola la tabella Utenti con utenti di test per la
-- fase di accesso. Da eseguire DOPO Schema.sql e Data.sql.
-- Credenziali di tutti gli utenti: username / password
--
-- @author Scolaro Gabriele, 760123, VA
-- ============================================================

INSERT INTO Utenti (username, password, nome, cognome, email, ruolo) VALUES
    ('cliente_test',  '$2a$12$LodzHvtBCDgAJmhfb6qg9e8RpVj9A0gqVQB2BUOSnegpBa4STsVGi', 'Mario',   'Rossi',   'cliente_test@theknife.it',  'CLIENTE'),
    ('cliente_test2', '$2a$12$DFuILj7snFGmTWbdwGBJ2.w12U1uReXRn2G6YcYZ6rKygEud9pES.', 'Giulia',  'Verdi',   'cliente_test2@theknife.it', 'CLIENTE'),
    ('gestore_test',  '$2a$12$7HRZ2Hh/RLl10qWm9e5c.umFR2BSW5JXFbp42wZt8hiwY4dwxuhAi', 'Luca',    'Bianchi', 'gestore_test@theknife.it',  'RISTORATORE'),
    ('gestore_test2', '$2a$12$.Rg9hSTEHWr.PIbNM9Gzbe0AcJ7kxo/JhdAV7pEWxIb5cTlt.49MS', 'Anna',    'Neri',    'gestore_test2@theknife.it', 'RISTORATORE')
;

-- La colonna password contiene l'hash BCrypt (costo 12) della
-- password "password", generato con at.favre.lib.crypto.bcrypt,
-- la stessa libreria usata da UtenteService per il login.

-- Facoltativo, per testare la risposta a una recensione: i ristoranti
-- di Data.sql hanno id_gestore NULL. Assegnarne uno a un gestore:
-- UPDATE RistorantiTheKnife SET id_gestore = <id_di_gestore_test>
--   WHERE id_ristorante = <id_del_ristorante>;
