package service;
import java.io.FileWriter;
import java.util.List;

import data.ManagerDto;
import data.ManagerGateway;
import data.ManagerTableModule;
import domain.Manager;


public class ManagerService {

    // save all to file
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

    // load all from files
    public List<ManagerDto> loadAllFromFile() throws Exception {
        ManagerGateway gw = new ManagerGateway();
        return gw.findAll();
    }
}