package data;// ReservationUnitOfWork.java
import interfaces.IReservationRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservationUnitOfWork {
    private final List<ReservationDto> newReservations = new ArrayList<>();
    private final List<ReservationDto> modifiedReservations = new ArrayList<>();
    private final List<Integer> deletedReservationIds = new ArrayList<>();

    private final IReservationRepository repository;
    private boolean inTransaction = false;

    public ReservationUnitOfWork(IReservationRepository repository) {
        this.repository = repository;
    }

    public void begin() {
        inTransaction = true;
        newReservations.clear();
        modifiedReservations.clear();
        deletedReservationIds.clear();
    }

    public void registerNew(ReservationDto reservation) { ensure(); newReservations.add(reservation); }
    public void registerModified(ReservationDto reservation) { ensure(); modifiedReservations.add(reservation); }
    public void registerDeleted(int id) { ensure(); deletedReservationIds.add(id); }

    public void commit() throws SQLException {
        ensure();
        for (ReservationDto r : newReservations) repository.insert(r);
        for (ReservationDto r : modifiedReservations) repository.update(r);
        for (int id : deletedReservationIds) repository.deleteById(id);
        inTransaction = false;
    }

    public void rollback() {
        ensure();
        newReservations.clear();
        modifiedReservations.clear();
        deletedReservationIds.clear();
        inTransaction = false;
    }

    private void ensure() {
        if (!inTransaction) throw new IllegalStateException("No active transaction. Call begin first.");
    }
}