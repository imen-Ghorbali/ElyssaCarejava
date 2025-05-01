package tn.esprit.interfaces;
import java.util.ArrayList;

public interface IService_consultation<C> {
    void add (C c);
    ArrayList<C> getAll();

    void update(C c);
    boolean delete (C c);
}
