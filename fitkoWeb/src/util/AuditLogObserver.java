package util;

import data.ReservationDto;
import interfaces.IReservationObserver;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//Observer
public class AuditLogObserver implements IReservationObserver {

    @Override
    public void onReservationStatusChanged(ReservationDto reservation, String oldStatus, String newStatus) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("[AUDIT] " + timestamp + " | Rezervace #" + reservation.id +
                " | " + oldStatus + " → " + newStatus);
    }
}