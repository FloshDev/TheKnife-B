-- ============================================================
-- Utilizzo: questo script va eseguito una sola volta, da un utente
-- amministrativo (es. postgres), PRIMA di Schema.sql e Data.sql.
--
-- @author Scolaro Gabriele, 760123, VA
-- ============================================================

CREATE ROLE tk_app
    LOGIN
    PASSWORD 'TheKnife-B';

CREATE DATABASE "dbTK";

-- Il ruolo riceve i privilegi SOLO sul database dbTK. Concessi in
-- fase di creazione del db; qui si prepara il contesto applicativo.
GRANT CONNECT ON DATABASE "dbTK" TO tk_app;