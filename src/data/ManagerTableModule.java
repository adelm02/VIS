package data;

import domain.Manager;

import java.util.ArrayList;
import java.util.List;

public class ManagerTableModule {
    private List<Manager> managers = new ArrayList<>();

    public void add(Manager m) {
        managers.add(m);
    }

    public Manager findById(int id) {
        for (Manager m : managers) {
            if (m.id == id) return m;
        }
        return null;
    }

    public List<Manager> getAll() {
        return managers;
    }

    public void printAll() {
        for (Manager m : managers) {
            m.infoPrint();
        }
    }
}