package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportGateway {

    public List<LessonCountRow> reservationsPerLesson() throws SQLException {
        String sql = """
            SELECT l.id        AS lekceId,
                   l.title     AS title,
                   COUNT(r.id) AS cnt
            FROM Lekce l
            LEFT JOIN Reservation r ON r.lekceId = l.id
            GROUP BY l.id, l.title
            ORDER BY cnt DESC, l.title
        """;
        List<LessonCountRow> out = new ArrayList<>();
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LessonCountRow row = new LessonCountRow();
                row.lekceId = rs.getInt("lekceId");
                row.title = rs.getString("title");
                row.count = rs.getInt("cnt");
                out.add(row);
            }
        }
        return out;
    }

    public List<LessonRatingRow> avgRatingPerLesson() throws SQLException {
        String sql = """
            SELECT l.id                         AS lekceId,
                   l.title                      AS title,
                   ROUND(AVG(r.rating), 2)      AS avg_rating,
                   COUNT(r.rating)              AS n
            FROM Lekce l
            JOIN Reservation r ON r.lekceId = l.id
            WHERE r.rating IS NOT NULL
            GROUP BY l.id, l.title
            ORDER BY avg_rating DESC, n DESC, l.title
        """;
        List<LessonRatingRow> out = new ArrayList<>();
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LessonRatingRow row = new LessonRatingRow();
                row.lekceId = rs.getInt("lekceId");
                row.title = rs.getString("title");
                row.avgRating = rs.getDouble("avg_rating");
                row.count = rs.getInt("n");
                out.add(row);
            }
        }
        return out;
    }

    // 3) history of leesons
    public List<CustomerReservationRow> reservationsForCustomer(int customerId) throws SQLException {
        String sql = """
            SELECT r.id     AS resId,
                   l.title  AS title,
                   r.datum  AS datum,
                   r.stav   AS stav,
                   r.rating AS rating,
                   r.review AS review
            FROM Reservation r
            JOIN Lekce l ON l.id = r.lekceId
            WHERE r.zakaznikId = ?
            ORDER BY r.datum
        """;
        List<CustomerReservationRow> out = new ArrayList<>();
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CustomerReservationRow row = new CustomerReservationRow();
                    row.reservationId = rs.getInt("resId");
                    row.title = rs.getString("title");
                    row.datum = rs.getString("datum");
                    row.stav = rs.getString("stav");
                    int r = rs.getInt("rating");
                    row.rating = rs.wasNull() ? null : r;
                    row.review = rs.getString("review");
                    out.add(row);
                }
            }
        }
        return out;
    }

    //DTO for return
    public static class LessonCountRow {
        public int lekceId;
        public String title;
        public int count;
    }
    public static class LessonRatingRow {
        public int lekceId;
        public String title;
        public double avgRating;
        public int count;
    }
    public static class CustomerReservationRow {
        public int reservationId;
        public String title;
        public String datum;
        public String stav;
        public Integer rating;
        public String review;
    }
}