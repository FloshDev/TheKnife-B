# Contratto del protocollo

- Ogni CommandType rappresenta una singola operazione atomica eseguibile dal server.
- I DTO del modulo common sono classi immutabili dal punto di vista del significato: contengono esclusivamente dati trasferiti sulla rete e non implementano alcuna logica applicativa.

// GUEST 
OTTIENI_LOCALITA_INIZIALE, ACCEDI, REGISTRATI, CERCA_RISTORANTI, OTTIENI_DETTAGLI_RISTORANTE,
LEGGI_RECENSIONI,
// CLIENTE 
AGGIUNGI_PREFERITO, RIMUOVI_PREFERITO, VEDI_PREFERITI, AGGIUNGI_RECENSIONE, MODIFICA_RECENSIONE, 
ELIMINA_RECENSIONE, CERCA_VICINO, ESCI, 
// RISTORATORE 
AGGIUNGI_RISTORANTE, VEDI_RISTORANTI_GESTITI, LEGGI_RECENSIONI_RISTORANTI_GESTITI, 
OTTIENI_STATISTICHE_RISTORANTE, RISPONDI_RECENSIONE, ASSOCIA_RISTORANTE

| CommandType                         | DTO Request            | DTO/ogg. Response         |Token |Autenticaz.
| ----------------------------------- | ---------------------- | ------------------------- | ---- |
| ACCEDI                              | LoginDTO               | LoginResultDTO            |no    |non serve
| CERCA_RISTORANTI                    | CercaRistorantiDTO     | `List<RistoranteDTO>`     |no    |non serve
| REGISTRATI                          | RegistrazioneDTO       | R. senza payload          |no    |non serve
| AGGIUNGI_RECENSIONE                 | AggiungiRecensioneDTO  | R. senza payload          |sì    |serve
| OTTIENI_LOCALITA_INIZIALE           | PosizioneDTO (geoloc.) | `List<RistoranteDTO>`     |no    |non serve
| OTTIENI_DETTAGLI_RISTORANTE         | IdRistoranteDTO        | RistoranteDTO             |no    |non serve
| LEGGI_RECENSIONI                    | IdRistoranteDTO        | `List<RecensioneDTO>`     |no    |non serve
| AGGIUNGI_PREFERITO                  | IdRistoranteDTO        | R. senza payload          |sì    |serve
| RIMUOVI_PREFERITO                   | IdRistoranteDTO        | R. senza payload          |sì    |serve
| VEDI_PREFERITI                      | null                   | `List<RistoranteDTO>`     |sì    |serve
| MODIFICA_RECENSIONE                 | ModificaRecensioneDTO  | R. senza payload          |sì    |serve
| ELIMINA_RECENSIONE                  | IdRecensioneDTO        | R. senza payload          |sì    |serve
| CERCA_VICINO                        | CercaVicinoDTO         | `List<RistoranteDTO>`     |sì    |non serve
| ESCI (DISCONNETTITI)                | null                   | R. senza payload          |sì    |serve
| AGGIUNGI_RISTORANTE                 | AggiungiRistoranteDTO  | R. senza payload          |sì    |serve
| VEDI_RISTORANTI_GESTITI             | null                   | `List<RistoranteDTO>`     |sì    |serve
| LEGGI_RECENSIONI_RISTORANTI_GESTITI | null                   | `List<RecensioneDTO>`     |sì    |serve
| OTTIENI_STATISTICHE_RISTORANTE      | IdRistoranteDTO        | StatisticheRistoranteDTO  |sì    |serve
| RISPONDI_RECENSIONE                 | RispondiRecensioneDTO  | R. senza payload          |sì    |serve
| ASSOCIA_RISTORANTE                  | IdRistoranteDTO        | R. senza payload          |sì    |serve


| ruolo       | operazione                          | richiede dati  | restituisce dati        |
| ----------- | ----------------------------------- | -------------- | ----------------------- |
| guest       | OTTIENI_LOCALITA_INIZIALE           | sì-geoloc.     | lista (consigliati)     |
| guest       | ACCEDI                              | sì             | sì                      |
| guest       | REGISTRATI                          | sì             | conferma                |
| guest       | CERCA_RISTORANTI                    | sì             | lista                   |
| guest       | OTTIENI_DETTAGLI_RISTORANTE         | sì             | RistoranteDTO           |
| guest       | LEGGI_RECENSIONI                    | sì             | lista                   |
| cliente     | AGGIUNGI_PREFERITO                  | sì             | conferma                |
| cliente     | RIMUOVI_PREFERITO                   | sì             | conferma                |
| cliente     | VEDI_PREFERITI                      | no             | lista                   |
| cliente     | AGGIUNGI_RECENSIONE                 | sì             | conferma                |
| cliente     | MODIFICA_RECENSIONE                 | sì             | conferma                |
| cliente     | ELIMINA_RECENSIONE                  | sì             | conferma                |
| cliente     | CERCA_VICINO                        | sì-geoloc.     | lista                   |
| cliente     | ESCI (DISCONNETTITI)                | no             | conferma                |
| ristoratore | AGGIUNGI_RISTORANTE                 | sì             | conferma                |
| ristoratore | VEDI_RISTORANTI_GESTITI             | no             | lista                   |
| ristoratore | LEGGI_RECENSIONI_RISTORANTI_GESTITI | no             | lista                   |
| ristoratore | OTTIENI_STATISTICHE_RISTORANTE      | sì             | sì                      |
| ristoratore | RISPONDI_RECENSIONE                 | sì             | conferma                |
| ristoratore | ASSOCIA_RISTORANTE                  | sì             | conferma                |

## Request

```java
CommandType comando;
Object payload;
//Costruttore
//Getter
//Setter
//toString
```

## ResponseStatus
SUCCESSO, ERRORE_VALIDAZIONE, NON_AUTORIZZATO, NON_TROVATO, ERRORE_SERVER, ERRORE

## Response

```java
ResponseStatus status;
Object payload;
String messaggio;
//Costruttore
//Getter
//Setter
//toString
```