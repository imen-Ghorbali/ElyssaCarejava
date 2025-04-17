package tn.esprit.interfaces;
import java.util.ArrayList;

public interface IService<U> {
    int add (U u );
    ArrayList<U> getAll();

    void update(U u );
    boolean delete (U u);
}
