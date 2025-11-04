package dev.locker.repo;

import dev.locker.domain.Device;

import java.util.List;
import java.util.Optional;

/**
 * Repository for devices.
 */
public interface DeviceRepository {
    /**
     * Return all devices stored in the repository.
     *
     * @return an unmodifiable list of devices
     */
    List<Device> findAll();

    /**
     * Find a device by id.
     *
     * @param id the device id
     * @return an Optional containing the device if found
     */
    Optional<Device> findById(String id);

    /**
     * Save or update the given device in the repository.
     *
     * @param device the device to save
     */
    void save(Device device);
}
