package service;
import java.io.FileWriter;
import java.util.List;

import data.RecepcniDto;
import data.RecepcniGateway;
import data.RecepcniTableModule;
import domain.Recepcni;
import domain.Reservation;
import domain.Zakaznik;
import interfaces.IReservationRepository;
import data.ReservationGateway;
import util.AuditLogObserver;

public class RecepcniService {

    // SAVE ALL TO FILE
    public void saveAllToFile(RecepcniTableModule table) throws Exception {
        RecepcniGateway gw = new RecepcniGateway();

        // clear file before saving
        new FileWriter("recepcni.csv", false).close();

        for (Recepcni r : table.getAll()) {
            RecepcniDto dto = new RecepcniDto();
            dto.id = r.id;
            dto.name = r.name;
            dto.login = r.login;
            dto.password = r.password;
            gw.insert(dto);
        }
    }

    // LOAD ALL FROM FILE
    public List<RecepcniDto> loadAllFromFile() throws Exception {
        RecepcniGateway gw = new RecepcniGateway();
        return gw.findAll();
    }
}