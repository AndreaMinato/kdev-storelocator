package it.kdevgroup.storelocator.database;

/**
 * Created by damiano on 20/04/16.
 * Interfaccia per la gestione di una query asincrona eseguita sul database.
 */
public interface IAsyncMapReduceQueryHandler {
    void handle(Object value, Throwable error);
}
