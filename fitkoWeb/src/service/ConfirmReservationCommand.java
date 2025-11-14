package service;

import java.sql.SQLException;

import data.ReservationDto;
import interfaces.IReservationCommand;



public class ConfirmReservationCommand implements IReservationCommand {
    private final ReservationDto reservation;
    private final ReservationService service;

    public ConfirmReservationCommand(ReservationDto reservation, ReservationService service) {
        this.reservation = reservation;
        this.service = service;
    }

    @Override
    public void execute() throws SQLException {
        service.confirmReservation(reservation);
    }

    @Override
    public String getCommandName() {
        return "Potvrdit rezervaci";
    }
}