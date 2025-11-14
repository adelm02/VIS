package data;

import interfaces.IManagerRepository;

import java.sql.*;
import java.util.*;

public class ManagerGateway implements IManagerRepository {

    @Override
    public void insert(ManagerDto m) throws SQLException {
        String sql = "INSERT INTO Manager(id,name,login,password,position) VALUES(?,?,?,?,?)";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, m.id);
            ps.setString(2, m.name);
            ps.setString(3, m.login);
            ps.setString(4, m.password);
            ps.setString(5, m.position);
            ps.executeUpdate();
        }
    }

    @Override
    public List<ManagerDto> findAll() throws SQLException {
        String sql = "SELECT id,name,login,password,position FROM Manager";
        List<ManagerDto> out = new ArrayList<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ManagerDto m = new ManagerDto();
                m.id = rs.getInt("id");
                m.name = rs.getString("name");
                m.login = rs.getString("login");
                m.password = rs.getString("password");
                m.position = rs.getString("position");
                out.add(m);
            }
        }
        return out;
    }

    @Override
    public void update(ManagerDto m) throws SQLException {
        String sql = "UPDATE Manager SET name=?,login=?,password=?,position=? WHERE id=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.name);
            ps.setString(2, m.login);
            ps.setString(3, m.password);
            ps.setString(4, m.position);
            ps.setInt(5, m.id);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteById(int id) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("DELETE FROM Manager WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}