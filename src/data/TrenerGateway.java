package data;

import interfaces.ITrenerRepository;

import java.sql.*;
import java.util.*;

public class TrenerGateway implements ITrenerRepository {

    @Override
    public void insert(TrenerDto t) throws SQLException {
        String sql = """
            INSERT INTO Trener(id,name,email,password,speciality,clas)
            VALUES(?,?,?,?,?,?)
            ON CONFLICT(id) DO UPDATE SET
              name=excluded.name,
              email=excluded.email,
              password=excluded.password,
              speciality=excluded.speciality,
              clas=excluded.clas
        """;
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, t.id);
            ps.setString(2, t.name);
            ps.setString(3, t.email);
            ps.setString(4, t.password);
            ps.setString(5, t.speciality);
            ps.setString(6, t.clas);
            ps.executeUpdate();
        }
    }

    @Override
    public List<TrenerDto> findAll() throws SQLException {
        String sql = "SELECT id,name,email,password,speciality,clas FROM Trener";
        List<TrenerDto> out = new ArrayList<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TrenerDto t = new TrenerDto();
                t.id = rs.getInt("id");
                t.name = rs.getString("name");
                t.email = rs.getString("email");
                t.password = rs.getString("password");
                t.speciality = rs.getString("speciality");
                t.clas = rs.getString("clas");
                out.add(t);
            }
        }
        return out;
    }

    @Override
    public TrenerDto findById(int id) throws SQLException {
        String sql = "SELECT id,name,email,password,speciality,clas FROM Trener WHERE id=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                TrenerDto t = new TrenerDto();
                t.id = rs.getInt("id");
                t.name = rs.getString("name");
                t.email = rs.getString("email");
                t.password = rs.getString("password");
                t.speciality = rs.getString("speciality");
                t.clas = rs.getString("clas");
                return t;
            }
        }
    }

    @Override
    public void update(TrenerDto t) throws SQLException {
        String sql = "UPDATE Trener SET name=?,email=?,password=?,speciality=?,clas=? WHERE id=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, t.name);
            ps.setString(2, t.email);
            ps.setString(3, t.password);
            ps.setString(4, t.speciality);
            ps.setString(5, t.clas);
            ps.setInt(6, t.id);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteById(int id) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("DELETE FROM Trener WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}