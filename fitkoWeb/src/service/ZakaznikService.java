package service;
import java.io.FileWriter;
import java.util.List;

import data.ZakaznikDto;
import data.ZakaznikGateway;
import data.ZakaznikTableModule;
import domain.Zakaznik;

public class ZakaznikService {
    private ZakaznikGateway gateway = new ZakaznikGateway();

    // vytvoření zákazníka
    public void CreateCust(int id, String name, String email, String password, int credit) throws Exception {
        ZakaznikDto z = new ZakaznikDto();
        z.id = id;
        z.name = name;
        z.email = email;
        z.setPassword(password);
        z.credit = credit;

        if (credit < 0) {
            System.out.println("Kredit nemůže být záporný!");
            return;
        }

        gateway.insert(z);
        System.out.println("Zákazník uložen: " + name);
    }

    // načti všechny zákazníky
    public void vypisVsechny() throws Exception {
        for (ZakaznikDto z : gateway.findAll()) {
            System.out.println(z.id + " - " + z.name + " (" + z.credit + ")");
        }
    }

    public List<ZakaznikDto> loadAllFromFile() throws Exception {
        ZakaznikGateway gateway = new ZakaznikGateway();
        return gateway.findAll();
    }



    public void SaveAllInFile(ZakaznikTableModule table) throws Exception {
        ZakaznikGateway gw = new ZakaznikGateway();

        // Clear the CSV file before saving new data
        new FileWriter("zakaznici.csv", false).close();

        for (Zakaznik z : table.getAll()) {
            ZakaznikDto dto = new ZakaznikDto();
            dto.id = z.id;
            dto.name = z.name;
            dto.email = z.email;
            dto.setPassword("unknown");
            dto.credit = z.credit;
            gw.insert(dto);
        }
    }
}