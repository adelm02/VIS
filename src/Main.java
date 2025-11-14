import data.ReservationDto;
import data.ReservationGateway;
import data.DB;
import data.ReservationUnitOfWork;
import domain.Lekce;
import domain.Zakaznik;
import domain.osobniZakaznik;
import interfaces.IReservationCommand;
import service.ConfirmReservationCommand;
import service.ReservationNumberManager;
import service.ReservationService;
import service.StandardPricingStrategy;
import util.AuditLogObserver;

public class Main {
    public static void main(String[] args) throws Exception {
        DB.initSchema();
        DB.clearAll();
        DB.seed();

        Zakaznik z1 = new osobniZakaznik(1, "Anna Nováková", "anna@fit.cz", 500, "Anna", "Nováková", "1995-01-01");
        Lekce l1 = new Lekce(1, "Crossfit","Petr Novák", "Monday", "10:00", 10, 150);

        ReservationGateway gateway = new ReservationGateway();
        ReservationUnitOfWork uow = new ReservationUnitOfWork(gateway);
        ReservationService service = new ReservationService(uow);

        //GoF
        // 1. SINGLETON
        System.out.println("1. SINGLETON - Centrální číslování");
        int id1 = ReservationNumberManager.getInstance().getNextNumber();
        int id2 = ReservationNumberManager.getInstance().getNextNumber();
        System.out.println("První ID: " + id1);
        System.out.println("Druhé ID: " + id2);
        System.out.println("Navazují? " + (id2 == id1 + 1));

        // 2. STRATEGY
        System.out.println("\n2. STRATEGY - Cenová strategie");
        service.setPricingStrategy(new StandardPricingStrategy());
        System.out.println("Strategie nastavena: Standardní");

        // 3. OBSERVER
        System.out.println("\n3. OBSERVER - Notifikace");
        service.addObserver(new AuditLogObserver());
        System.out.println("Pozorovatel zaregistrován");
        System.out.println("\nVytvářím rezervaci...");
        service.createReservation(z1, l1, "2025-11-20");

        // 4. COMMAND
        System.out.println("\n4. COMMAND - Příkazy");
        java.util.List<ReservationDto> all = gateway.findAll();
        if (!all.isEmpty()) {
            ReservationDto res = all.get(all.size() - 1);
            IReservationCommand cmd = new ConfirmReservationCommand(res, service);
            System.out.println("Spouštím příkaz: " + cmd.getCommandName());
            cmd.execute();
        }
    }
}