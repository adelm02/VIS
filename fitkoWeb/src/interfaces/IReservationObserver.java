package interfaces;//Observer

import data.ReservationDto;

public interface IReservationObserver {
    void onReservationStatusChanged(ReservationDto reservation, String oldStatus, String newStatus);
}