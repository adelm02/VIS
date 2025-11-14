package data;// RecepcniGateway.java
import interfaces.IRecepcniRepository;
import data.DB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecepcniGateway implements IRecepcniRepository {

    @Override
    public void insert(RecepcniDto r) throws SQLException {
        final String sql = "INSERT INTO Recepcni(id, name, login, password) VALUES(?, ?, ?, ?)";
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, r.id);
            ps.setString(2, r.name);
            ps.setString(3, r.login);
            ps.setString(4, r.password);
            ps.executeUpdate();
        }
    }

    @Override
    public List<RecepcniDto> findAll() throws SQLException {
        final String sql = "SELECT id, name, login, password FROM Recepcni";
        List<RecepcniDto> out = new ArrayList<>();
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                RecepcniDto r = new RecepcniDto();
                r.id = rs.getInt("id");
                r.name = rs.getString("name");
                r.login = rs.getString("login");
                r.password = rs.getString("password");
                out.add(r);
            }
        }
        return out;
    }

    @Override
    public RecepcniDto findById(int id) throws SQLException {
        final String sql = "SELECT id, name, login, password FROM Recepcni WHERE id = ?";
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                RecepcniDto r = new RecepcniDto();
                r.id = rs.getInt("id");
                r.name = rs.getString("name");
                r.login = rs.getString("login");
                r.password = rs.getString("password");
                return r;
            }
        }
    }

    @Override
    public void update(RecepcniDto r) throws SQLException {
        final String sql = "UPDATE Recepcni SET name = ?, login = ?, password = ? WHERE id = ?";
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.name);
            ps.setString(2, r.login);
            ps.setString(3, r.password);
            ps.setInt(4, r.id);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteById(int id) throws SQLException {
        final String sql = "DELETE FROM Recepcni WHERE id = ?";
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}