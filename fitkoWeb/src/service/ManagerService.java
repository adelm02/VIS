package service;
import java.io.FileWriter;
import java.util.List;

import data.ManagerDto;
import data.ManagerGateway;
import data.ManagerTableModule;
import domain.Manager;


public class ManagerService {

    // SAVE ALL TO FILE
    public void saveAllToFile(ManagerTableModule table) throws Exception {
        ManagerGateway gw = new ManagerGateway();

        // clear file before saving
        new FileWriter("manageri.csv", false).close();

        for (Manager m : table.getAll()) {
            ManagerDto dto = new ManagerDto();
            dto.id = m.id;
            dto.name = m.name;
            dto.login = m.login;
            dto.password = m.password;
            dto.position = m.position;
            gw.insert(dto);
        }
    }

    // LOAD ALL FROM FILE
    public List<ManagerDto> loadAllFromFile() throws Exception {
        ManagerGateway gw = new ManagerGateway();
        return gw.findAll();
    }
}