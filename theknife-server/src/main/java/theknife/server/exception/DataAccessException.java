package theknife.server.exception;

  /**
   * Eccezione tecnica del layer dati: segnala che l'accesso al database è
   * fallito (connessione non disponibile, credenziali rifiutate, query non
   * eseguibile). Riavvolge la SQLException catturata dentro il DAO, così che
   * java.sql non risalga oltre il confine del package dao. Il chiamante la
   * traduce in ResponseStatus.ERRORE_SERVER.
   * <p>
   * Da non confondere con {@link ApplicationException}, che segnala una
   * violazione delle regole di dominio: qui la colpa è del sistema, lì
   * dell'operazione richiesta.
   *
   * @author Ciani Flavio Angelo, 761581, VA
   */
  public class DataAccessException extends Exception {

      private static final long serialVersionUID = 1L;

      /**
       * Crea l'eccezione con il messaggio descrittivo del fallimento.
       *
       * @param messaggio la descrizione dell'errore di accesso ai dati
       */
      public DataAccessException(String messaggio) {
          super(messaggio);
      }
  }
