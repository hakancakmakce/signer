/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.util;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author hakan
 */
public abstract class Observed {
    private final List<Observer> observers = new ArrayList<>();
    
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    protected void notifyObservers(boolean isUserAuthenticated) {
        for (Observer observer : observers) {
            observer.update(isUserAuthenticated);
        }
    }
}
