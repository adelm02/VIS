package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class BaseEntity {
    protected long createdAtEpochMs;
    protected long updatedAtEpochMs;
    private final List<String> validationErrors = new ArrayList<>();

    protected BaseEntity() {
        long now = System.currentTimeMillis();
        this.createdAtEpochMs = now;
        this.updatedAtEpochMs = now;
    }

    public void markUpdated() {
        this.updatedAtEpochMs = System.currentTimeMillis();
    }

    public long getCreatedAtEpochMs() { return createdAtEpochMs; }
    public long getUpdatedAtEpochMs() { return updatedAtEpochMs; }

    public final boolean isValid() {
        validationErrors.clear();
        validate();
        return validationErrors.isEmpty();
    }

    public List<String> getValidationErrors() {
        return Collections.unmodifiableList(validationErrors);
    }

    protected void addValidationError(String message) {
        validationErrors.add(message);
    }

    protected void validate() {
    }
}


