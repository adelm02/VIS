package interfaces;

import data.ZakaznikDto;

import java.sql.SQLException;
import java.util.List;

public interface IZakaznikRepository {
    void insert(ZakaznikDto z) throws SQLException;
    List<ZakaznikDto> findAll() throws SQLException;
    ZakaznikDto findById(int id) throws SQLException;
    void update(ZakaznikDto updated) throws SQLException;
    void deleteById(int id) throws SQLException;
}