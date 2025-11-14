package interfaces;

import data.ManagerDto;

import java.sql.SQLException;
import java.util.List;

public interface IManagerRepository {
    void insert(ManagerDto m) throws SQLException;
    List<ManagerDto> findAll() throws SQLException;
    void update(ManagerDto m) throws SQLException;
    void deleteById(int id) throws SQLException;
}