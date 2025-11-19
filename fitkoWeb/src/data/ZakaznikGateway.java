package data;

import interfaces.IZakaznikRepository;

import java.sql.*;
import java.util.*;
import java.security.MessageDigest;

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

    // ========================================================================
    // PŘIHLÁŠENÍ S DEBUGEM
    // ========================================================================

    public ZakaznikDto login(String email, String password) throws SQLException {
        String hashedPassword = hashPassword(password);

        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║ 🔍 DEBUG LOGIN                                           ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("   📧 Email: " + email);
        System.out.println("   🔑 Zadané heslo: " + password);
        System.out.println("   🔐 Zadané heslo (hash): " + hashedPassword);

        String sql = "SELECT id, name, email, password, credit FROM Zakaznik WHERE email = ?";

        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");

                    System.out.println("   💾 Heslo v DB: " + storedPassword);
                    System.out.println("   ✔️  Shoduje se? " + hashedPassword.equals(storedPassword));
                    System.out.println("   📏 Délka hash (zadané): " + hashedPassword.length());
                    System.out.println("   📏 Délka hash (v DB):   " + storedPassword.length());

                    if (hashedPassword.equals(storedPassword)) {
                        ZakaznikDto dto = new ZakaznikDto();
                        dto.id = rs.getInt("id");
                        dto.name = rs.getString("name");
                        dto.email = rs.getString("email");
                        dto.setPassword(rs.getString("password"));
                        dto.credit = rs.getInt("credit");

                        System.out.println("   ✅ ÚSPĚCH: Přihlášen jako " + dto.name + " (Kredity: " + dto.credit + ")");
                        System.out.println("╚══════════════════════════════════════════════════════════╝\n");
                        return dto;
                    } else {
                        System.out.println("   ❌ CHYBA: Hash hesla se NESHODUJE!");
                        System.out.println("╚══════════════════════════════════════════════════════════╝\n");
                        return null;
                    }
                } else {
                    System.out.println("   ❌ CHYBA: Email nenalezen v databázi!");
                    System.out.println("╚══════════════════════════════════════════════════════════╝\n");
                    return null;
                }
            }
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Chyba při hashování hesla: " + e.getMessage());
        }
    }
}