package data;

import domain.Recepcni;

import java.util.ArrayList;
import java.util.List;

public class RecepcniTableModule {
    private List<Recepcni> recepcni = new ArrayList<>();

    public void add(Recepcni r) {
        recepcni.add(r);
    }

    public Recepcni findById(int id) {
        for (Recepcni r : recepcni) {
            if (r.id == id) return r;
        }
        return null;
    }

    public List<Recepcni> getAll() {
        return recepcni;
    }

    public void printAll() {
        for (Recepcni r : recepcni) {
            r.infoPrint();
        }
    }
}