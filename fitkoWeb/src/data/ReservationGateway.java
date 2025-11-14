package data;// ReservationGateway.java
import interfaces.IReservationRepository;

import java.sql.*;
import java.util.*;

public class ReservationGateway implements IReservationRepository {

    @Override
    public void insert(ReservationDto r) throws SQLException {
        String sql = "INSERT INTO Reservation(id,zakaznikId,lekceId,datum,stav,rating,review) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, r.id);
            ps.setInt(2, r.zakaznikId);
            ps.setInt(3, r.lekceId);
            ps.setString(4, r.datum);
            ps.setString(5, r.stav);
            if (r.rating == null)
                ps.setNull(6, Types.INTEGER);
            else
                ps.setInt(6, r.rating);

            if (r.review == null)
                ps.setNull(7, Types.VARCHAR);
            else
                ps.setString(7, r.review);

            ps.executeUpdate();
        }
    }

    @Override
    public List<ReservationDto> findAll() throws SQLException {
        String sql = "SELECT id,zakaznikId,lekceId,datum,stav,rating,review FROM Reservation";
        List<ReservationDto> out = new ArrayList<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ReservationDto r = new ReservationDto();
                r.id = rs.getInt("id");
                r.zakaznikId = rs.getInt("zakaznikId");
                r.lekceId = rs.getInt("lekceId");
                r.datum = rs.getString("datum");
                r.stav = rs.getString("stav");
                int rate = rs.getInt("rating");
                r.rating = rs.wasNull() ? null : rate;
                r.review = rs.getString("review"); // může být null
                out.add(r);
            }
        }
        return out;
    }

    @Override
    public List<ReservationDto> findByZakaznikId(int zakaznikId) throws SQLException {
        String sql = "SELECT id,zakaznikId,lekceId,datum,stav,rating,review FROM Reservation WHERE zakaznikId=?";
        List<ReservationDto> out = new ArrayList<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, zakaznikId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReservationDto r = new ReservationDto();
                    r.id = rs.getInt("id");
                    r.zakaznikId = rs.getInt("zakaznikId");
                    r.lekceId = rs.getInt("lekceId");
                    r.datum = rs.getString("datum");
                    r.stav = rs.getString("stav");
                    int rate = rs.getInt("rating");
                    r.rating = rs.wasNull() ? null : rate;
                    r.review = rs.getString("review"); // může být null
                    out.add(r);
                }
            }
        }
        return out;
    }

    @Override
    public void update(ReservationDto r) throws SQLException {
        String sql = "UPDATE Reservation SET zakaznikId=?,lekceId=?,datum=?,stav=?,rating=?,review=? WHERE id=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, r.zakaznikId);
            ps.setInt(2, r.lekceId);
            ps.setString(3, r.datum);
            ps.setString(4, r.stav);

            if (r.rating == null)
                ps.setNull(5, Types.INTEGER);
            else
                ps.setInt(5, r.rating);

            if (r.review == null)
                ps.setNull(6, Types.VARCHAR);
            else
                ps.setString(6, r.review);

            ps.setInt(7, r.id);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteById(int id) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                "DELETE FROM Reservation WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}