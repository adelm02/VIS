package data;

import domain.Lekce;

import java.util.ArrayList;
import java.util.List;

public class LekceTableModule {
    private List<Lekce> lekce = new ArrayList<>();

    public void add(Lekce l) {
        lekce.add(l);
    }

    public Lekce findById(int id) {
        for (Lekce l : lekce) {
            if (l.id == id) return l;
        }
        return null;
    }

    public List<Lekce> getAll() {
        return lekce;
    }
}