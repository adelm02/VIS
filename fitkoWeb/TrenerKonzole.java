import data.*;
import domain.*;
import service.*;

import java.util.List;
import java.util.Scanner;

/**
 * Konzolová aplikace pro trenéra - U8 Správa rozvrhu
 */
public class TrenerKonzole {
    private final LekceService lekceService;
    private final LekceTableModule lekceTableModule;
    private final LekceGateway lekceGateway;
    private final Scanner scanner;
    private final String trenerJmeno;

    public TrenerKonzole(String trenerJmeno) {
        this.trenerJmeno = trenerJmeno;
        this.lekceService = new LekceService();
        this.lekceTableModule = new LekceTableModule();
        this.lekceGateway = new LekceGateway();
        this.scanner = new Scanner(System.in);
    }

    public void start() throws Exception {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║     FITKO - Správa rozvrhu (Trenér)         ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Přihlášen jako: " + trenerJmeno);
        System.out.println();

        loadLessons();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    zobrazRozvrh();
                    break;
                case "2":
                    upravLekci();
                    break;
                case "3":
                    pridatLekci();
                    break;
                case "4":
                    smazatLekci();
                    break;
                case "0":
                    running = false;
                    System.out.println("\nDěkujeme za použití systému FITKO!");
                    break;
                default:
                    System.out.println("\n❌ Neplatná volba!");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📋 MENU");
        System.out.println("=".repeat(50));
        System.out.println("1. Zobrazit můj rozvrh");
        System.out.println("2. Upravit lekci");
        System.out.println("3. Přidat novou lekci");
        System.out.println("4. Smazat lekci");
        System.out.println("0. Odhlásit se");
        System.out.println("=".repeat(50));
        System.out.print("Vaše volba: ");
    }

    private void loadLessons() throws Exception {
        System.out.println("📥 Načítám lekce z databáze...");
        List<LekceDto> allLessons = lekceService.loadAllFromFile();

        lekceTableModule.getAll().clear();
        for (LekceDto dto : allLessons) {
            if (dto.trainer.equals(trenerJmeno)) {
                Lekce lekce = new Lekce(dto.id, dto.title, dto.trainer, dto.day, dto.time, dto.capacity, dto.price);
                lekce.loggedIn = dto.loggedIn;
                lekceTableModule.add(lekce);
            }
        }
        System.out.println("✅ Načteno " + lekceTableModule.getAll().size() + " lekcí");
    }

    private void zobrazRozvrh() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                              MŮJ ROZVRH LEKCÍ                                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════════╝");

        List<Lekce> lekce = lekceTableModule.getAll();

        if (lekce.isEmpty()) {
            System.out.println("\n❌ Nemáte žádné naplánované lekce.");
            return;
        }

        System.out.println();
        System.out.printf("%-5s %-20s %-12s %-8s %-12s %-8s %-10s%n",
                "ID", "Název", "Den", "Čas", "Obsazenost", "Cena", "Status");
        System.out.println("-".repeat(85));

        for (Lekce l : lekce) {
            String obsazenost = l.loggedIn + "/" + l.capacity;
            String status = l.loggedIn >= l.capacity ? "PLNÁ ⚠️" : "Volno ✅";
            String priceCzk = l.price.getAmountCzk() + " Kč";

            System.out.printf("%-5d %-20s %-12s %-8s %-12s %-8s %-10s%n",
                    l.id, l.title, l.day, l.time, obsazenost, priceCzk, status);
        }
        System.out.println();
    }

    private void upravLekci() throws Exception {
        zobrazRozvrh();

        System.out.print("\n🔧 Zadejte ID lekce k úpravě: ");
        String idStr = scanner.nextLine().trim();

        try {
            int id = Integer.parseInt(idStr);
            Lekce lekce = lekceTableModule.findById(id);

            if (lekce == null) {
                System.out.println("\n❌ Lekce s ID " + id + " nebyla nalezena!");
                return;
            }

            System.out.println("\n📝 Úprava lekce: " + lekce.title);
            System.out.println("(Pro ponechání současné hodnoty stiskněte Enter)");
            System.out.println();

            System.out.print("Název [" + lekce.title + "]: ");
            String newTitle = scanner.nextLine().trim();
            if (!newTitle.isEmpty()) lekce.title = newTitle;

            System.out.print("Den [" + lekce.day + "]: ");
            String newDay = scanner.nextLine().trim();
            if (!newDay.isEmpty()) lekce.day = newDay;

            System.out.print("Čas [" + lekce.time + "]: ");
            String newTime = scanner.nextLine().trim();
            if (!newTime.isEmpty()) lekce.time = newTime;

            System.out.print("Kapacita [" + lekce.capacity + "]: ");
            String newCapacity = scanner.nextLine().trim();
            if (!newCapacity.isEmpty()) {
                try {
                    int cap = Integer.parseInt(newCapacity);
                    if (cap < lekce.loggedIn) {
                        System.out.println("⚠️  Varování: Nová kapacita je menší než počet přihlášených (" + lekce.loggedIn + ")");
                    }
                    lekce.capacity = cap;
                } catch (NumberFormatException e) {
                    System.out.println("❌ Neplatná kapacita!");
                }
            }

            System.out.print("Cena [" + lekce.price.getAmountCzk() + " Kč]: ");
            String newPrice = scanner.nextLine().trim();
            if (!newPrice.isEmpty()) {
                try {
                    int price = Integer.parseInt(newPrice);
                    lekce.price = new Price(price);
                } catch (NumberFormatException e) {
                    System.out.println("❌ Neplatná cena!");
                }
            }

            lekce.markUpdated();
            lekceService.saveAllToFile(lekceTableModule);

            System.out.println("\n✅ Lekce byla úspěšně aktualizována!");
            System.out.println("📞 Volá se: LekceGateway.update(lekceDto)");

        } catch (NumberFormatException e) {
            System.out.println("\n❌ Neplatné ID!");
        }
    }

    private void pridatLekci() throws Exception {
        System.out.println("\n➕ Přidání nové lekce");
        System.out.println();

        System.out.print("Název lekce: ");
        String title = scanner.nextLine().trim();

        System.out.print("Den (např. Monday): ");
        String day = scanner.nextLine().trim();

        System.out.print("Čas (např. 10:00): ");
        String time = scanner.nextLine().trim();

        System.out.print("Kapacita: ");
        int capacity = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Cena (Kč): ");
        int price = Integer.parseInt(scanner.nextLine().trim());

        int newId = lekceTableModule.getAll().stream()
                .mapToInt(l -> l.id)
                .max()
                .orElse(0) + 1;

        Lekce novaLekce = new Lekce(newId, title, trenerJmeno, day, time, capacity, price);
        lekceTableModule.add(novaLekce);

        lekceService.saveAllToFile(lekceTableModule);

        System.out.println("\n✅ Nová lekce byla úspěšně přidána!");
        System.out.println("📞 Volá se: LekceGateway.insert(lekceDto)");
    }

    private void smazatLekci() throws Exception {
        zobrazRozvrh();

        System.out.print("\n🗑️  Zadejte ID lekce ke smazání: ");
        String idStr = scanner.nextLine().trim();

        try {
            int id = Integer.parseInt(idStr);
            Lekce lekce = lekceTableModule.findById(id);

            if (lekce == null) {
                System.out.println("\n❌ Lekce s ID " + id + " nebyla nalezena!");
                return;
            }

            if (lekce.loggedIn > 0) {
                System.out.println("\n⚠️  VAROVÁNÍ: Lekce má " + lekce.loggedIn + " přihlášených zákazníků!");
                System.out.print("Opravdu chcete smazat? (ano/ne): ");
                String confirm = scanner.nextLine().trim().toLowerCase();
                if (!confirm.equals("ano")) {
                    System.out.println("❌ Operace zrušena.");
                    return;
                }
            }

            lekceTableModule.getAll().remove(lekce);
            lekceGateway.deleteById(id);

            System.out.println("\n✅ Lekce byla úspěšně smazána!");
            System.out.println("📞 Volá se: LekceGateway.deleteById(id)");

        } catch (NumberFormatException e) {
            System.out.println("\n❌ Neplatné ID!");
        }
    }

    public static void main(String[] args) {
        try {
            DB.initSchema();

            TrenerKonzole app = new TrenerKonzole("Petr Novák");
            app.start();

        } catch (Exception e) {
            System.err.println("❌ Chyba aplikace: " + e.getMessage());
            e.printStackTrace();
        }
    }
}