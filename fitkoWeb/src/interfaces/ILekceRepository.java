package interfaces;

import data.LekceDto;

import java.sql.SQLException;
import java.util.List;

public interface ILekceRepository {
    void insert(LekceDto l) throws SQLException;
    List<LekceDto> findAll() throws SQLException;
    void update(LekceDto updated) throws SQLException;
    void deleteById(int id) throws SQLException;
}