# Contratto del protocollo

Ogni CommandType rappresenta una singola operazione atomica eseguibile dal server.


| CommandType                         | DTO Request            | DTO/ogg. Response             |Token
| ----------------------------------- | ---------------------- | ----------------------------- |
| ACCEDI                              | LoginDTO               | LoginResultDTO                |no
| CERCA_RISTORANTI                    | CercaRistoranteDTO     | `List<RistoranteDTO>`         |no
| REGISTRATI                          | RegistratiDTO          | R. senza payload/DTO conferma |no
| AGGIUNGI_RECENSIONE                 | AggiungiRecensioneDTO  | R. senza payload/DTO conferma |sì
| OTTIENI_LOCALITA_INIZIALE           | LocalizzazioneDTO      | PosizioneDTO                  |no
| OTTIENI_DETTAGLI_RISTORANTE         | SelezionaRistoranteDTO | RistoranteDTO                 |no
| LEGGI_RECENSIONI                    | LeggiRecensioniDTO     | RecensioniRistoranteDTO       |no
| AGGIUNGI_PREFERITO                  | AggiungiPreferitoDTO   | R. senza payload              |sì
| RIMUOVI_PREFERITO                   | RimuoviPreferitoDTO    | R. senza payload              |sì
| VEDI_PREFERITI                      | null                   | `List<RistoranteDTO>`         |sì
| MODIFICA_RECENSIONE                 | ModificaRecensioneDTO  | R. senza payload/DTO conferma |sì
| ELIMINA_RECENSIONE                  | EliminaRecensioneDTO   | R. senza payload              |sì
| CERCA_VICINO                        | CercaVicinoDTO         | `List<RistoranteDTO>`         |sì
| ESCI (DISCONNETTITI)                | null                   | R. senza payload              |sì
| AGGIUNGI_RISTORANTE                 | AggiungiRistoranteDTO  | R: senza payload/DTO conferma |sì
| VEDI_RISTORANTI_GESTITI             | null                   | `List<RistoranteDTO>`         |sì
| LEGGI_RECENSIONI_RISTORANTI_GESTITI | null                   | `List<RecensioneDTO>`         |sì
| OTTIENI_RIEPILOGO_RISTORANTE        | DettagliRistoranteDTO  | StatisticheRistoranteDTO      |sì
| RISPONDI_RECENSIONE                 | RispondiRecensioneDTO  | R. senza payload              |sì
| ASSOCIA_RISTORANTE                  | AssociaRistoranteDTO   | R. senza payload/DTO conferma |sì


| ruolo       | operazione                          | richiede dati  | restituisce dati        |
| ----------- | ----------------------------------- | -------------- | ----------------------- |
| guest       | OTTIENI_LOCALITA_INIZIALE           | sì-geoloc.     | sì-lista consigliati    |
| guest       | ACCEDI                              | sì             | sì                      |
| guest       | REGISTRATI                          | sì             | conferma                |
| guest       | CERCA_RISTORANTI                    | sì             | lista                   |
| guest       | OTTIENI_DETTAGLI_RISTORANTE         | sì-facoltativo | RistoranteDTO           |
| guest       | LEGGI_RECENSIONI                    | sì             | RecensioniRistoranteDTO |
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
| ristoratore | OTTIENI_RIEPILOGO_RISTORANTE        | sì             | sì                      |
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
SUCCESSO, ERRORE_VALIDAZIONE, NON_AUTORIZZATO, NON_TROVATO, ERRORE_SERVER

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