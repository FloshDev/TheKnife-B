-- ============================================================
-- Utilizzo: questo script permette la creazione dello schema del
-- database, ovvero la struttura delle 6 tabelle.
--
-- @author Scolaro Gabriele, 760123, VA
-- ============================================================

CREATE TABLE Utenti (
    id            SERIAL PRIMARY KEY,
    username      VARCHAR(255) UNIQUE NOT NULL,
    password      VARCHAR(255) NOT NULL,
    nome          VARCHAR(100) NOT NULL,
    cognome       VARCHAR(100) NOT NULL,
    email         VARCHAR(255) UNIQUE,
    data_nascita  DATE,
    domicilio     VARCHAR(255),
    ruolo         VARCHAR(20) CHECK (ruolo IN ('CLIENTE','RISTORATORE'))
);


CREATE TABLE Servizio (
    id   SERIAL PRIMARY KEY,
    nome VARCHAR(100) UNIQUE NOT NULL
);


CREATE TABLE RistorantiTheKnife (
    id_ristorante        SERIAL PRIMARY KEY,
    nome                 VARCHAR(255) NOT NULL,
    nazione              VARCHAR(100),
    citta                VARCHAR(100),
    provincia            VARCHAR(100),
    indirizzo            VARCHAR(255),
    latitudine           DOUBLE PRECISION,
    longitudine          DOUBLE PRECISION,
    fascia_prezzo        SMALLINT CHECK (fascia_prezzo BETWEEN 1 AND 4),
    prenotazione_online  BOOLEAN DEFAULT FALSE,
    consegna_a_domicilio BOOLEAN DEFAULT FALSE,
    tipo_cucina          VARCHAR(100),
    telefono             VARCHAR(50),
    website              VARCHAR(255),
    premi                TEXT,
    id_gestore           INT REFERENCES Utenti(id) ON DELETE SET NULL
);


CREATE TABLE RistoranteServizio (
    id_ristorante INT REFERENCES RistorantiTheKnife(id_ristorante) ON DELETE CASCADE,
    id_servizio   INT REFERENCES Servizio(id) ON DELETE CASCADE,
    PRIMARY KEY (id_ristorante, id_servizio)
);


CREATE TABLE Recensioni (
    id_recensione      SERIAL PRIMARY KEY,
    id_ristorante      INT REFERENCES RistorantiTheKnife(id_ristorante) ON DELETE CASCADE,
    id_cliente         INT REFERENCES Utenti(id) ON DELETE CASCADE,
    titolo             VARCHAR(255),
    testo              TEXT,
    stelle             SMALLINT CHECK (stelle BETWEEN 1 AND 5),
    data_pubblicazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    risposta           TEXT,
    data_risposta      TIMESTAMP,
    CONSTRAINT unique_cliente_ristorante UNIQUE (id_cliente, id_ristorante)
);


CREATE TABLE Preferiti (
    id_cliente    INT REFERENCES Utenti(id) ON DELETE CASCADE,
    id_ristorante INT REFERENCES RistorantiTheKnife(id_ristorante) ON DELETE CASCADE,
    PRIMARY KEY (id_cliente, id_ristorante)
);

