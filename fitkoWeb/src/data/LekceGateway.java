package data;

import interfaces.ILekceRepository;

import java.sql.*;
import java.util.*;

public class LekceGateway implements ILekceRepository {

    @Override
    public void insert(LekceDto l) throws SQLException {
        String sql = """
            INSERT INTO Lekce(id,title,trainer,day,time,capacity,loggedIn,price)
            VALUES(?,?,?,?,?,?,?,?)
            ON CONFLICT(id) DO UPDATE SET
              title=excluded.title,
              trainer=excluded.trainer,
              day=excluded.day,
              time=excluded.time,
              capacity=excluded.capacity,
              loggedIn=excluded.loggedIn,
              price=excluded.price
        """;
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, l.id);
            ps.setString(2, l.title);
            ps.setString(3, l.trainer);
            ps.setString(4, l.day);
            ps.setString(5, l.time);
            ps.setInt(6, l.capacity);
            ps.setInt(7, l.loggedIn);
            ps.setInt(8, l.price);
            ps.executeUpdate();
        }
    }

    @Override
    public List<LekceDto> findAll() throws SQLException {
        String sql = "SELECT id,title,trainer,day,time,capacity,loggedIn,price FROM Lekce";
        List<LekceDto> out = new ArrayList<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LekceDto l = new LekceDto();
                l.id = rs.getInt("id");
                l.title = rs.getString("title");
                l.trainer = rs.getString("trainer");
                l.day = rs.getString("day");
                l.time = rs.getString("time");
                l.capacity = rs.getInt("capacity");
                l.loggedIn = rs.getInt("loggedIn");
                l.price = rs.getInt("price");
                out.add(l);
            }
        }
        return out;
    }

    @Override
    public void update(LekceDto l) throws SQLException {
        String sql = "UPDATE Lekce SET title=?,trainer=?,day=?,time=?,capacity=?,loggedIn=?,price=? WHERE id=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, l.title);
            ps.setString(2, l.trainer);
            ps.setString(3, l.day);
            ps.setString(4, l.time);
            ps.setInt(5, l.capacity);
            ps.setInt(6, l.loggedIn);
            ps.setInt(7, l.price);
            ps.setInt(8, l.id);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteById(int id) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("DELETE FROM Lekce WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}