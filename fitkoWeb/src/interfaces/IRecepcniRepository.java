package interfaces;// IRecepcniRepository.java
import java.sql.SQLException;
import java.util.List;

import data.RecepcniDto;


public interface IRecepcniRepository {
    void insert(RecepcniDto r) throws SQLException;
    List<RecepcniDto> findAll() throws SQLException;
    RecepcniDto findById(int id) throws SQLException;
    void update(RecepcniDto r) throws SQLException;
    void deleteById(int id) throws SQLException;
}