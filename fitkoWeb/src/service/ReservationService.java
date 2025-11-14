package service;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import data.*;
import domain.*;
import interfaces.*;
import util.AuditLogObserver;

public class ReservationService {
    private final ReservationUnitOfWork uow;
    private final INotificationService notificationService;
    private final IPaymentGateway paymentGateway;

    // Observer pattern - jednodušší verze
    private final List<IReservationObserver> observers = new ArrayList<>();

    // Strategy pattern - můžeme mít null = standardní cena
    private IPricingStrategy pricingStrategy = null;

    public ReservationService(ReservationUnitOfWork uow) {
        this(uow, new NotificationServiceStub(), new PaymentGatewayStub());
    }

    public ReservationService(ReservationUnitOfWork uow, INotificationService notificationService) {
        this(uow, notificationService, new PaymentGatewayStub());
    }

    public ReservationService(ReservationUnitOfWork uow, INotificationService notificationService, IPaymentGateway paymentGateway) {
        this.uow = uow;
        this.notificationService = notificationService;
        this.paymentGateway = paymentGateway;
    }

    // Observer - přidat pozorovatele
    public void addObserver(IReservationObserver observer) {
        observers.add(observer);
    }

    // Observer - notifikovat pozorovatele
    private void notifyObservers(ReservationDto reservation, String oldStatus, String newStatus) {
        for (IReservationObserver observer : observers) {
            observer.onReservationStatusChanged(reservation, oldStatus, newStatus);
        }
    }

    // Strategy - nastavit strategii (VOLITELNÉ)
    public void setPricingStrategy(IPricingStrategy strategy) {
        this.pricingStrategy = strategy;
    }

    public void createReservation(Zakaznik zakaznik, Lekce lekce, String datum) throws SQLException {
        if (zakaznik == null) zakaznik = NeznamyZakaznik.instance();

        // Strategy pattern - pokud není nastavena strategie, použij základní cenu
        int finalPrice;
        if (pricingStrategy != null) {
            finalPrice = pricingStrategy.calculatePrice(lekce.price.getAmountCzk(), zakaznik);
            System.out.println("Cena podle strategie: " + finalPrice + " CZK");
        } else {
            finalPrice = lekce.price.getAmountCzk();
        }

        boolean approved = paymentGateway.charge(zakaznik.id, finalPrice, "Reservation for lesson " + lekce.title);
        if (!approved) {
            System.out.println("Platba zamítnuta. Rezervace neprovedena.");
            return;
        }

        if (zakaznik.credit < finalPrice) {
            System.out.println("Insufficient credit for " + zakaznik.name);
            return;
        }

        Lekce targetLekce = (lekce.loggedIn >= lekce.capacity) ? new PlnaLekce(lekce) : lekce;

        zakaznik.credit -= finalPrice;
        targetLekce.registerCustomer(zakaznik);

        ReservationDto dto = new ReservationDto();
        // Singleton pattern
        dto.id = ReservationNumberManager.getInstance().getNextNumber();
        dto.zakaznikId = zakaznik.id;
        dto.lekceId = lekce.id;
        dto.datum = datum;
        dto.stav = "CEKA_NA_POTVRZENI";
        dto.rating = null;
        dto.review = "";

        uow.begin();
        uow.registerNew(dto);
        uow.commit();

        System.out.println("Reservation created: " + zakaznik.name + " for " + lekce.title + " (ID: " + dto.id + ")");
        notificationService.sendReservationConfirmation(zakaznik.email, dto.id);

        // Observer pattern - notifikace
        notifyObservers(dto, "NOVA", "CEKA_NA_POTVRZENI");
    }

    public void confirmReservation(ReservationDto dto) throws SQLException {
        String oldStatus = dto.stav;
        dto.stav = "POTVRZENA";
        uow.begin();
        uow.registerModified(dto);
        uow.commit();
        System.out.println("Reservation confirmed: " + dto.id);

        // Observer pattern
        notifyObservers(dto, oldStatus, "POTVRZENA");
    }

    public void cancelReservation(int id) throws SQLException {
        uow.begin();
        uow.registerDeleted(id);
        uow.commit();
        System.out.println("Reservation cancelled: " + id);
    }

    // Zbytek metod zůstává stejný...
    public void addRating(int reservationId, int rating, String review) throws SQLException {
        if (rating < 1 || rating > 5) {
            System.out.println("Rating must be 1..5 stars.");
            return;
        }

        IReservationRepository gw = new ReservationGateway();
        List<ReservationDto> all = gw.findAll();

        ReservationDto target = null;
        for (ReservationDto r : all) {
            if (r.id == reservationId) {
                target = r;
                break;
            }
        }
        if (target == null) {
            System.out.println("Reservation " + reservationId + " not found.");
            return;
        }

        if (!"POTVRZENA".equals(target.stav) && !"DOKONCENA".equals(target.stav)) {
            System.out.println("Reservation must be confirmed or finished before rating.");
            return;
        }
        if (target.rating != null) {
            System.out.println("Reservation already rated.");
            return;
        }

        target.rating = rating;
        target.review = (review == null ? "" : review);
        target.stav = "DOKONCENA";

        uow.begin();
        uow.registerModified(target);
        uow.commit();

        System.out.println("Rating saved for reservation " + reservationId + ": " + rating + "★");
    }

    public List<ReservationDto> getHistoryForCustomer(int customerId) throws SQLException {
        IReservationRepository gw = new ReservationGateway();
        List<ReservationDto> all = gw.findByZakaznikId(customerId);
        List<ReservationDto> done = new ArrayList<>();
        for (ReservationDto r : all) {
            if ("DOKONCENA".equals(r.stav)) {
                done.add(r);
            }
        }
        return done;
    }

    public List<ReservationDto> getRateableForCustomer(int customerId) throws SQLException {
        IReservationRepository gw = new ReservationGateway();
        List<ReservationDto> all = gw.findByZakaznikId(customerId);
        List<ReservationDto> rateable = new ArrayList<>();
        for (ReservationDto r : all) {
            if (("POTVRZENA".equals(r.stav) || "DOKONCENA".equals(r.stav)) && r.rating == null) {
                rateable.add(r);
            }
        }
        return rateable;
    }
}