package de.uniko.fb1.soma7;

import java.util.Observable;

/**
 * Created by asdf on 2/10/17.
 */

public class ObservableObject extends Observable {
    private static ObservableObject instance = new ObservableObject();


    /* FIXME MEMORY LEAK -- Leaks MainActivity instance! */


    public static ObservableObject getInstance() {
        return instance;
    }

    private ObservableObject() {
    }

    public void updateValue(Object data) {
        synchronized (this) {
            setChanged();
            notifyObservers(data);
        }
    }
}
