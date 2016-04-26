package it.kdevgroup.storelocator.database;

import java.util.Map;

/**
 * Created by damiano on 20/04/16.
 * Interfaccia per la gestione di una query asincrona eseguita sul database.
 */
public interface IAsyncMapQueryHandler {
    void handle(Map<String, Object> value, Throwable error);
    void onFinish();
}
