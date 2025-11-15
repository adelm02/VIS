package data;

import java.sql.*;

public class DB {
    private static final String URL = "jdbc:sqlite:fitko.db";

    public static Connection get() throws SQLException {
        Connection c = DriverManager.getConnection(URL);
        try (Statement s = c.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON");
        }
        return c;
    }

    // zavolej při startu
    public static void initSchema() throws SQLException {
        try (Connection c = get(); Statement s = c.createStatement()) {

            s.execute("""
                CREATE TABLE IF NOT EXISTS Zakaznik(
                    id       INTEGER PRIMARY KEY,
                    name     TEXT    NOT NULL,
                    email    TEXT    NOT NULL,
                    password TEXT    NOT NULL,
                    credit   INTEGER NOT NULL
                );
            """);

            s.execute("""
                CREATE TABLE IF NOT EXISTS Trener(
                    id         INTEGER PRIMARY KEY,
                    name       TEXT NOT NULL,
                    email      TEXT NOT NULL,
                    password   TEXT NOT NULL,
                    speciality TEXT NOT NULL,
                    clas       TEXT NOT NULL
                );
            """);

            s.execute("""
                CREATE TABLE IF NOT EXISTS Recepcni(
                    id       INTEGER PRIMARY KEY,
                    name     TEXT NOT NULL,
                    login    TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL
                );
            """);

            s.execute("""
                CREATE TABLE IF NOT EXISTS Manager(
                    id       INTEGER PRIMARY KEY,
                    name     TEXT NOT NULL,
                    login    TEXT NOT NULL,
                    password TEXT NOT NULL,
                    position TEXT NOT NULL
                );
            """);

            s.execute("""
                CREATE TABLE IF NOT EXISTS Lekce(
                    id        INTEGER PRIMARY KEY,
                    title     TEXT NOT NULL,
                    trainer   TEXT NOT NULL,   -- držíme TEXT (jméno), ať nemusíš měnit DTO
                    day       TEXT NOT NULL,
                    time      TEXT NOT NULL,
                    capacity  INTEGER NOT NULL,
                    loggedIn  INTEGER NOT NULL,
                    price     INTEGER NOT NULL
                );
            """);

            s.execute("""
                CREATE TABLE IF NOT EXISTS Reservation(
                    id          INTEGER PRIMARY KEY,
                    zakaznikId  INTEGER NOT NULL,
                    lekceId     INTEGER NOT NULL,
                    datum       TEXT    NOT NULL,
                    stav        TEXT    NOT NULL,
                    rating      INTEGER NULL,
                    review      TEXT    NULL,
                    FOREIGN KEY (zakaznikId) REFERENCES Zakaznik(id) ON DELETE CASCADE,
                    FOREIGN KEY (lekceId)    REFERENCES Lekce(id)    ON DELETE CASCADE
                );
            """);

            // --- Lightweight migrace pro staré DB soubory ---
            if (!columnExists(c, "Reservation", "rating")) {
                s.execute("ALTER TABLE Reservation ADD COLUMN rating INTEGER");
            }
            if (!columnExists(c, "Reservation", "review")) {
                s.execute("ALTER TABLE Reservation ADD COLUMN review TEXT");
            }
        }
    }

    private static boolean columnExists(Connection c, String table, String column) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("PRAGMA table_info(" + table + ")");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                if (column.equalsIgnoreCase(rs.getString("name"))) return true;
            }
            return false;
        }
    }

    // pro čistý start v demo/scénářích
    public static void clearAll() throws SQLException {
        try (Connection c = get(); Statement s = c.createStatement()) {
            s.executeUpdate("DELETE FROM Reservation");
            s.executeUpdate("DELETE FROM Lekce");
            s.executeUpdate("DELETE FROM Trener");
            s.executeUpdate("DELETE FROM Recepcni");
            s.executeUpdate("DELETE FROM Manager");
            s.executeUpdate("DELETE FROM Zakaznik");
        }
    }

    // vloží základní data, která používáš v Main (IDs držíme konzistentní)
    public static void seed() throws SQLException {
        try (Connection c = get()) {
            // Zakaznici
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO Zakaznik(id,name,email,password,credit) VALUES(?,?,?,?,?)")) {
                ps.setInt(1, 1); ps.setString(2, "Anna Novaková"); ps.setString(3, "anna@fit.cz");
                ps.setString(4, "pwd"); ps.setInt(5, 500); ps.executeUpdate();

                ps.setInt(1, 2); ps.setString(2, "Marek Malý"); ps.setString(3, "marek@fit.cz");
                ps.setString(4, "pwd"); ps.setInt(5, 80); ps.executeUpdate();
            }

            // Trener
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO Trener(id,name,email,password,speciality,clas) VALUES(?,?,?,?,?,?)")) {
                ps.setInt(1, 1);
                ps.setString(2, "Petr Novák");
                ps.setString(3, "petr@fit.cz");
                ps.setString(4, "abc123");
                ps.setString(5, "Crossfit");
                ps.setString(6, "Pondělí–Středa–Pátek");
                ps.executeUpdate();

                // Trener 2
                ps.setInt(1, 2);
                ps.setString(2, "Jana Dvořáková");
                ps.setString(3, "jana@fit.cz");
                ps.setString(4, "fit123");
                ps.setString(5, "Pilates");
                ps.setString(6, "Úterý–Čtvrtek–Sobota");
                ps.executeUpdate();

                // Trener 3
                ps.setInt(1, 3);
                ps.setString(2, "Tomáš Král");
                ps.setString(3, "tomas@fit.cz");
                ps.setString(4, "gympass");
                ps.setString(5, "Kruhový trénink");
                ps.setString(6, "Pondělí–Středa–Pátek");
                ps.executeUpdate();

                // Trener 4
                ps.setInt(1, 4);
                ps.setString(2, "Lucie Benešová");
                ps.setString(3, "lucie@fit.cz");
                ps.setString(4, "yoga2024");
                ps.setString(5, "Jóga");
                ps.setString(6, "Pondělí–Čtvrtek–Neděle");
                ps.executeUpdate();

                // Trener 5
                ps.setInt(1, 5);
                ps.setString(2, "Martin Svoboda");
                ps.setString(3, "martin@fit.cz");
                ps.setString(4, "runfit");
                ps.setString(5, "Kickbox");
                ps.setString(6, "Úterý–Pátek–Neděle");
                ps.executeUpdate();
            }


        // Lekce - PŘIDEJTE VÍCE s různými trenéry
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO Lekce(id,title,trainer,day,time,capacity,loggedIn,price) VALUES(?,?,?,?,?,?,?,?)")) {

                // l1: Crossfit - Petr Novák
                ps.setInt(1, 1); ps.setString(2, "Crossfit"); ps.setString(3, "Petr Novák");
                ps.setString(4, "Monday"); ps.setString(5, "10:00");
                ps.setInt(6, 10); ps.setInt(7, 0); ps.setInt(8, 150); ps.executeUpdate();

                // l2: Yoga - Petr Novák
                ps.setInt(1, 2); ps.setString(2, "Yoga"); ps.setString(3, "Petr Novák");
                ps.setString(4, "Monday"); ps.setString(5, "12:00");
                ps.setInt(6, 1); ps.setInt(7, 0); ps.setInt(8, 30); ps.executeUpdate();

                // l3: Pilates - Jana Dvořáková
                ps.setInt(1, 3); ps.setString(2, "Pilates"); ps.setString(3, "Jana Dvořáková");
                ps.setString(4, "Tuesday"); ps.setString(5, "09:00");
                ps.setInt(6, 15); ps.setInt(7, 0); ps.setInt(8, 120); ps.executeUpdate();

                // l4: Kruhový trénink - Tomáš Král
                ps.setInt(1, 4); ps.setString(2, "Kruhový trénink"); ps.setString(3, "Tomáš Král");
                ps.setString(4, "Wednesday"); ps.setString(5, "18:00");
                ps.setInt(6, 12); ps.setInt(7, 0); ps.setInt(8, 100); ps.executeUpdate();

                // l5: Ranní jóga - Lucie Benešová
                ps.setInt(1, 5); ps.setString(2, "Ranní jóga"); ps.setString(3, "Lucie Benešová");
                ps.setString(4, "Thursday"); ps.setString(5, "07:00");
                ps.setInt(6, 20); ps.setInt(7, 0); ps.setInt(8, 80); ps.executeUpdate();

                // l6: Kickbox - Martin Svoboda
                ps.setInt(1, 6); ps.setString(2, "Kickbox"); ps.setString(3, "Martin Svoboda");
                ps.setString(4, "Friday"); ps.setString(5, "19:00");
                ps.setInt(6, 8); ps.setInt(7, 0); ps.setInt(8, 180); ps.executeUpdate();

                // l7: Večerní Pilates - Jana Dvořáková
                ps.setInt(1, 7); ps.setString(2, "Večerní Pilates"); ps.setString(3, "Jana Dvořáková");
                ps.setString(4, "Thursday"); ps.setString(5, "20:00");
                ps.setInt(6, 15); ps.setInt(7, 0); ps.setInt(8, 120); ps.executeUpdate();
            }

            // Recepcni
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO Recepcni(id,name,login,password) VALUES(?,?,?,?)")) {
                ps.setInt(1, 1); ps.setString(2, "Tereza Novotná");
                ps.setString(3, "tereza"); ps.setString(4, "pass123"); ps.executeUpdate();
            }

            // Manager
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO Manager(id,name,login,password,position) VALUES(?,?,?,?,?)")) {
                ps.setInt(1, 1); ps.setString(2, "Lucie Horáková");
                ps.setString(3, "lucie"); ps.setString(4, "admin123");
                ps.setString(5, "General Manager"); ps.executeUpdate();
            }
        }
    }
}