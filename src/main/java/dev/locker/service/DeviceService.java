package dev.locker.service;

import dev.locker.domain.Device;
import dev.locker.repo.DeviceRepository;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Service for device operations.
 */
@SuppressWarnings("ClassCanBeRecord")
public class DeviceService {
    private final DeviceRepository repo;

    /**
     * Create a new DeviceService with the given repository.
     *
     * @param repo the device repository
     */
    public DeviceService(DeviceRepository repo) {
        this.repo = repo;
    }

    /**
     * List all devices.
     *
     * @return an unmodifiable list of all devices
     */
    public List<Device> listAll() {
        return repo.findAll();
    }

    /**
     * Search devices by name or tags (case-insensitive). Empty query returns all.
     *
     * @param query the query string to search for
     * @return a list of matching devices
     */
    public List<Device> search(String query) {
        if (query == null || query.trim().isEmpty()) return listAll();
        String lower = query.toLowerCase(Locale.ROOT);
        return repo.findAll().stream().filter(d -> d.name().toLowerCase(Locale.ROOT).contains(lower)
                        || d.tags().stream().anyMatch(t -> t.toLowerCase(Locale.ROOT).contains(lower)))
                .collect(Collectors.toList());
    }
}
