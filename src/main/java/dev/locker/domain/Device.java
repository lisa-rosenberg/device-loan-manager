package dev.locker.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Domain device model.
 *
 * @param id unique device identifier
 * @param name human-readable device name
 * @param tags immutable set of tags associated with the device
 * @param condition current device condition
 * @param timesBorrowed number of times the device has been borrowed
 */
public record Device(String id, String name, Set<String> tags, Condition condition, int timesBorrowed) {
    @SuppressWarnings("unused")
    public enum Condition {GOOD, FAIR, POOR}

    @JsonCreator
    public Device(@JsonProperty("id") String id,
                  @JsonProperty("name") String name,
                  @JsonProperty("tags") Set<String> tags,
                  @JsonProperty("condition") Condition condition,
                  @JsonProperty("timesBorrowed") int timesBorrowed) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.tags = tags == null ? Collections.emptySet() : Set.copyOf(tags);
        this.condition = condition == null ? Condition.GOOD : condition;
        this.timesBorrowed = timesBorrowed;
    }

    /**
     * Return a new Device with incremented timesBorrowed.
     *
     * @return a new Device instance with timesBorrowed increased by one
     */
    public Device incrementTimesBorrowed() {
        return new Device(id, name, tags, condition, timesBorrowed + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Device device)) return false;
        return id.equals(device.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Device{" + "id='" + id + '\'' + ", name='" + name + '\'' + '}';
    }
}
