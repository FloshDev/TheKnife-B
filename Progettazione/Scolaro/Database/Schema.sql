CREATE TABLE Utenti (
    username        VARCHAR(50) PRIMARY KEY,
    password        VARCHAR(255) NOT NULL,
    nome            VARCHAR(100) NOT NULL,
    cognome         VARCHAR(100) NOT NULL,
    email           VARCHAR(150) UNIQUE NOT NULL,
    data_nascita    DATE,
    domicilio       VARCHAR(255),
    ruolo           VARCHAR(20) CHECK (ruolo IN ('CLIENTE', 'GESTORE'))
);

CREATE TABLE RistorantiTheKnife (
    id_ristorante       SERIAL PRIMARY KEY,
    nome                VARCHAR(255) NOT NULL,
    nazione             VARCHAR(100),
    citta               VARCHAR(100),
    indirizzo           VARCHAR(255),
    latitudine          DOUBLE PRECISION,
    longitudine         DOUBLE PRECISION,
    fascia_prezzo       VARCHAR(50),
    delivery            BOOLEAN DEFAULT FALSE,
    prenotazione_online BOOLEAN DEFAULT FALSE,
    tipo_cucina         VARCHAR(100),
    telefono            VARCHAR(20),
    website             VARCHAR(255),
    premi               TEXT,
    servizi             TEXT,
    descrizione         TEXT,
    username_gestore    VARCHAR(50) REFERENCES Utenti(username) ON DELETE SET NULL
);

CREATE TABLE Recensioni (
    id_recensione   SERIAL PRIMARY KEY,
    id_ristorante   INT REFERENCES RistorantiTheKnife(id_ristorante) ON DELETE CASCADE,
    username_cliente VARCHAR(50) REFERENCES Utenti(username) ON DELETE CASCADE,
    stelle          INT CHECK (stelle BETWEEN 1 AND 5),
    commento        TEXT,
    data_recensione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    risposta        TEXT,
    data_risposta   TIMESTAMP,
    CONSTRAINT unique_cliente_ristorante UNIQUE (username_cliente, id_ristorante)
);

CREATE TABLE Preferiti (
    username_cliente VARCHAR(50) REFERENCES Utenti(username) ON DELETE CASCADE,
    id_ristorante    INT REFERENCES RistorantiTheKnife(id_ristorante) ON DELETE CASCADE,
    PRIMARY KEY (username_cliente, id_ristorante)
);