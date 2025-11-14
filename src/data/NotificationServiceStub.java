package data;

import interfaces.INotificationService;

public class NotificationServiceStub implements INotificationService {
    @Override
    public void sendReservationConfirmation(String email, int reservationId) {
        System.out.println("[STUB] Confirmation email sent to " + email + " for reservation " + reservationId);
    }
}


