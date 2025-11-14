package domain;

public class PlnaLekce extends Lekce {
    public PlnaLekce(Lekce original) {
        super(original.id, original.title, original.trainer, original.day, original.time, original.capacity, original.price);
        this.loggedIn = original.loggedIn;
        this.registeredCustomers = original.registeredCustomers;
    }

    @Override
    public void registerCustomer(Zakaznik z) {
        System.out.println("Lesson is full.");
    }
}


