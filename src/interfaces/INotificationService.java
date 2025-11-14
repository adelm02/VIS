package interfaces;

public interface INotificationService {
    void sendReservationConfirmation(String email, int reservationId);
}


