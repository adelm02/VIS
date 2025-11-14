package service;
import data.DB;

import java.sql.*;


//Singleton

public class ReservationNumberManager {
    private static ReservationNumberManager instance;
    private int lastNumber;

    private ReservationNumberManager() {
        loadLastNumber();
    }

    public static ReservationNumberManager getInstance() {
        if (instance == null) {
            instance = new ReservationNumberManager();
        }
        return instance;
    }

    private void loadLastNumber() {
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement("SELECT MAX(id) as maxId FROM Reservation")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                lastNumber = rs.getInt("maxId");
            } else {
                lastNumber = 0;
            }
        } catch (SQLException e) {
            System.out.println("Nelze načíst poslední číslo rezervace, začínám od 0");
            lastNumber = 0;
        }
    }

    public synchronized int getNextNumber() {
        lastNumber++;
        return lastNumber;
    }

    public int getCurrentNumber() {
        return lastNumber;
    }
}