package service;
import java.io.FileWriter;
import java.util.List;

import data.TrenerDto;
import data.TrenerGateway;
import data.TrenerTableModule;
import domain.Reservation;
import domain.Trener;
import domain.Zakaznik;
import interfaces.IReservationRepository;
import data.ReservationGateway;
import util.AuditLogObserver;

public class TrenerService {


    public void saveAllToFile(TrenerTableModule table) throws Exception {
        TrenerGateway gw = new TrenerGateway();

        // clear file before saving
        new FileWriter("treneri.csv", false).close();

        for (Trener t : table.getAll()) {
            TrenerDto dto = new TrenerDto();
            dto.id = t.id;
            dto.name = t.name;
            dto.email = t.email;
            dto.password = t.password;
            dto.speciality = t.speciality;
            dto.clas = t.clas;
            gw.insert(dto);
        }
    }

    // LOAD ALL FROM FILE
    public List<TrenerDto> loadAllFromFile() throws Exception {
        TrenerGateway gw = new TrenerGateway();
        return gw.findAll();
    }
}