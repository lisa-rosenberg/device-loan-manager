package dev.locker.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Objects;

/**
 * Loan record. Identity is defined by deviceId + borrowedAt.
 *
 * @param deviceId   the identifier of the device being loaned
 * @param userId     the identifier of the user who borrowed the device
 * @param borrowedAt the timestamp when the device was borrowed
 * @param dueAt      the timestamp when the device is due to be returned
 * @param returnedAt the timestamp when the device was returned; may be null if not yet returned
 */
public record Loan(String deviceId, String userId, Instant borrowedAt, Instant dueAt, Instant returnedAt) {
    @JsonCreator
    public Loan(@JsonProperty("deviceId") String deviceId,
                @JsonProperty("userId") String userId,
                @JsonProperty("borrowedAt") Instant borrowedAt,
                @JsonProperty("dueAt") Instant dueAt,
                @JsonProperty("returnedAt") Instant returnedAt) {
        this.deviceId = Objects.requireNonNull(deviceId);
        this.userId = Objects.requireNonNull(userId);
        this.borrowedAt = Objects.requireNonNull(borrowedAt);
        this.dueAt = Objects.requireNonNull(dueAt);
        this.returnedAt = returnedAt;
    }

    /**
     * Returns a copy of this loan with the given returnedAt timestamp.
     *
     * @param returnedAt the returned at timestamp
     * @return a copy of this loan with the given returnedAt timestamp
     */
    public Loan withReturnedAt(Instant returnedAt) {
        return new Loan(deviceId, userId, borrowedAt, dueAt, returnedAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Loan loan)) return false;
        return deviceId.equals(loan.deviceId) && borrowedAt.equals(loan.borrowedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, borrowedAt);
    }

    @Override
    public String toString() {
        return "Loan{" + "deviceId='" + deviceId + '\'' + ", userId='" + userId + '\'' + ", borrowedAt=" + borrowedAt + '}';
    }
}
