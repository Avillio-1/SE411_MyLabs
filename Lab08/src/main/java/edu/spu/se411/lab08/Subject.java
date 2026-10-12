package edu.spu.se411.lab08;

public interface Subject {
    /** Registers a new observer. */
    void register(Observer o);

    /** Unregisters an observer. */
    void unregister(Observer o);

    /** Notifies all registered observers of data change. */
    void notifyObservers();
}
