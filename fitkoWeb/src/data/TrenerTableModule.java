package data;

import domain.Trener;

import java.util.ArrayList;
import java.util.List;

public class TrenerTableModule {
    private List<Trener> treneri = new ArrayList<>();

    public void add(Trener t) {
        treneri.add(t);
    }

    public Trener findById(int id) {
        for (Trener t : treneri) {
            if (t.id == id) return t;
        }
        return null;
    }

    public List<Trener> getAll() {
        return treneri;
    }

    public void printAll() {
        for (Trener t : treneri) {
            System.out.println(t.id + " - " + t.name + " (" + t.speciality + ")");
        }
    }
}