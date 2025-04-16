package tn.esprit.services;

import java.util.List;

public interface IMedicamentService <T> {
    void ajouter(T t);
    void modifier(T t);
    void supprimer(long id);
    List<T> rechercher();
    T rechercherParId(long id);
    List<T> getAll();
}