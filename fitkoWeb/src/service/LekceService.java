package service;
import java.io.FileWriter;
import java.util.List;

import data.LekceDto;
import data.LekceGateway;
import data.LekceTableModule;
import domain.Lekce;


public class LekceService {
    // Save all lessons to CSV
    public void saveAllToFile(LekceTableModule table) throws Exception {
        LekceGateway gateway = new LekceGateway();

        // clear file first
        new FileWriter("lekce.csv", false).close();

        for (Lekce l : table.getAll()) {
            LekceDto dto = new LekceDto();
            dto.id = l.id;
            dto.title = l.title;
            dto.trainer = l.trainer;
            dto.day = l.day;
            dto.time = l.time;
            dto.capacity = l.capacity;
            dto.loggedIn = l.loggedIn;
            dto.price = l.price.getAmountCzk();

            gateway.insert(dto);
        }
    }

    // Load all lessons from CSV
    public List<LekceDto> loadAllFromFile() throws Exception {
        LekceGateway gateway = new LekceGateway();
        return gateway.findAll();
    }
}