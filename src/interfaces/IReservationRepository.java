package interfaces;
import data.ReservationDto;
import domain.Reservation;

import java.sql.SQLException;
import java.util.List;

public interface IReservationRepository {
    void insert(ReservationDto r) throws SQLException;
    List<ReservationDto> findByZakaznikId(int zakaznikId) throws SQLException;
    List<ReservationDto> findAll() throws SQLException;
    void update(ReservationDto r) throws SQLException;
    void deleteById(int id) throws SQLException;
}