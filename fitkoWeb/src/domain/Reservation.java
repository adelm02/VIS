package domain;

public class Reservation extends BaseEntity {
    public enum ReservationStatus { CEKA_NA_POTVRZENI, POTVRZENA, ZRUSENA, DOKONCENA }

    public int id;
    public Zakaznik zakaznik;
    public Lekce lekce;
    public String datum;
    public ReservationStatus stav;

    public Integer rating;   // 1..5
    public String review;

    public Reservation(int id, Zakaznik zakaznik, Lekce lekce, String datum) {
        super();
        this.id = id;
        this.zakaznik = zakaznik;
        this.lekce = lekce;
        this.datum = datum;
        this.stav = ReservationStatus.CEKA_NA_POTVRZENI;
    }

    public void submit()  { this.stav = ReservationStatus.POTVRZENA; markUpdated(); }
    public void abort()   { this.stav = ReservationStatus.ZRUSENA; markUpdated(); }
    public void finale()  { this.stav = ReservationStatus.DOKONCENA; markUpdated(); }

    public void rate(int rating, String review) {
        this.rating = rating;
        this.review = review;
        this.stav   = ReservationStatus.DOKONCENA;
        markUpdated();
    }
}