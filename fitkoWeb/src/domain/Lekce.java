package domain;

import java.util.ArrayList;
import java.util.List;

public class Lekce extends BaseEntity {
    public int id;
    public String title;
    public String trainer;
    public String day;
    public String time;
    public int capacity;
    public int loggedIn;
    public Price price;

    public List<Zakaznik> registeredCustomers = new ArrayList<>();

    public Lekce(int id, String title, String trainer, String day, String time, int capacity, int price) {
        this(id, title, trainer, day, time, capacity, new Price(price));
    }

    public Lekce(int id, String title, String trainer, String day, String time, int capacity, Price price) {
        super();
        this.id = id;
        this.title = title;
        this.trainer = trainer;
        this.day = day;
        this.time = time;
        this.capacity = capacity;
        this.loggedIn = 0;
        this.price = price;
    }

    public void registerCustomer(Zakaznik z) {
        if (loggedIn < capacity) {
            registeredCustomers.add(z);
            loggedIn++;
            System.out.println(z.name + " registered to " + title);
            markUpdated();
        } else {
            System.out.println("Lesson is full.");
        }
    }

    public void logInCust(Zakaznik zakaznik) {
        registerCustomer(zakaznik);
    }
}