package interfaces;

import data.TrenerDto;

import java.sql.SQLException;
import java.util.List;

public interface ITrenerRepository {
    void insert(TrenerDto t) throws SQLException;
    List<TrenerDto> findAll() throws SQLException;
    TrenerDto findById(int id) throws SQLException;
    void update(TrenerDto t) throws SQLException;
    void deleteById(int id) throws SQLException;
}