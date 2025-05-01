package tn.esprit.interfaces;

import java.util.ArrayList;

public interface IService<E> {
    int add(E e);
    void update(E e);
    boolean delete(E e);
    ArrayList<E> getAll();
}