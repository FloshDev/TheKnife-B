# Mappa delle schermate — TheKnife

Mappa completa per ruolo. Per ogni schermata: cosa mostra, azioni disponibili, provenienza, destinazioni.

---

## Indice schermate

| ID | Schermata | Attori |
|----|-----------|--------|
| S01 | Splash / Locality confirm | Ospite |
| S02 | Login | Ospite |
| S03 | Registrazione | Ospite |
| S04 | Home / Ricerca | Ospite, Cliente, Ristoratore |
| S05 | Risultati ricerca | Ospite, Cliente, Ristoratore |
| S06 | Dettaglio ristorante | Ospite, Cliente, Ristoratore |
| S07 | Scrivi / Modifica recensione | Cliente |
| S08 | Lista preferiti | Cliente |
| S09 | Dashboard ristoratore | Ristoratore |
| S10 | Aggiungi ristorante | Ristoratore |
| S11 | Associati a ristorante | Ristoratore |
| S12 | Gestione recensioni ristorante | Ristoratore |

---

## S01 — Splash / Locality confirm

**Attore:** Ospite (tutti vedono questa schermata all'avvio)

**Mostra:**
- Splash logo TheKnife
- Località rilevata da IP (RF01) con possibilità di conferma o modifica manuale

**Azioni:**
- Conferma località → S04 Home
- Modifica località (campo testo) → S04 Home
- Vai al login → S02

**Arriva da:** avvio applicazione

**Va a:** S04, S02

**RF coperti:** RF01

---

## S02 — Login

**Attore:** Ospite (non autenticato)

**Mostra:**
- Campo username
- Campo password
- Link a registrazione
- Pulsante login

**Azioni:**
- Login con credenziali → S04 Home (ruolo corretto: Cliente o Ristoratore)
- Vai a registrazione → S03

**Arriva da:** S01, S04 (click "Accedi"), qualsiasi schermata (azione richiede auth)

**Va a:** S04 (Cliente), S09 (Ristoratore), S03

**RF coperti:** RF06, RF15

---

## S03 — Registrazione

**Attore:** Ospite

**Mostra:**
- Selezione tipo account (Cliente / Ristoratore)
- Campi: nome, cognome, username, email, password, indirizzo
- Pulsante registra

**Azioni:**
- Registrazione → S02 Login (dopo successo, l'utente si autentica)
- Torna al login → S02

**Arriva da:** S02

**Va a:** S02

**RF coperti:** RF05, RF14

---

## S04 — Home / Ricerca

**Attore:** Ospite, Cliente, Ristoratore

**Mostra:**
- Barra ricerca (nome ristorante)
- Filtri: tipologia cucina, fascia prezzo, valutazione minima
- Ricerca per raggio (campo km + posizione)
- Pulsante "Vicino a me" (solo Cliente — usa indirizzo registrato, RF07)
- Navbar: [Login/Profilo] [Preferiti — solo Cliente] [Dashboard — solo Ristoratore]

**Azioni:**
- Cerca per filtri → S05 Risultati (RF02)
- Cerca per geolocalizzazione + raggio → S05 Risultati (RF03)
- "Vicino a me" → S05 Risultati filtrati per indirizzo cliente (RF07)
- Click Preferiti → S08 (solo Cliente)
- Click Dashboard → S09 (solo Ristoratore)
- Click Login → S02 (solo Ospite)
- Logout → S01 (RF21)

**Arriva da:** S01, S02 (post-login), S05 (torna indietro), S06 (torna indietro)

**Va a:** S05, S08, S09, S02

**RF coperti:** RF01, RF02, RF03, RF07, RF10, RF21

---

## S05 — Risultati ricerca

**Attore:** Ospite, Cliente, Ristoratore

**Mostra:**
- Lista card ristoranti (nome, tipologia, fascia prezzo, valutazione media, distanza)
- Mappa JS embedded (WebView) con marker per ogni risultato — **solo visualizzazione**: JS riceve coordinate e disegna marker, niente altro
- Indicatore "Preferito" su card (solo Cliente)

**Azioni:**
- Click card (JavaFX) → S06 Dettaglio ristorante
- Click su area mappa → evento intercettato da JavaFX, non da JS
- Modifica filtri → aggiorna lista in-place
- Torna indietro → S04

**Arriva da:** S04

**Va a:** S06, S04

**RF coperti:** RF02, RF03, RF07

---

## S06 — Dettaglio ristorante

**Attore:** Ospite, Cliente, Ristoratore

**Mostra:**
- Nome, tipologia cucina, indirizzo, fascia prezzo
- Valutazione media con stelle
- Pulsante "Aggiungi ai preferiti" / "Rimuovi dai preferiti" (solo Cliente)
- Lista recensioni con testo, stelle, data, username
- Per ogni recensione: risposta del ristoratore (se presente)
- Pulsante "Scrivi recensione" (solo Cliente)
- Per recensioni proprie (Cliente): pulsanti Modifica / Elimina
- Per ristoratori gestori: pulsante "Rispondi" su ogni recensione senza risposta

**Azioni:**
- Aggiungi preferito → aggiorna icona (RF08)
- Rimuovi preferito → aggiorna icona (RF09)
- Scrivi recensione → S07 (RF11)
- Modifica recensione → S07 pre-compilato (RF12)
- Elimina recensione → dialog conferma → aggiorna lista (RF13)
- Rispondi a recensione → dialog inline testo risposta (RF18)
- Torna indietro → S05

**Arriva da:** S05

**Va a:** S07, S05

**RF coperti:** RF04, RF08, RF09, RF11, RF12, RF13, RF18

---

## S07 — Scrivi / Modifica recensione

**Attore:** Cliente

**Mostra:**
- Nome ristorante (intestazione, non modificabile)
- Stelle (1–5) con RatingControl ControlsFX
- Campo testo recensione
- Pulsante Pubblica / Salva modifiche
- Pulsante Annulla

**Azioni:**
- Pubblica / Salva → torna S06, lista recensioni aggiornata (RF11, RF12)
- Annulla → torna S06

**Arriva da:** S06

**Va a:** S06

**RF coperti:** RF11, RF12

---

## S08 — Lista preferiti

**Attore:** Cliente

**Mostra:**
- Lista card ristoranti preferiti (stessa card di S05)
- Pulsante rimuovi su ogni card

**Azioni:**
- Click card → S06 Dettaglio ristorante
- Rimuovi preferito → aggiorna lista (RF09)
- Torna indietro → S04

**Arriva da:** S04 (navbar)

**Va a:** S06, S04

**RF coperti:** RF09, RF10

---

## S09 — Dashboard ristoratore

**Attore:** Ristoratore

**Mostra:**
- Lista ristoranti gestiti dal ristoratore (RF19): nome, indirizzo, valutazione media
- Pulsante "Aggiungi nuovo ristorante"
- Pulsante "Associati a ristorante esistente"
- Per ogni ristorante: pulsante "Gestisci recensioni"

**Azioni:**
- Aggiungi ristorante → S10 (RF16)
- Associati a ristorante → S11 (RF17)
- Gestisci recensioni → S12 (RF18, RF20)
- Logout → S01 (RF21)
- Torna Home → S04

**Arriva da:** S02 (post-login Ristoratore), S04 (navbar)

**Va a:** S10, S11, S12, S04

**RF coperti:** RF19, RF21

---

## S10 — Aggiungi ristorante

**Attore:** Ristoratore

**Mostra:**
- Campi: nome, tipologia cucina, indirizzo, fascia prezzo, telefono, descrizione
- Geocoding automatico indirizzo → lat/lon (OpenCage, RNF09)
- Pulsante Salva
- Pulsante Annulla

**Azioni:**
- Salva → torna S09, ristorante aggiunto alla lista (RF16)
- Annulla → torna S09

**Arriva da:** S09

**Va a:** S09

**RF coperti:** RF16

---

## S11 — Associati a ristorante esistente

**Attore:** Ristoratore

**Mostra:**
- Campo ricerca ristorante (nome / indirizzo)
- Lista risultati: ristoranti non ancora gestiti da nessuno
- Pulsante "Associati" per ogni risultato

**Azioni:**
- Associati → conferma dialog → torna S09, ristorante appare nella lista (RF17)
- Annulla → torna S09

**Arriva da:** S09

**Va a:** S09

**RF coperti:** RF17

---

## S12 — Gestione recensioni ristorante

**Attore:** Ristoratore

**Mostra:**
- Nome ristorante (intestazione)
- Lista recensioni: testo, stelle, data, username cliente
- Per ogni recensione: risposta esistente (se presente) o pulsante "Rispondi"

**Azioni:**
- Rispondi → dialog inline con campo testo → pubblica risposta (RF18)
- Torna → S09

**Arriva da:** S09

**Va a:** S09

**RF coperti:** RF18, RF20

---

## Mappa di navigazione (sintesi)

```
[S01 Splash]
    ├──→ [S04 Home]
    └──→ [S02 Login] ──→ [S03 Registrazione] ──→ [S02]
                  └──→ [S04 Home / S09 Dashboard]

[S04 Home]
    ├──→ [S05 Risultati] ──→ [S06 Dettaglio]
    │                              ├──→ [S07 Scrivi/Modifica recensione] ──→ [S06]
    │                              └──→ [S06] (preferiti, risposta inline)
    ├──→ [S08 Preferiti] ──→ [S06 Dettaglio]
    └──→ [S09 Dashboard Ristoratore]
              ├──→ [S10 Aggiungi ristorante] ──→ [S09]
              ├──→ [S11 Associati ristorante] ──→ [S09]
              └──→ [S12 Gestione recensioni] ──→ [S09]
```

---

## Copertura RF

| RF | Schermata |
|----|-----------|
| RF01 | S01, S04 |
| RF02 | S04, S05 |
| RF03 | S04, S05 |
| RF04 | S06 |
| RF05 | S03 |
| RF06 | S02 |
| RF07 | S04, S05 |
| RF08 | S06 |
| RF09 | S06, S08 |
| RF10 | S08 |
| RF11 | S06, S07 |
| RF12 | S06, S07 |
| RF13 | S06 |
| RF14 | S03 |
| RF15 | S02 |
| RF16 | S09, S10 |
| RF17 | S09, S11 |
| RF18 | S06, S12 |
| RF19 | S09 |
| RF20 | S12 |
| RF21 | S04, S09 |
