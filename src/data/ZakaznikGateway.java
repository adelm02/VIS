package data;

import interfaces.IZakaznikRepository;

import java.sql.*;
import java.util.*;

public class ZakaznikGateway implements IZakaznikRepository {

    @Override
    public void insert(ZakaznikDto z) throws SQLException {
        String sql = """
            INSERT INTO Zakaznik(id, name, email, password, credit)
            VALUES(?,?,?,?,?)
            ON CONFLICT(id) DO UPDATE SET
              name=excluded.name,
              email=excluded.email,
              password=excluded.password,
              credit=excluded.credit
        """;
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, z.id);
            ps.setString(2, z.name);
            ps.setString(3, z.email);
            ps.setString(4, z.getPassword());
            ps.setInt(5, z.credit);
            ps.executeUpdate();
        }
    }

    @Override
    public List<ZakaznikDto> findAll() throws SQLException {
        String sql = "SELECT id,name,email,password,credit FROM Zakaznik";
        List<ZakaznikDto> out = new ArrayList<>();
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ZakaznikDto z = new ZakaznikDto();
                z.id = rs.getInt("id");
                z.name = rs.getString("name");
                z.email = rs.getString("email");
                z.setPassword(rs.getString("password"));
                z.credit = rs.getInt("credit");
                out.add(z);
            }
        }
        return out;
    }

    @Override
    public ZakaznikDto findById(int id) throws SQLException {
        String sql = "SELECT id,name,email,password,credit FROM Zakaznik WHERE id=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                ZakaznikDto z = new ZakaznikDto();
                z.id = rs.getInt("id");
                z.name = rs.getString("name");
                z.email = rs.getString("email");
                z.setPassword(rs.getString("password"));
                z.credit = rs.getInt("credit");
                return z;
            }
        }
    }

    @Override
    public void update(ZakaznikDto z) throws SQLException {
        String sql = "UPDATE Zakaznik SET name=?,email=?,password=?,credit=? WHERE id=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, z.name);
            ps.setString(2, z.email);
            ps.setString(3, z.getPassword());
            ps.setInt(4, z.credit);
            ps.setInt(5, z.id);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteById(int id) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("DELETE FROM Zakaznik WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}