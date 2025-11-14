package data;

import domain.Zakaznik;

import java.util.ArrayList;
import java.util.List;

public class ZakaznikTableModule {
    private List<Zakaznik> zakaznici = new ArrayList<>();

    public void add(Zakaznik z) {
        zakaznici.add(z);
    }

    public Zakaznik findById(int id) {
        for (Zakaznik z : zakaznici) {
            if (z.id == id) return z;
        }
        return null;
    }

    public void printAll() {
        for (Zakaznik z : zakaznici) {
            System.out.println(z.id + " - " + z.name + " (" + z.credit + ")");
        }
    }

    public List<Zakaznik> getAll() {
        return zakaznici;
    }
}