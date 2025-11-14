package domain;

import data.ReservationDto;
import data.ReservationGateway;

import java.sql.SQLException;
import java.util.List;

public class osobniZakaznik extends Zakaznik {
    public String jmeno;
    public String prijmeni;
    public String datumNarozeni;

    // Lazy load proměnné
    private List<ReservationDto> rezervace;
    private boolean rezervaceNactene = false;

    public osobniZakaznik(int id, String name, String email, int credit,
                          String jmeno, String prijmeni, String datumNarozeni) {
        super(id, name, email, credit);
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.datumNarozeni = datumNarozeni;
    }

    // Lazy load metoda pro rezervace
    public List<ReservationDto> getRezervace() throws SQLException {  // ← bylo List<Reservation>
        if (!rezervaceNactene) {
            ReservationGateway gw = new ReservationGateway();
            rezervace = gw.findByZakaznikId(this.id); // vrací List<ReservationDto>
            rezervaceNactene = true;
        }
        return rezervace;
    }

    @Override
    public String toString() {
        return jmeno + " " + prijmeni + " (" + email + ")";
    }
}